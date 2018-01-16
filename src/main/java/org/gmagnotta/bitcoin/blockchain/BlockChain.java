package org.gmagnotta.bitcoin.blockchain;

import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;

/**
 * Represents the blockchain known by a node
 * 
 */
public interface BlockChain {
	
	/**
	 * Return last known block
	 * 
	 * @return
	 */
	public long getBlockStartHeight();

	/**
	 * Returns block at position
	 * 
	 * @param index
	 * @return
	 */
	public BlockHeader getBlock(int index);
	
	/**
	 * Returns a list of hashes
	 * @return
	 */
	public List<Sha256Hash> getHashList();
	
	/**
	 * Return a list of block headers
	 * @return
	 */
	public List<BlockHeader> getBlockHeaders();
	
	/**
	 * Add the blockheader to the blockchain
	 * @param header
	 */
	public void addBlockHeader(BlockHeader header);

}
