package org.gmagnotta.bitcoin.blockchain;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeaders;

public class BlockChainImpl implements BlockChain {
	
	private List<BlockHeaders> blocks;
	
	public BlockChainImpl() {

		blocks = new ArrayList<BlockHeaders>();

		blocks.add(
				
				/* GENESIS BLOCK */
				new BlockHeaders(1,
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
	public BlockHeaders getBlock(int index) {
		return blocks.get(index);
	}

	public List<Sha256Hash> getHashList() {
		
		List<Sha256Hash> hashes = new ArrayList<Sha256Hash>();
		
		for (BlockHeaders header : blocks) {
			
			hashes.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(header));
			
		}
		
		return hashes;
		
	}

	@Override
	public List<BlockHeaders> getBlockHeaders() {
		return new ArrayList<BlockHeaders>();
	}

}
