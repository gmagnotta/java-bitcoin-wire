package org.gmagnotta.bitcoin.blockchain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

public class BlockChainSQLiteImpl implements BlockChain {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlockChainSQLiteImpl.class);

	public static final String CREATE_HEADER_TABLE = "create table blockHeader (hash text not null, number integer not null, version integer not null, prevBlock text not null, merkleRoot text not null, timestamp integer not null, bits integer not null, nonce integer not null, txnCount integer not null, primary key (hash));";
	
	public static final String CREATE_BESTCHAIN_VIEW = "create view bestChain(number, hash, version, prevBlock, merkleRoot, timeStamp, bits, nonce, txnCount) as WITH RECURSIVE header(number, hash, version, prevBlock, merkleRoot, timeStamp, bits, nonce, txnCount) AS ( SELECT b.number, b.hash, b.version, b.prevBlock, b.merkleRoot, b.timeStamp, b.bits, b.nonce, b.txnCount FROM blockHeader b WHERE b.hash = (select hash from blockHeader where number = (select max(number) from blockHeader) order by timestamp asc limit 1) UNION ALL SELECT cte_count.number, cte_count.hash, cte_count.version, cte_count.prevBlock, cte_count.merkleRoot, cte_count.timeStamp, cte_count.bits, cte_count.nonce, cte_count.txnCount from blockHeader cte_count, header where cte_count.hash = header.prevBlock ) SELECT * from header;";

	public static final String RETRIEVE_BESTCHAIN_LEN = "select max(number) as lastIndex from bestChain;";
	
	public static final String RETRIEVE_BY_NUMBER = "select number, hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txncount from bestChain where number = ?;";
	
	public static final String RETRIEVE_BY_HASH = "select number, hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txncount from bestChain where hash = ?;";
	
	public static final String RETRIEVE_BY_HASH_ALL = "select number, hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txncount from blockHeader where hash = ?;";
	
//	public static final String RETRIEVE_BY_PREV_BLOCK = "select hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txncount from bestChain where prevBlock = ?;";
	
	public static final String RETRIEVE_HEADER_FROM_TO = "select number, hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txncount from bestChain where number >= ? order by number asc limit ?;";
	
	public static final String HEADER_INSERT = "insert into blockHeader (hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txnCount, number) values (?,?,?,?,?,?,?,?,?);";
	
//	public static final String INDEX_FROM_HASH = "select number from bestChain where hash = ?;";
	
//	public static final String INDEX_FROM_HASH_ALL = "select number from blockHeader where hash = ?;";
	
	public static final String SEARCH_DUPLICATED_BLOCKS = "select number from blockHeader group by number having count(number) > 1 order by number asc;";
	
	public static final String SEARCH_CHILDS = "WITH RECURSIVE header(idx, hash, version, prevBlock, merkleRoot, timeStamp, bits, nonce, txnCount) AS ( SELECT 0 as idx, b.hash, b.version, b.prevBlock, b.merkleRoot, b.timeStamp, b.bits, b.nonce, b.txnCount FROM blockHeader b WHERE b.hash = ? UNION ALL SELECT idx + 1, cte_count.hash, cte_count.version, cte_count.prevBlock, cte_count.merkleRoot, cte_count.timeStamp, cte_count.bits, cte_count.nonce, cte_count.txnCount from blockHeader cte_count, header where cte_count.prevBlock = header.hash ) SELECT count(*) as childs from header";
	
//	public static final String RECURSE_BLOCKCHAIN = "WITH RECURSIVE header(number, hash, version, prevBlock, merkleRoot, timeStamp, bits, nonce, txnCount) AS ( SELECT b.number, b.hash, b.version, b.prevBlock, b.merkleRoot, b.timeStamp, b.bits, b.nonce, b.txnCount FROM blockHeader b WHERE b.hash = ? UNION ALL SELECT cte_count.number, cte_count.hash, cte_count.version, cte_count.prevBlock, cte_count.merkleRoot, cte_count.timeStamp, cte_count.bits, cte_count.nonce, cte_count.txnCount from blockHeader cte_count, header where cte_count.hash = header.prevBlock ) SELECT * from header";
	
//	public static final String RETRIEVE_LONGEST_HEADER = "select number, hash, version, prevBlock, merkleRoot, timeStamp, bits, nonce, txnCount from blockHeader where number = (select max(number) from blockHeader) order by timestamp asc limit 1";
	
	public static final String RETRIEVE_LOWEST_NUMBER = "select coalesce(min(bits), ?) as bits from bestChain where number >= ? and number <= ? and bits != ? ;";
	
	protected BasicDataSource dataSource;

	private BlockChainParameters blockChainParameters;
	
	public BlockChainSQLiteImpl(BlockChainParameters blockChainParameters, BasicDataSource dataSource) {

		this.blockChainParameters = blockChainParameters;

		this.dataSource = dataSource;

	}
	
	public void close() throws Exception {
		
		dataSource.close();
		
	}

	@Override
	public long getBestChainLenght() {

		Statement statement = null;
		Connection connection = null;

		try {

			connection = dataSource.getConnection();
			
			statement = connection.createStatement();

			ResultSet rs = statement.executeQuery(RETRIEVE_BESTCHAIN_LEN);

			if (rs.next()) {

				return rs.getInt("lastIndex");

			}

			statement.close();
			connection.close();

		} catch (Exception ex) {
			
			LOGGER.error("Exception", ex);

		} finally {

			if (statement != null) {

				try {
					statement.close();
				} catch (SQLException e) {
					LOGGER.error("Exception!", e);
				}

			}
			
			if (connection != null) {

				try {
					connection.close();
				} catch (SQLException e) {
					LOGGER.error("Exception!", e);
				}

			}

		}

		return 0;
	}

	private ResultSetHandler<ValidatedBlockHeader> createBlockHeaderResultSetHandler() {

		return new ResultSetHandler<ValidatedBlockHeader>() {

			@Override
			public ValidatedBlockHeader handle(ResultSet rs) throws SQLException {

				if (rs.next()) {

					return blockHeaderFromResultSet(rs);

				}

				return null;
			}
		};

	}
	
	private ResultSetHandler<List<Sha256Hash>> createListBlockHeaderHashesResultSetHandler() {

		return new ResultSetHandler<List<Sha256Hash>>() {

			@Override
			public List<Sha256Hash> handle(ResultSet rs) throws SQLException {
				
				List<Sha256Hash> list = new ArrayList<Sha256Hash>();

				while (rs.next()) {

					ValidatedBlockHeader header = blockHeaderFromResultSet(rs);
					
					list.add(header.getHash());

				}

				return list;
			}
			
		};

	}
	
	private ResultSetHandler<List<ValidatedBlockHeader>> createListBlockHeaderResultSetHandler() {

		return new ResultSetHandler<List<ValidatedBlockHeader>>() {

			@Override
			public List<ValidatedBlockHeader> handle(ResultSet rs) throws SQLException {
				
				List<ValidatedBlockHeader> list = new ArrayList<ValidatedBlockHeader>();

				while (rs.next()) {

					list.add(blockHeaderFromResultSet(rs));

				}

				return list;
			}
			
		};

	}

	/**
	 * Helper method
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private ValidatedBlockHeader blockHeaderFromResultSet(ResultSet rs) throws SQLException {

		Sha256Hash prevBlock = Sha256Hash.wrap(rs.getString("prevBlock"));
		Sha256Hash merkleRoot = Sha256Hash.wrap(rs.getString("merkleRoot"));
		Sha256Hash hash = Sha256Hash.wrap(rs.getString("hash"));

		return new ValidatedBlockHeader(rs.getLong("version"), prevBlock, merkleRoot, rs.getLong("timestamp"),
				rs.getLong("bits"), rs.getLong("nonce"), rs.getLong("txncount"), hash, rs.getLong("number"));

	}

	@Override
	public ValidatedBlockHeader getBlockHeader(int number) {
		
		ResultSetHandler<ValidatedBlockHeader> handler = createBlockHeaderResultSetHandler();

		QueryRunner queryRunner = new QueryRunner(dataSource);

		try {
			
			return queryRunner.query(RETRIEVE_BY_NUMBER, handler, number);
			
		} catch (SQLException e) {

			LOGGER.error("Exception!", e);

			return null;
		}

	}
	
	@Override
	public ValidatedBlockHeader getBlockHeader(String hash) {
		
		ResultSetHandler<ValidatedBlockHeader> handler = createBlockHeaderResultSetHandler();

		QueryRunner queryRunner = new QueryRunner(dataSource);

		try {
			return queryRunner.query(RETRIEVE_BY_HASH, handler, hash);
		} catch (SQLException e) {

			LOGGER.error("Exception!", e);

			return null;
		}

	}
	
	private ValidatedBlockHeader getBlockHeaderFromAll(String hash) {
		
		ResultSetHandler<ValidatedBlockHeader> handler = createBlockHeaderResultSetHandler();

		QueryRunner queryRunner = new QueryRunner(dataSource);

		try {
			
			return queryRunner.query(RETRIEVE_BY_HASH_ALL, handler, hash);
			
		} catch (SQLException e) {

			LOGGER.error("Exception!", e);

			return null;
		}

	}
	
	@Override
	public List<Sha256Hash> getHashList(long index, long len) {

		ResultSetHandler<List<Sha256Hash>> handler = createListBlockHeaderHashesResultSetHandler();

		QueryRunner queryRunner = new QueryRunner(dataSource);

		try {
			
			return queryRunner.query(RETRIEVE_HEADER_FROM_TO, handler, index, len);
			
		} catch (SQLException e) {

			LOGGER.error("Exception!", e);

			return null;
		}
		
	}

	@Override
	public List<ValidatedBlockHeader> getBlockHeaders(long index, long len) {

		ResultSetHandler<List<ValidatedBlockHeader>> handler = createListBlockHeaderResultSetHandler();

		QueryRunner queryRunner = new QueryRunner(dataSource);

		try {
			
			return queryRunner.query(RETRIEVE_HEADER_FROM_TO, handler, index, len);
			
		} catch (SQLException e) {

			LOGGER.error("Exception!", e);

			return null;
		}

	}

	@Override
	public boolean addBlockHeader(BlockHeader receivedHeader) {
		
		// compute hash of received header
		Sha256Hash receivedHeaderHash = org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(receivedHeader);

		// Check if it is already present
		if (getBlockHeaderFromAll(Hex.toHexString(receivedHeaderHash.getReversedBytes())) != null) {

			LOGGER.warn("Blockchain already contains block {}", receivedHeader);
			
			return false;

		} else {

			// Retrieve previous header referenced
			ValidatedBlockHeader previousHeader = getBlockHeaderFromAll(Hex.toHexString(receivedHeader.getPrevBlock().getBytes()));
			
			if (previousHeader == null) {
				
				LOGGER.error("BlockHeader {} references an unknown block {}", receivedHeader, Hex.toHexString(receivedHeader.getPrevBlock().getBytes()));
				
				return false;
				
			}
			
			int currentTarget = (int) getNextWorkRequired(previousHeader, this, receivedHeader, blockChainParameters);
			
			if (!Utils.isShaMatchesTarget(receivedHeaderHash, currentTarget)) {

				LOGGER.error("Block Header {} doesn't match expected target {}!", receivedHeaderHash, currentTarget);

				return false;

			}
			
			try {
				
				insertHeader(receivedHeader, Hex.toHexString(receivedHeaderHash.getReversedBytes()), previousHeader);
				
				LOGGER.info("Inserted header {}", receivedHeader);
				
				return true;
				
			} catch (Exception e) {
				
				LOGGER.error("Exception!", e);
				
				return false;
				
			}
			
		}

	}
	
	public void insertHeader(BlockHeader blockHeader, String hash, ValidatedBlockHeader previous) throws Exception {
		
		QueryRunner run = new QueryRunner(dataSource);
		
			run.update(HEADER_INSERT, hash,
					blockHeader.getVersion(), blockHeader.getPrevBlock().toString(), blockHeader.getMerkleRoot().toString(),
					blockHeader.getTimestamp(), blockHeader.getBits(), blockHeader.getNonce(), blockHeader.getTxnCount(),
					(previous.getNumber() + 1));
			
//			LOGGER.info("Inserted {}", hash);
		
	}

	public List<Integer> getDuplicatedBlockNumber() {
		
		PreparedStatement statement = null;
		Connection connection = null;
		
		List<Integer> list = new ArrayList<Integer>();

		try {

			connection = dataSource.getConnection();
			
			statement = connection.prepareStatement(SEARCH_DUPLICATED_BLOCKS);
			
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {

				list.add(rs.getInt("number"));

			}

			statement.close();
			connection.close();

		} catch (Exception ex) {
			
			LOGGER.error("Exception", ex);

		} finally {

			if (statement != null) {

				try {
					statement.close();
				} catch (SQLException e) {
					LOGGER.error("Exception!", e);
				}

			}
			
			if (connection != null) {

				try {
					connection.close();
				} catch (SQLException e) {
					LOGGER.error("Exception!", e);
				}

			}

		}

		return list;
		
	}
	
	private int getchild(String hash) {
		
		PreparedStatement statement = null;
		Connection connection = null;

		try {

			connection = dataSource.getConnection();
			
			statement = connection.prepareStatement(SEARCH_CHILDS);
			statement.setString(1, hash);

			ResultSet rs = statement.executeQuery();

			if (rs.next()) {

				return rs.getInt("childs");

			}

			statement.close();
			connection.close();

		} catch (Exception ex) {
			
			LOGGER.error("Exception", ex);

		} finally {

			if (statement != null) {

				try {
					statement.close();
				} catch (SQLException e) {
					LOGGER.error("Exception!", e);
				}

			}
			
			if (connection != null) {

				try {
					connection.close();
				} catch (SQLException e) {
					LOGGER.error("Exception!", e);
				}

			}

		}

		return 0;
		
	}
	
	private long getNextWorkRequired(ValidatedBlockHeader pindexLast, BlockChain blockchain, BlockHeader pblock,
			BlockChainParameters blockChainParameters) {

		if ((pindexLast.getNumber() + 1) % blockChainParameters.getDifficultyAdjustmentInterval() != 0) {

			if (blockChainParameters.getAllowMinDifficultyBlocks()) {

				// Special difficulty rule for testnet:
				// If the new block's timestamp is more than 2* 10 minutes
				// then allow mining of a min-difficulty block.
				if (pblock.getTimestamp() > pindexLast.getTimestamp()
						+ blockChainParameters.getTargetSpacing() * 2) {

					return org.gmagnotta.bitcoin.utils.Utils.compact(blockChainParameters.getPowLimit());

				} else {

					// Return the last non-special-min-difficulty-rules-block
//					long pindex = pindexLast.getNumber();
//					while (pindex % blockChainParameters.getDifficultyAdjustmentInterval() != 0 &&
//							blockchain.getBlockHeader((int) pindex).getBits() == org.gmagnotta.bitcoin.utils.Utils.compact(blockChainParameters.getPowLimit())) {
//						pindex--;
//					}
//					
//					return blockchain.getBlockHeader((int) pindex).getBits();
					
					// If block is multiple of maxadjustment return bits
					if ((pindexLast.getNumber() % blockChainParameters.getDifficultyAdjustmentInterval()) == 0) {
						return pindexLast.getBits();
					}
					
					return getBestNumber(pindexLast.getNumber(), blockChainParameters.getDifficultyAdjustmentInterval(),
							org.gmagnotta.bitcoin.utils.Utils.compact(blockChainParameters.getPowLimit())
							);

				}

			}

			return pindexLast.getBits();

		}

		// Go back by what we want to be 14 days worth of blocks
		long nHeightFirst = pindexLast.getNumber() - (blockChainParameters.getDifficultyAdjustmentInterval() - 1);

		BlockHeader pindexFirst = blockchain.getBlockHeader((int) nHeightFirst);

		return org.gmagnotta.bitcoin.utils.Utils.calculateNextWorkRequired(pindexLast, pindexFirst.getTimestamp(), blockChainParameters);

	}
	
	private long getBestNumber(long index, long interval, long bits) {

		PreparedStatement statement = null;
		Connection connection = null;
		
		try {

			connection = dataSource.getConnection();
			
			statement = connection.prepareStatement(RETRIEVE_LOWEST_NUMBER);
			statement.setInt(1, (int) bits);
			statement.setInt(2, (int) (Math.floorDiv(index, interval) * interval));
			statement.setInt(3, (int) index);
			statement.setInt(4, (int) bits);

			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				
				return rs.getInt("bits");
				
			}

			statement.close();
			connection.close();

		} catch (Exception ex) {
			
			LOGGER.error("Exception", ex);

		} finally {

			if (statement != null) {

				try {
					statement.close();
				} catch (SQLException e) {
					LOGGER.error("Exception!", e);
				}

			}
			
			if (connection != null) {

				try {
					connection.close();
				} catch (SQLException e) {
					LOGGER.error("Exception!", e);
				}

			}

		}

		return bits;
	}
	
//	@Override
//	public void manageForks() {
//		
//		List<Integer> duplicatedBlocks = getDuplicatedBlockNumber();
//		
//		LOGGER.info("Found duplicated blocks {}", duplicatedBlocks);
//		
//		for (BlockHeader header : duplicated) {
//			
//			Sha256Hash hash = org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(header);
//			
//			int childs = getchild(Hex.toHexString(hash.getReversedBytes()));
//			
//			if (childs > max) {
//				max = childs;
//				longest = header;
//			}
//			
//			LOGGER.info("Header {} has {} childs", hash, childs);
//			
//		}
//		
//		LOGGER.info("Longest chain {}", longest);
//		
//	}

}
