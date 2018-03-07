package org.gmagnotta.bitcoin.blockchain;

import java.util.List;

import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.message.impl.BlockMessage;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.message.impl.TransactionOutput;
import org.gmagnotta.bitcoin.utils.Sha256Hash;

/**
 * Represents the blockchain known by a node
 */
public interface BlockChain {
	
	/**
	 * Return last known number
	 * 
	 * @return
	 */
	public long getBestChainLenght();

	/**
	 * Returns block header at specified position
	 * 
	 * @param index
	 * @return
	 */
	public ValidatedBlockHeader getBlockHeader(int index);
	
	/**
	 * Returns block header from specified hash
	 * 
	 * @param hash
	 * @return
	 */
	public ValidatedBlockHeader getBlockHeader(String hash);
	
	/**
	 * Return a list of block headers
	 * @return
	 */
	public List<ValidatedBlockHeader> getBlockHeaders(long index, long len);

	/**
	 * Returns a list of len hashes starting from index
	 * @return
	 */
	public List<Sha256Hash> getHashList(long index, long len);
	
	
	/**
	 * Add the blockheader to the blockchain or throw Exception in case the block can't be connected
	 * @param header
	 */
	public void addBlockHeader(BlockHeader header) throws Exception;
	
	/**
	 * Add the complete block to the blockChain
	 * @param blockMessage
	 * @return
	 */
	public void addBlock(BlockMessage blockMessage) throws Exception;
	
	/**
	 * 
	 * @param hash
	 * @return
	 */
	public Transaction getTransaction(String txHash);
	
	/**
	 * 
	 * @param hash
	 * @return
	 */
	public TransactionOutput getTransactionOutput(String txHash, long idx);

	/**
	 * Check if a TransactionInput is already spent
	 * @param transactionInput
	 * @return
	 * @throws Exception 
	 */
	boolean isTransactionInputAlreadySpent(TransactionInput transactionInput) throws Exception;
	
	/**
	 * Return transaction manager
	 * @return
	 */
	public TransactionManager getTransactionManager();

	/**
	 * Creates auxiliary tables to speed lookup
	 * @param previousBlock
	 * @throws Exception
	 */
	void createAuxiliaryTables(Sha256Hash previousBlock) throws Exception;

}
