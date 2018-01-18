package org.gmagnotta.bitcoin.blockchain;

import java.sql.Connection;
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

public class BlockChainSQLiteImpl implements BlockChain {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlockChainSQLiteImpl.class);

	public static final String CREATE_HEADER_TABLE = "create table provider_channel (hash text not null, version integer not null, prevBlock text not null, merkleRoot text not null, timestamp integer not null, bits integer not null, nonce integer not null, txnCount integer not null, primary key (hash));";

	public static final String RETRIEVE_LAST_INDEX = "select count(hash) as lastIndex from provider_channel order by timestamp asc;";

	public static final String RETRIEVE_BY_INDEX = "select hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txncount from provider_channel where rowid = ?;";
	
	public static final String RETRIEVE_BY_HASH = "select hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txncount from provider_channel where hash = ?;";
	
	private static final String HEADER_INSERT = "insert into provider_channel (hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txnCount) values (?, ?, ?, ?, ?, ?, ?, ?);";

	protected BasicDataSource dataSource;

	private BlockChainParameters blockChainParameters;

	public BlockChainSQLiteImpl(BlockChainParameters blockChainParameters, BasicDataSource dataSource) {

		this.blockChainParameters = blockChainParameters;

		this.dataSource = dataSource;

	}

	@Override
	public synchronized long getLastKnownIndex() {

		Statement statement = null;
		Connection connection = null;

		try {

			connection = dataSource.getConnection();
			
			statement = connection.createStatement();

			ResultSet rs = statement.executeQuery(RETRIEVE_LAST_INDEX);

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

	private ResultSetHandler<BlockHeader> createBlockHeaderResultSetHandler() {

		return new ResultSetHandler<BlockHeader>() {

			@Override
			public BlockHeader handle(ResultSet rs) throws SQLException {

				if (rs.next()) {

					return blockHeaderFromResultSet(rs);

				}

				return null;
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
	private BlockHeader blockHeaderFromResultSet(ResultSet rs) throws SQLException {

		Sha256Hash prevBlock = Sha256Hash.wrap(rs.getString("prevBlock"));
		Sha256Hash merkleRoot = Sha256Hash.wrap(rs.getString("merkleRoot"));

		return new BlockHeader(rs.getLong("version"), prevBlock, merkleRoot, rs.getLong("timestamp"),
				rs.getLong("bits"), rs.getLong("nonce"), rs.getLong("txncount"));

	}

	@Override
	public synchronized BlockHeader getBlockHeader(int index) {
		
		if (index == 0)
			return blockChainParameters.getGenesis();

		ResultSetHandler<BlockHeader> handler = createBlockHeaderResultSetHandler();

		QueryRunner queryRunner = new QueryRunner(dataSource);

		try {
			return queryRunner.query(RETRIEVE_BY_INDEX, handler, index);
		} catch (SQLException e) {

			LOGGER.error("Exception!", e);

			return null;
		}

	}
	
	public synchronized BlockHeader getBlockHeader(String hash) {

		ResultSetHandler<BlockHeader> handler = createBlockHeaderResultSetHandler();

		QueryRunner queryRunner = new QueryRunner(dataSource);

		try {
			return queryRunner.query(RETRIEVE_BY_HASH, handler, hash);
		} catch (SQLException e) {

			LOGGER.error("Exception!", e);

			return null;
		}

	}

	@Override
	public synchronized List<Sha256Hash> getHashList(long index, long len) {

		List<Sha256Hash> hashes = new ArrayList<Sha256Hash>();

		return hashes;

	}

	@Override
	public synchronized List<BlockHeader> getBlockHeaders(long index, long len) {

		return null;

	}

	@Override
	public synchronized void addBlockHeader(BlockHeader receivedHeader) {
		
		Sha256Hash hash = org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(receivedHeader);

		if (getBlockHeader(hash.toString()) != null) {

			LOGGER.error("Blockchain already contains block {}", receivedHeader);

		} else {

			long lastIndex = getLastKnownIndex();
			
			BlockHeader current = getBlockHeader((int) lastIndex);

			Sha256Hash myHeaderSha = Sha256Hash
					.wrapReversed(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(current).getBytes());

			if (receivedHeader.getPrevBlock().equals(myHeaderSha)) {

				int currentTarget = (int) org.gmagnotta.bitcoin.utils.Utils.getNextWorkRequired(lastIndex, this,
						receivedHeader, blockChainParameters);

				if (!Utils.isShaMatchesTarget(Utils.computeBlockHeaderHash(receivedHeader), currentTarget)) {

					LOGGER.error("Block hash is not valid!");

					return;

				}

				try {
					insertHeader(receivedHeader, hash.toString());
				} catch (Exception e) {
					
					LOGGER.error("Exception!", e);
					
				}

			}

		}

	}
	
	public void insertHeader(BlockHeader blockHeader, String hash) throws Exception {
		
		QueryRunner run = new QueryRunner(dataSource);
		
			//hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txnCount
			
			run.update(HEADER_INSERT, hash,
					blockHeader.getVersion(), blockHeader.getPrevBlock().toString(), blockHeader.getMerkleRoot().toString(),
					blockHeader.getTimestamp(), blockHeader.getBits(), blockHeader.getNonce(), blockHeader.getTxnCount());
			
			LOGGER.info("Inserted {}", hash);
			
		
	}

}
