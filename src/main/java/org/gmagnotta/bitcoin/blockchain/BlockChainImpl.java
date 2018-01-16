package org.gmagnotta.bitcoin.blockchain;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockChainImpl implements BlockChain {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BlockChainImpl.class);
	
	private List<BlockHeader> headers;
	private long index;
	
	public BlockChainImpl() {

		headers = new ArrayList<BlockHeader>();

		headers.add(
				
				/* TESTNET3 GENESIS BLOCK */
				new BlockHeader(1,
				Sha256Hash.wrap("0000000000000000000000000000000000000000000000000000000000000000"),
				Sha256Hash.wrap("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"),
				1296688602,
				486604799,
				414098458,
				1
				
		));
		
//		headers.add(
//				
//				/* REGTEST GENESIS BLOCK */
//				new BlockHeader(1,
//				Sha256Hash.wrap("0000000000000000000000000000000000000000000000000000000000000000"),
//				Sha256Hash.wrap("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"),
//				1296688602,
//				545259519,
//				2,
//				1
//				
//		));
		
		index = 0;
		
	}
	
	@Override
	public synchronized long getLastKnownIndex() {
		return index;
	}

	@Override
	public synchronized BlockHeader getBlock(int index) {
		return headers.get(index);
	}

	@Override
	public synchronized List<Sha256Hash> getHashList(long index, long len) {
		
		List<Sha256Hash> hashes = new ArrayList<Sha256Hash>();
		
		for (long i = index; i < len; i++) {
			
			BlockHeader header = headers.get((int) i);
			
			hashes.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(header));
			
		}
		
		return hashes;
		
	}

	@Override
	public synchronized List<BlockHeader> getBlockHeaders(long index, long len) {
		
		return headers.subList((int) index, (int) (index + len));
		
	}

	@Override
	public synchronized void addBlockHeader(BlockHeader receivedHeader) {
		
		if (headers.contains(receivedHeader)) {
			
			LOGGER.error("Blockchain already contains block {}", receivedHeader);
			
		} else {
			
				BlockHeader current = headers.get((int) index);
			
				Sha256Hash myHeaderSha = Sha256Hash.wrapReversed(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(current).getBytes());
				
				if (receivedHeader.getPrevBlock().equals(myHeaderSha)) {
					
					headers.add(receivedHeader);
					index++;
					
				}
		
		}
		
	}

}
