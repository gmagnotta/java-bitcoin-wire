package org.gmagnotta.bitcoin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.dbcp2.BasicDataSource;
import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.blockchain.BlockChainSQLiteImpl;
import org.gmagnotta.bitcoin.peer.BitcoinPeer;
import org.gmagnotta.bitcoin.peer.BitcoinPeerManager;
import org.gmagnotta.bitcoin.peer.BitcoinPeerManagerImpl;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.log.LogLevel;
import org.gmagnotta.log.impl.system.ConsoleLogEventWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		
		org.gmagnotta.log.LogEventCollector.getInstance().setLogLevelThreshold(LogLevel.DEBUG);
		
		org.gmagnotta.log.LogEventCollector.getInstance().addLogEventWriter(new ConsoleLogEventWriter());
		
		MagicVersion magicVersion = null;
		
		if (args.length > 0) {
		
			if ("testnet3".equals(args[0])) {
			
				magicVersion = MagicVersion.TESTNET3;
					
			} else if ("mainnet".equals(args[0])) {
				
				magicVersion = MagicVersion.MAINNET;
				
			} else if ("regtest".equals(args[0])) {
				
				magicVersion = MagicVersion.REGTEST;
				
			} else {
				
				LOGGER.info("Unknown network type {}", args[0]);
				
				System.exit(-1);
				
			}
		
		} else {
			
			LOGGER.info("Please specify network type!");
			
			System.exit(-1);
			
		}
		
		BasicDataSource dataSource = new BasicDataSource();

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
		
		//
		
//		BasicDataSource dataSource2 = new BasicDataSource();
//
//		dataSource2.setDriverClassName("org.sqlite.JDBC");
//		
//		dataSource2.addConnectionProperty("foreign_keys", "ON");
//		dataSource2.addConnectionProperty("journal_mode", "WAL");
//		dataSource2.addConnectionProperty("transaction_mode", "IMMEDIATE");
//		dataSource2.addConnectionProperty("busy_timeout", "0");
//		
//		dataSource2.setDefaultAutoCommit(true);
//
//		dataSource2.setUrl("jdbc:sqlite:regtest2.db");
//
//		Connection connection2 = dataSource2.getConnection();
//		
//		if (!tableExist(connection2, "blockHeader")) {
//			
////			LOGGER.info("Found empty database! Creating needed tables");
//			
//			initDb(connection2);
//			
//		}
//		
//		connection2.close();
//		
//		BlockChain bestChain2 = new BlockChainSQLiteImpl(magicVersion.getBlockChainParameters(), dataSource2);
//		
//		//
		
		final BitcoinPeerManager bitcoinPeerManager = new BitcoinPeerManagerImpl(magicVersion, blockChain);
		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				try {
//					bitcoinPeerManager.listen(4000);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			
//		}, "bitcoinPeerManagerListener").start();
//		
//		
//		final BitcoinPeerManager bitcoinPeerManager2 = new BitcoinPeerManagerImpl(magicVersion, bestChain2);
		
//		bitcoinPeerManager2.connect("127.0.0.1", 4000);
		
//		bitcoinPeerManager.connect("surricani.chickenkiller.com", 18333);
		
//		bitcoinPeerManager.connect("seed.bitcoin.jonasschnelli.ch", 8333);
//		
//		bitcoinPeerManager.connect("seed.bitcoin.sipa.be", 8333);
//		
//		bitcoinPeerManager.connect("seed.btc.petertodd.org", 8333);
		
		for (String seed : magicVersion.getBlockChainParameters().getSeeds()) {
			
			LOGGER.info("Connecting to {}", seed);
			
			bitcoinPeerManager.connect(seed, magicVersion.getBlockChainParameters().getPort());
			
		}
		
		Timer inputTimer = new Timer();
		inputTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {

				List<BitcoinPeer> peers = bitcoinPeerManager.getConnectedPeers();
				
				if (peers.size() > 1) {
				
					int randomElement = ThreadLocalRandom.current().nextInt(peers.size());
					
						try {
							
							LOGGER.info("Disconnecting from a peer");
							
							peers.get(randomElement).disconnect();
							
						} catch (Exception e) {
							
							LOGGER.error("Exception killing connection", e);
							
						}
				
				}
				
			}
		}, 0, 300000);
		
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
