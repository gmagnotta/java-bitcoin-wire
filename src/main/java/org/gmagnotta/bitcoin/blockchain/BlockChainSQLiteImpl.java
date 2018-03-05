package org.gmagnotta.bitcoin.blockchain;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.gmagnotta.bitcoin.TransactionAwareBasicDataSource;
import org.gmagnotta.bitcoin.TransactionAwareQueryRunner;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.message.impl.BlockMessage;
import org.gmagnotta.bitcoin.message.impl.OutPoint;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.message.impl.TransactionOutput;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

public class BlockChainSQLiteImpl implements BlockChain {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BlockChainSQLiteImpl.class);

	public static final String CREATE_HEADER_TABLE = "create table blockHeader (hash text not null, number integer not null, version integer not null, prevBlock text not null, merkleRoot text not null, timestamp integer not null, bits integer not null, nonce integer not null, txnCount integer not null, primary key (hash));";
	
	public static final String CREATE_TRANSACTION_TABLE = "create table tx (hash text not null, idx integer not null, version integer not null, lockTime integer not null, block text not null REFERENCES blockHeader(hash) DEFERRABLE INITIALLY DEFERRED, primary key (hash, block));";
	
	public static final String CREATE_TXOUT_TABLE = "create table tx_out (value integer not null, idx integer not null, scriptPubKey text not null, tx text not null, block text not null, foreign key (tx, block) references tx(hash, block) DEFERRABLE INITIALLY DEFERRED);";
	
	public static final String CREATE_TXIN_TABLE = "create table tx_in (prevTx string not null, prevIdx integer not null, idx integer not null, scriptSig text not null, sequence integer not null, tx text not null, block text not null, foreign key (tx, block) references tx(hash, block) DEFERRABLE INITIALLY DEFERRED);";
	
	public static final String CREATE_BESTCHAIN_VIEW = "create view bestChain(number, hash, version, prevBlock, merkleRoot, timeStamp, bits, nonce, txnCount) as WITH RECURSIVE header(number, hash, version, prevBlock, merkleRoot, timeStamp, bits, nonce, txnCount) AS ( SELECT b.number, b.hash, b.version, b.prevBlock, b.merkleRoot, b.timeStamp, b.bits, b.nonce, b.txnCount FROM blockHeader b WHERE b.hash = (select hash from blockHeader where number = (select max(number) from blockHeader) order by timestamp asc limit 1) UNION ALL SELECT cte_count.number, cte_count.hash, cte_count.version, cte_count.prevBlock, cte_count.merkleRoot, cte_count.timeStamp, cte_count.bits, cte_count.nonce, cte_count.txnCount from blockHeader cte_count, header where cte_count.hash = header.prevBlock ) SELECT * from header;";

	public static final String RETRIEVE_BESTCHAIN_LEN = "select max(number) as lastIndex from bestChain;";
	
	public static final String RETRIEVE_BY_NUMBER = "select number, hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txncount from bestChain where number = ?;";
	
	public static final String RETRIEVE_BY_HASH = "select number, hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txncount from bestChain where hash = ?;";
	
	public static final String RETRIEVE_BY_HASH_ALL = "select number, hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txncount from blockHeader where hash = ?;";
	
//	public static final String RETRIEVE_BY_PREV_BLOCK = "select hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txncount from bestChain where prevBlock = ?;";
	
	public static final String RETRIEVE_HEADER_FROM_TO = "select number, hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txncount from bestChain where number >= ? order by number asc limit ?;";
	
	public static final String HEADER_INSERT = "insert into blockHeader (hash, version, prevBlock, merkleRoot, timestamp, bits, nonce, txnCount, number) values (?,?,?,?,?,?,?,?,?);";
	
	public static final String TRANSACTION_INSERT = "insert into tx(hash, idx, version, lockTime, block) values (?, ?, ?, ?, ?);";
	
	public static final String TRANSACTION_OUT_INSERT = "insert into tx_out(value, idx, scriptPubKey, tx, block) values (?, ?, ?, ?, ?);";
	
	public static final String TRANSACTION_INPUT_INSERT = "insert into tx_in(prevTx, prevIdx, idx, scriptSig, sequence, tx, block) values (?, ?, ?, ?, ?, ?, ?);";
	
	public static final String TRANSACTION_RETRIEVE = "select * from tx t where t.hash = ?";
	
	public static final String TRANSACTION_INPUT_RETRIEVE = "select * from tx_in i where i.tx = ? order by idx asc";
	
	public static final String TRANSACTION_INPUT_ALREADY_SPENT_TEMPORARY_TABLE = "CREATE TEMPORARY TABLE IF NOT EXISTS unspent AS select i.* from tx_in i where (i.tx, i.block) in (select t.hash, t.block from tx t where t.block in ( WITH RECURSIVE header(number, hash, version, prevBlock, merkleRoot, timeStamp, bits, nonce, txnCount) AS 	( SELECT b.number, b.hash, b.version, b.prevBlock, b.merkleRoot, b.timeStamp, b.bits, b.nonce, b.txnCount FROM blockHeader b WHERE b.hash = (?) UNION ALL SELECT cte_count.number, cte_count.hash, cte_count.version, cte_count.prevBlock, cte_count.merkleRoot, cte_count.timeStamp, cte_count.bits, cte_count.nonce, cte_count.txnCount	from blockHeader cte_count, header where cte_count.hash = header.prevBlock ) SELECT hash from header ) ); CREATE INDEX temp.unspent_idx on unspent(prevTx, prevIdx);";
	
	public static final String TRANSACTION_INPUT_ALREADY_SPENT = "select i.* from unspent i where i.prevTx = ? and i.prevIdx = ?";
	
	public static final String TRANSACTION_OUTPUT_RETRIEVE = "select * from tx_out o where o.tx = ? order by idx asc";
	
//	public static final String INDEX_FROM_HASH = "select number from bestChain where hash = ?;";
	
//	public static final String INDEX_FROM_HASH_ALL = "select number from blockHeader where hash = ?;";
	
	public static final String SEARCH_DUPLICATED_BLOCKS = "select number from blockHeader group by number having count(number) > 1 order by number asc;";
	
	public static final String SEARCH_CHILDS = "WITH RECURSIVE header(idx, hash, version, prevBlock, merkleRoot, timeStamp, bits, nonce, txnCount) AS ( SELECT 0 as idx, b.hash, b.version, b.prevBlock, b.merkleRoot, b.timeStamp, b.bits, b.nonce, b.txnCount FROM blockHeader b WHERE b.hash = ? UNION ALL SELECT idx + 1, cte_count.hash, cte_count.version, cte_count.prevBlock, cte_count.merkleRoot, cte_count.timeStamp, cte_count.bits, cte_count.nonce, cte_count.txnCount from blockHeader cte_count, header where cte_count.prevBlock = header.hash ) SELECT count(*) as childs from header";
	
//	public static final String RECURSE_BLOCKCHAIN = "WITH RECURSIVE header(number, hash, version, prevBlock, merkleRoot, timeStamp, bits, nonce, txnCount) AS ( SELECT b.number, b.hash, b.version, b.prevBlock, b.merkleRoot, b.timeStamp, b.bits, b.nonce, b.txnCount FROM blockHeader b WHERE b.hash = ? UNION ALL SELECT cte_count.number, cte_count.hash, cte_count.version, cte_count.prevBlock, cte_count.merkleRoot, cte_count.timeStamp, cte_count.bits, cte_count.nonce, cte_count.txnCount from blockHeader cte_count, header where cte_count.hash = header.prevBlock ) SELECT * from header";
	
//	public static final String RETRIEVE_LONGEST_HEADER = "select number, hash, version, prevBlock, merkleRoot, timeStamp, bits, nonce, txnCount from blockHeader where number = (select max(number) from blockHeader) order by timestamp asc limit 1";
	
	/*
	 * CREATE INDEX `max_number` ON `blockHeader` (`number` )
	 * CREATE INDEX `timestamp_asc` ON `blockHeader` (`timestamp` ASC)
	 * CREATE INDEX `tx_hash_block` ON `tx` (`hash` ,`block` )
	 * CREATE INDEX `tx_out_tx` ON `tx_out` (`tx` )
	 * CREATE INDEX `tx_tx_tx_block` ON `tx_in` (`tx` ,`block` )
	 * CREATE INDEX `txin_tx` ON `tx_in` (`tx` )
	 */
	
	protected TransactionAwareBasicDataSource dataSource;

	private BlockChainParameters blockChainParameters;
	
	private BlockCache blockCache;
	
	public BlockChainSQLiteImpl(BlockChainParameters blockChainParameters, TransactionAwareBasicDataSource dataSource) {

		this.blockChainParameters = blockChainParameters;

		this.dataSource = dataSource;
		
		this.blockCache = new BlockCache(blockChainParameters.getDifficultyAdjustmentInterval());

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
	
	private ResultSetHandler<Transaction> createTransactionResultSetHandler() {

		return new ResultSetHandler<Transaction>() {

			@Override
			public Transaction handle(ResultSet rs) throws SQLException {

				if (rs.next()) {

					return transactionFromResultSet(rs);

				}

				return null;
			}
		};

	}
	
	private ResultSetHandler<List<TransactionInput>> createListTransactionInputResultSetHandler() {

		return new ResultSetHandler<List<TransactionInput>>() {

			@Override
			public List<TransactionInput> handle(ResultSet rs) throws SQLException {
				
				List<TransactionInput> list = new ArrayList<TransactionInput>();

				while (rs.next()) {

					TransactionInput header = transactionInputFromResultSet(rs);
					
					list.add(header);

				}

				return list;
			}
			
		};

	}
	
	private ResultSetHandler<List<TransactionOutput>> createListTransactionOutputResultSetHandler() {

		return new ResultSetHandler<List<TransactionOutput>>() {

			@Override
			public List<TransactionOutput> handle(ResultSet rs) throws SQLException {
				
				List<TransactionOutput> list = new ArrayList<TransactionOutput>();

				while (rs.next()) {

					TransactionOutput header = transactionOutputFromResultSet(rs);
					
					list.add(header);

				}

				return list;
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
	
	private TransactionInput transactionInputFromResultSet(ResultSet rs) throws SQLException {

		Sha256Hash prevtx = Sha256Hash.wrap(rs.getString("prevTx"));
		long prevIdx = rs.getLong("prevIdx");
		byte[] scriptSig = Hex.decode(rs.getString("scriptSig"));
		long sequence = rs.getLong("sequence");

		OutPoint outPoint = new OutPoint(prevtx, prevIdx);
		
		return new TransactionInput(outPoint, scriptSig, sequence);

	}
	
	private TransactionOutput transactionOutputFromResultSet(ResultSet rs) throws SQLException {

		BigInteger value = BigInteger.valueOf(rs.getLong("value"));
		byte[] scriptPubKey = Hex.decode(rs.getString("scriptPubKey"));

		return new TransactionOutput(value, scriptPubKey);

	}
	
	private Transaction transactionFromResultSet(ResultSet rs) throws SQLException {

//		Sha256Hash prevBlock = Sha256Hash.wrap(rs.getString("hash"));
//		long idx = rs.getLong("idx");
		long version = rs.getLong("version");
		long lockTime = rs.getLong("lockTime");
//		Sha256Hash block = Sha256Hash.wrap(rs.getString("block"));

		return new Transaction(version, null, null, lockTime);

	}

	@Override
	public ValidatedBlockHeader getBlockHeader(int number) {
		
		ValidatedBlockHeader header = blockCache.getBlockHeader(number);
		
		if (header != null) {
			return header;
		}
		
		ResultSetHandler<ValidatedBlockHeader> handler = createBlockHeaderResultSetHandler();

		QueryRunner queryRunner = new TransactionAwareQueryRunner(dataSource);

		try {
			
			header = queryRunner.query(RETRIEVE_BY_NUMBER, handler, number);
			
			if (header != null) {
				
				blockCache.putBlockHeader(number, header.getHash().toString(), header);
			
			}
			
			return header;
			
		} catch (SQLException e) {

			LOGGER.error("Exception!", e);

			return null;
		}

	}
	
	@Override
	public ValidatedBlockHeader getBlockHeader(String hash) {
		
		ValidatedBlockHeader header = blockCache.getBlockHeader(hash);
		
		if (header != null) {
			return header;
		}
		
		ResultSetHandler<ValidatedBlockHeader> handler = createBlockHeaderResultSetHandler();

		QueryRunner queryRunner = new TransactionAwareQueryRunner(dataSource);

		try {
			
			header = queryRunner.query(RETRIEVE_BY_HASH, handler, hash);
			
			if (header != null) {
			
				blockCache.putBlockHeader((int) header.getNumber(), header.getHash().toString(), header);
			
				
			}

			return header;
			
		} catch (SQLException e) {

			LOGGER.error("Exception!", e);

			return null;
		}

	}
	
	private ValidatedBlockHeader getBlockHeaderFromAll(String hash) {
		
		ValidatedBlockHeader header = blockCache.getBlockHeader(hash);
		
		if (header != null) {
			return header;
		}
		
		ResultSetHandler<ValidatedBlockHeader> handler = createBlockHeaderResultSetHandler();

		QueryRunner queryRunner = new TransactionAwareQueryRunner(dataSource);

		try {
			
			header = queryRunner.query(RETRIEVE_BY_HASH_ALL, handler, hash);
			
			if (header != null) {
				
				blockCache.putBlockHeader((int) header.getNumber(), header.getHash().toString(), header);

			}
			
			return header;
			
		} catch (SQLException e) {

			LOGGER.error("Exception!", e);

			return null;
		}

	}
	
	@Override
	public List<Sha256Hash> getHashList(long index, long len) {

		ResultSetHandler<List<Sha256Hash>> handler = createListBlockHeaderHashesResultSetHandler();

		QueryRunner queryRunner = new TransactionAwareQueryRunner(dataSource);

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

		QueryRunner queryRunner = new TransactionAwareQueryRunner(dataSource);

		try {
			
			return queryRunner.query(RETRIEVE_HEADER_FROM_TO, handler, index, len);
			
		} catch (SQLException e) {

			LOGGER.error("Exception!", e);

			return null;
		}

	}

	@Override
	public void addBlockHeader(BlockHeader receivedHeader) throws Exception {
		
		// compute hash of received header
		Sha256Hash receivedHeaderHash = org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(receivedHeader);

		// Check if it is already present
		if (getBlockHeaderFromAll(receivedHeaderHash.toReversedString()) != null) {

			throw new Exception("Blockchain already contains block " + receivedHeader);

		} 
		
		// Retrieve previous header referenced
		ValidatedBlockHeader previousHeader = getBlockHeaderFromAll(receivedHeader.getPrevBlock().toString());
		
		if (previousHeader == null) {
			
			throw new Exception("BlockHeader " + receivedHeader + " references an unknown block " + receivedHeader.getPrevBlock().toString());
			
		}
		
		int currentTarget = (int) Utils.getNextWorkRequired(previousHeader.getNumber(), this, receivedHeader, blockChainParameters);
		
		if (!Utils.isShaMatchesTarget(receivedHeaderHash, currentTarget)) {

			throw new Exception("Block Header " + receivedHeaderHash + " doesn't match expected target" + currentTarget);

		}
		
		insertHeader(receivedHeader, receivedHeaderHash.toReversedString(), previousHeader);
		
		LOGGER.info("Inserted header {}", receivedHeader);
		
	}

	@Override
	public void addBlock(BlockMessage blockMessage) throws Exception {

		Sha256Hash receivedHeaderHash = org.gmagnotta.bitcoin.utils.Utils
				.computeBlockHeaderHash(blockMessage.getBlockHeader()).getReversed();

		BlockHeader header = blockMessage.getBlockHeader();

		if (getBlockHeaderFromAll(receivedHeaderHash.toString()) == null) {

			throw new Exception("Blockchain doesn't contains block " + header);

		}

		List<Transaction> txs = blockMessage.getTxns();

		for (int i = 0; i < txs.size(); i++) {

			Transaction tx = txs.get(i);

			List<TransactionOutput> outputs = tx.getTransactionOutput();

			for (int out = 0; out < outputs.size(); out++) {

				insertTransactionOutput(outputs.get(out), out,
						Utils.calculateTransactionHash(tx).toReversedString(), receivedHeaderHash.toString());

			}

			List<TransactionInput> inputs = tx.getTransactionInput();

			for (int in = 0; in < inputs.size(); in++) {

				insertTransactionInput(inputs.get(in), in, Utils.calculateTransactionHash(tx).toReversedString(), receivedHeaderHash.toString());

			}

			insertTransaction(Utils.calculateTransactionHash(tx).toReversedString(), i, tx,
					receivedHeaderHash.toString());

		}

	}
	
	private void insertHeader(BlockHeader blockHeader, String hash, ValidatedBlockHeader previous) throws Exception {
		
		QueryRunner run = new TransactionAwareQueryRunner(dataSource);
		
			run.update(HEADER_INSERT, hash,
					blockHeader.getVersion(), blockHeader.getPrevBlock().toString(), blockHeader.getMerkleRoot().toString(),
					blockHeader.getTimestamp(), blockHeader.getBits(), blockHeader.getNonce(), blockHeader.getTxnCount(),
					(previous.getNumber() + 1));
			
			ValidatedBlockHeader v = new ValidatedBlockHeader(blockHeader.getVersion(), blockHeader.getPrevBlock(), blockHeader.getMerkleRoot(),
					blockHeader.getTimestamp(), blockHeader.getBits(), blockHeader.getNonce(), blockHeader.getTxnCount(), Sha256Hash.wrap(hash), previous.getNumber() + 1);
			
			blockCache.putBlockHeader((int) previous.getNumber() + 1, hash, v);
			
	}

	private void insertTransaction(String txHash, long idx, Transaction transaction, String blockHeaderHash)
			throws Exception {

		QueryRunner run = new TransactionAwareQueryRunner(dataSource);

		run.update(TRANSACTION_INSERT, txHash, idx, transaction.getVersion(), transaction.getLockTime(),
				blockHeaderHash);

	}

	private void insertTransactionOutput(TransactionOutput txout, long idx, String transactionHash, String blockHash) throws Exception {

		QueryRunner run = new TransactionAwareQueryRunner(dataSource);

		run.update(TRANSACTION_OUT_INSERT, txout.getValue(), idx, Hex.toHexString(txout.getScriptPubKey()),
				transactionHash, blockHash);

	}

	private void insertTransactionInput(TransactionInput txIn, long idx, String transactionHash, String blockHash) throws Exception {

		QueryRunner run = new TransactionAwareQueryRunner(dataSource);

		run.update(TRANSACTION_INPUT_INSERT, txIn.getPreviousOutput().getHash().toString(), txIn.getPreviousOutput().getIndex(),
				idx, Hex.toHexString(txIn.getScriptSig()), txIn.getSequence(), transactionHash, blockHash);

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
	
	@Override
	public Transaction getTransaction(String hash) {
		
		ResultSetHandler<Transaction> handler = createTransactionResultSetHandler();

		QueryRunner queryRunner = new TransactionAwareQueryRunner(dataSource);

		try {
			
			List<TransactionInput> txInput = queryRunner.query(TRANSACTION_INPUT_RETRIEVE, createListTransactionInputResultSetHandler(), hash);
			
			List<TransactionOutput> txOutput = queryRunner.query(TRANSACTION_OUTPUT_RETRIEVE, createListTransactionOutputResultSetHandler(), hash);
			
			Transaction tx = queryRunner.query(TRANSACTION_RETRIEVE, handler, hash);
			
			if (tx == null) return null;
			
			tx.setTransactionOutput(txOutput);
			
			tx.setTransactionInput(txInput);
			
			return tx;
			
		} catch (SQLException e) {

			LOGGER.error("Exception!", e);

			return null;
		}
		
	}
	
	@Override
	public boolean isTransactionInputAlreadySpent(TransactionInput transactionInput) throws Exception {
		
		QueryRunner queryRunner = new TransactionAwareQueryRunner(dataSource);
	
		List<TransactionInput> txInput = queryRunner.query(TRANSACTION_INPUT_ALREADY_SPENT, createListTransactionInputResultSetHandler(), transactionInput.getPreviousOutput().getHash(), transactionInput.getPreviousOutput().getIndex());
		
		return txInput.size() > 0;
			
	}
	
	@Override
	public void updateSpentTransactions(Sha256Hash previousBlock) throws Exception {
		
		QueryRunner queryRunner = new TransactionAwareQueryRunner(dataSource);
		
		// DELETE IF EXISTS
		PreparedStatement s = queryRunner.getDataSource().getConnection().prepareStatement("DROP TABLE IF EXISTS unspent; DROP INDEX IF exists temp.unspent_idx;" );
		s.executeUpdate();
		s.close();
		
		// CREATE TEMP TABLE IF NOT EXISTS
		s = queryRunner.getDataSource().getConnection().prepareStatement(TRANSACTION_INPUT_ALREADY_SPENT_TEMPORARY_TABLE);
		s.setString(1, previousBlock.toString());
		s.executeUpdate();
		s.close();
		
	}

	@Override
	public TransactionManager getTransactionManager() {
		return dataSource;
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
