package org.gmagnotta.bitcoin.blockchain;

import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeaders;

/**
 * Represent the blockchain known by the node
 * 
 * @author giuseppe
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
	public BlockHeaders getBlock(int index);
	
	/**
	 * Returns a list of hashes
	 * @return
	 */
	public List<Sha256Hash> getHashList();
	
	/**
	 * Return a list of block headers
	 * @return
	 */
	public List<BlockHeaders> getBlockHeaders();

}