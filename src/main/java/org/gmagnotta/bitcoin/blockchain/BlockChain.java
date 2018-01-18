package org.gmagnotta.bitcoin.blockchain;

import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;

/**
 * Represents the blockchain known by a node
 */
public interface BlockChain {
	
	/**
	 * Return last known index
	 * 
	 * @return
	 */
	public long getLastKnownIndex();

	/**
	 * Returns block header at specified position
	 * 
	 * @param index
	 * @return
	 */
	public BlockHeader getBlockHeader(int index);
	
	/**
	 * Return a list of block headers
	 * @return
	 */
	public List<BlockHeader> getBlockHeaders(long index, long len);

	/**
	 * Returns a list of len hashes starting from index
	 * @return
	 */
	public List<Sha256Hash> getHashList(long index, long len);
	
	
	/**
	 * Add the blockheader to the blockchain
	 * @param header
	 */
	public void addBlockHeader(BlockHeader header);

}
