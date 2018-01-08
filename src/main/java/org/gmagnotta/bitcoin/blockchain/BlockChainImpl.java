package org.gmagnotta.bitcoin.blockchain;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeaders;

import com.subgraph.orchid.encoders.Hex;

public class BlockChainImpl implements BlockChain {
	
	private List<Block> blocks;
	
	public BlockChainImpl() {

		blocks = new ArrayList<Block>();

		blocks.add(new Block() {

			@Override
			public Sha256Hash getHash() {
				return Sha256Hash.wrap("000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943");
			}

		});
		
	}
	
	@Override
	public long getBlockStartHeight() {
		return blocks.size() - 1;
	}

	@Override
	public Block getBlock(int index) {
		return blocks.get(index);
	}

	@Override
	public List<Sha256Hash> getHashList() {
		
		List<Sha256Hash> hashes = new ArrayList<Sha256Hash>();
		
		for (Block b : blocks) {
			
			hashes.add(b.getHash());
			
		}
		
		return hashes;
		
	}

	@Override
	public List<BlockHeaders> getBlockHeaders() {
		return new ArrayList<BlockHeaders>();
	}

}
