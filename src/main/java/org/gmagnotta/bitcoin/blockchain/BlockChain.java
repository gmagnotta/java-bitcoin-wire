package org.gmagnotta.bitcoin.blockchain;

import java.util.List;

import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.message.impl.Transaction;
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
	 * Add the blockheader to the blockchain
	 * @param header
	 */
	public boolean addBlockHeader(BlockHeader header);
	
	/**
	 * 
	 * @param hash
	 * @return
	 */
	public Transaction getTransaction(String hash);
	
}
