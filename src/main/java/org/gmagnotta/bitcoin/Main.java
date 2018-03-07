package org.gmagnotta.bitcoin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.blockchain.BlockChainSQLiteImpl;
import org.gmagnotta.bitcoin.peer.BitcoinPeerManager;
import org.gmagnotta.bitcoin.peer.BitcoinPeerManagerImpl;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.log.LogEventWriter;
import org.gmagnotta.log.LogLevel;
import org.gmagnotta.log.impl.system.ConsoleLogEventWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		
		org.gmagnotta.log.LogEventCollector.getInstance().setLogLevelThreshold(LogLevel.INFO);
		
		org.gmagnotta.log.LogEventCollector.getInstance().addLogEventWriter(new ConsoleLogEventWriter());
		
		final MagicVersion magicVersion;
		
		if (args.length > 0) {
		
			if ("testnet3".equals(args[0])) {
			
				magicVersion = MagicVersion.TESTNET3;
					
			} else if ("mainnet".equals(args[0])) {
				
				magicVersion = MagicVersion.MAINNET;
				
			} else if ("regtest".equals(args[0])) {
				
				magicVersion = MagicVersion.REGTEST;
				
			} else {
				
				magicVersion = null;
				
				LOGGER.info("Unknown network type {}", args[0]);
				
				System.exit(-1);
				
			}
		
		} else {
			
			magicVersion = null;
			
			LOGGER.info("Please specify network type!");
			
			System.exit(-1);
			
		}
		
		final TransactionAwareBasicDataSource dataSource = new TransactionAwareBasicDataSource();

		dataSource.setDriverClassName("org.sqlite.JDBC");
		
		dataSource.addConnectionProperty("foreign_keys", "ON");
		dataSource.addConnectionProperty("journal_mode", "WAL");
		dataSource.addConnectionProperty("transaction_mode", "IMMEDIATE");
		dataSource.addConnectionProperty("busy_timeout", "0");
		
		dataSource.setDefaultAutoCommit(true);

		dataSource.setUrl("jdbc:sqlite:" + magicVersion.toString() + ".db");

		Connection connection = dataSource.getConnection();
		
		if (!tableExist(connection, "blockHeader")) {
			
			LOGGER.info("Found empty database! Creating needed tables");
			
			initDb(connection);
			
		}
		
		connection.close();
		
		BlockChain blockChain = new BlockChainSQLiteImpl(magicVersion.getBlockChainParameters(), dataSource);
		
		final BitcoinPeerManager bitcoinPeerManager = new BitcoinPeerManagerImpl(magicVersion, blockChain, 1);
		
		bitcoinPeerManager.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			public void run() {

				System.out.println("Terminating simulator...");

				try {
					
					try {
						System.out.println("Stop sync");
						bitcoinPeerManager.stop();
					} catch (Exception ex) {
						//
					}
					
					// tell the library to shutdown and close all opened resources
					System.out.println("Closing datasource");
					dataSource.close();

					// explicitly stop logging
					for (LogEventWriter writer : org.gmagnotta.log.LogEventCollector.getInstance().getLogEventWriters()) {

						writer.stop();

					}
					
					org.gmagnotta.log.LogEventCollector.getInstance().stop();

				} catch (Exception ex) {

					System.err.println("Exception while terminating simulator " + ex.getMessage());

				}
			}
		});
		
	}
	
	protected static void initDb(Connection connection) throws Exception {
		
		Statement statement = connection.createStatement();
		
		statement.executeUpdate(BlockChainSQLiteImpl.CREATE_HEADER_TABLE);
		
		statement.executeUpdate(BlockChainSQLiteImpl.CREATE_BESTCHAIN_VIEW);
		
		statement.close();
		
	}
	
	/**
	 * This is an utility method that checks if a table exists in the database
	 * @param conn
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private static boolean tableExist(Connection conn, String tableName) throws SQLException {
		
		boolean tExists = false;
		
		ResultSet rs = null;
		
		try {
			rs = conn.getMetaData().getTables(null, null, tableName, null);
			
			while (rs.next()) {
				String tName = rs.getString("TABLE_NAME");
				if (tName != null && tName.equals(tableName)) {
					tExists = true;
					break;
				}
			}
		} finally {
			
			if (rs != null) {
				rs.close();
			}
			
		}
		return tExists;
	}
	
}
