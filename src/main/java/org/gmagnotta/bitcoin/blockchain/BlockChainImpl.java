package org.gmagnotta.bitcoin.blockchain;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockChainImpl implements BlockChain {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BlockChainImpl.class);
	
	private List<BlockHeader> blocks;
	
	public BlockChainImpl() {

		blocks = new ArrayList<BlockHeader>();

		blocks.add(
				
				/* GENESIS BLOCK */
				new BlockHeader(1,
				Sha256Hash.wrap("0000000000000000000000000000000000000000000000000000000000000000"),
				Sha256Hash.wrap("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"),
				1296688602,
				486604799,
				414098458,
				1
				
		));
		
	}
	
	@Override
	public long getBlockStartHeight() {
		return blocks.size() - 1;
	}

	@Override
	public BlockHeader getBlock(int index) {
		return blocks.get(index);
	}

	public List<Sha256Hash> getHashList() {
		
		List<Sha256Hash> hashes = new ArrayList<Sha256Hash>();
		
		for (BlockHeader header : blocks) {
			
			hashes.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(header));
			
		}
		
		return hashes;
		
	}

	@Override
	public List<BlockHeader> getBlockHeaders() {
		return new ArrayList<BlockHeader>();
	}

	@Override
	public void addBlockHeader(BlockHeader header) {
		
		if (blocks.contains(header)) {
			
			LOGGER.info("BLockchain already contains block {}", header);
			
		} else {
		
			for (BlockHeader h : blocks) {
				
				if (header.getPrevBlock().equals(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(header))) {
					
					
					
				}
				
			}
		
		}
		
	}

}
