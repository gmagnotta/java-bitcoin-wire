package org.gmagnotta.bitcoin.blockchain;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockChainImpl implements BlockChain {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlockChainImpl.class);

	private List<BlockHeader> headers;
	private long last;
	private BlockChainParameters blockChainParameters;

	public BlockChainImpl(BlockChainParameters blockChainParameters) {
		
		this.blockChainParameters = blockChainParameters;

		headers = new ArrayList<BlockHeader>();

		headers.add(blockChainParameters.getGenesis());
		last = 0;

	}

	@Override
	public synchronized long getLastKnownIndex() {
		return last;
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

			BlockHeader current = headers.get((int) last);

			Sha256Hash myHeaderSha = Sha256Hash
					.wrapReversed(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(current).getBytes());

			if (receivedHeader.getPrevBlock().equals(myHeaderSha)) {

				int currentTarget = (int) org.gmagnotta.bitcoin.utils.Utils.getNextWorkRequired(last, headers, receivedHeader, blockChainParameters);

				if (!Utils.isShaMatchesTarget(Utils.computeBlockHeaderHash(receivedHeader), currentTarget)) {

					LOGGER.error("Block hash is not valid!");

					return;

				}

				headers.add(receivedHeader);
				last++;

			}

		}

	}

}
