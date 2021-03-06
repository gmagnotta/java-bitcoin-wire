package org.gmagnotta.bitcoin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.dbcp2.BasicDataSource;
import org.gmagnotta.bitcoin.blockchain.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionAwareBasicDataSource extends BasicDataSource implements TransactionManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionAwareBasicDataSource.class);
	
	private static final ThreadLocal<TransactionAwareConnection> context = new ThreadLocal<TransactionAwareConnection>();
	
	// This will prevent multiple writer to be active
	private final Lock writerLock;
	
	public TransactionAwareBasicDataSource() {
		super();
		
		this.writerLock = new ReentrantLock();
	}

	@Override
	public void startTransaction() throws Exception {
		
		LOGGER.debug("Starting transaction " + Thread.currentThread().getName());

		try {
			
			writerLock.lock();

			Connection connection = context.get();
			
			if (connection != null) {
				
				LOGGER.error("A transaction is already in progress for " + Thread.currentThread().getName());
				
				throw new Exception("Transaction already in progress");
				
			}

			connection = super.getConnection();
			connection.setAutoCommit(false);

			context.set(new TransactionAwareConnection(connection, Thread.currentThread()));

		} catch (Exception ex) {
			
			writerLock.unlock();

			throw new Exception("Exception", ex);

		}
		
	}

	@Override
	public void commitTransaction() throws Exception {

		LOGGER.debug("Committing transaction " + Thread.currentThread().getName());

		TransactionAwareConnection connection = context.get();
		
		if (connection == null) {
			
			LOGGER.error("No transaction found for " + Thread.currentThread().getName());
			
			throw new Exception("Connection is null!");
			
		}

		try {
			
			connection.commit();

			connection.setAutoCommit(true);

		} catch (Exception ex) {

			throw new Exception("Exception", ex);

		} finally {
			
			try {
				
				connection.close();
			
			} catch (SQLException e) {
				
				LOGGER.error("Exception closing connection", e);
				
			}
			
			writerLock.unlock();
		
			// remove wrapper!
			context.remove();

		}
		
	}

	@Override
	public void rollbackTransaction() throws Exception {
		
		LOGGER.debug("Rollbacking transaction " + Thread.currentThread().getName());

		TransactionAwareConnection connection = context.get();
		
		if (connection == null) {
			
			LOGGER.error("No transaction found for " + Thread.currentThread().getName());
			
			throw new Exception("Connection is null!");
			
		}

		try {
			
			connection.rollback();

			connection.setAutoCommit(true);

		} catch (Exception ex) {

			throw new Exception("Exception", ex);

		} finally {
			
			try {
				
				connection.close();
			
			} catch (SQLException e) {
				
				LOGGER.error("Exception closing connection", e);
				
			}
			
			writerLock.unlock();
		
			// remove wrapper!
			context.remove();

		}
		
	}

	@Override
	public boolean insideTransaction() {

		return (context.get() != null);

	}

	@Override
	public Connection getConnection() throws SQLException {

		Connection connection = context.get();

		if (connection == null) {

			connection = super.getConnection();

		}

		return connection;

	}

	
}
