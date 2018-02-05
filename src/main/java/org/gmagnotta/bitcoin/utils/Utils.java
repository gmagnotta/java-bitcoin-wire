package org.gmagnotta.bitcoin.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.blockchain.BlockChainParameters;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.wire.serializer.impl.BlockHeadersSerializer;
import org.spongycastle.util.encoders.Hex;

public class Utils {

	public static Sha256Hash computeBlockHeaderHash(BlockHeader blockHeader) {

		BlockHeadersSerializer blockHeadersSerializer = new BlockHeadersSerializer();

		Sha256Hash blockHash = Sha256Hash
				.twiceOf(Arrays.copyOfRange(blockHeadersSerializer.serialize(blockHeader), 0, 4 + 32 + 32 + 4 + 4 + 4));

		return blockHash;

	}

	public static BigInteger uncompact(int compact) {

		int mantissa = (0x00FFFFFF & compact);

		int exp = compact >>> 24;

		BigInteger b = BigInteger.valueOf(mantissa);

		return b.shiftLeft((exp - 3) * 8);

	}

	public static long compact(BigInteger bigInteger) {

		byte[] b = bigInteger.toByteArray();

		int len = b.length;

		byte[] b2 = new byte[4];

		b2[0] = (byte) len;
		b2[1] = b[0];
		b2[2] = b[1];
		b2[3] = b[2];

		return new BigInteger(b2).longValue();

	}

	public static boolean isShaMatchesTarget(Sha256Hash sha256, int target) {

		String string = new String(Hex.encode(sha256.getReversedBytes()));

		return isShaMatchesTarget(string, target);

	}

	public static boolean isShaMatchesTarget(String sha256, int target) {

		// see
		// https://bitcoin.stackexchange.com/questions/23912/how-is-the-target-section-of-a-block-header-calculated

		BigInteger uncompacted = uncompact(target);

		BigInteger b = new BigInteger(sha256, 16);

		if (b.compareTo(uncompacted) <= 0) {

			return true;

		} else {

			return false;

		}

	}

	public static long getNextWorkRequired(long height, BlockChain blockchain, BlockHeader pblock,
			BlockChainParameters blockChainParameters) {

		BlockHeader pindexLast = blockchain.getBlockHeader((int) height);
		
		if ((height + 1) % blockChainParameters.getDifficultyAdjustmentInterval() != 0) {

			if (blockChainParameters.getAllowMinDifficultyBlocks()) {

				// Special difficulty rule for testnet:
				// If the new block's timestamp is more than 2* 10 minutes
				// then allow mining of a min-difficulty block.
				if (pblock.getTimestamp() > pindexLast.getTimestamp()
						+ blockChainParameters.getTargetSpacing() * 2) {

					return compact(blockChainParameters.getPowLimit());

				} else {

					// Return the last non-special-min-difficulty-rules-block
					long pindex = height;
					while (pindex % blockChainParameters.getDifficultyAdjustmentInterval() != 0 &&
							blockchain.getBlockHeader((int) pindex).getBits() == compact(blockChainParameters.getPowLimit())) {
						pindex--;
					}

					return blockchain.getBlockHeader((int) pindex).getBits();

				}

			}

			return pindexLast.getBits();

		}

		// Go back by what we want to be 14 days worth of blocks
		long nHeightFirst = height - (blockChainParameters.getDifficultyAdjustmentInterval() - 1);

		BlockHeader pindexFirst = blockchain.getBlockHeader((int) nHeightFirst);

		return calculateNextWorkRequired(pindexLast, pindexFirst.getTimestamp(), blockChainParameters);

	}

	public static long calculateNextWorkRequired(BlockHeader last, long firstBlockTime, BlockChainParameters blockChainParameters) {

		if (blockChainParameters.getPowNoRetargeting()) {
			return last.getBits();
		}

		// https://bitcoin.stackexchange.com/questions/22581/how-was-the-new-target-for-block-32256-calculated?rq=1

		// Limit adjustment step
		long nActualTimespan = last.getTimestamp() - firstBlockTime;

		if (nActualTimespan < blockChainParameters.getTargetTimespan() / 4)
			nActualTimespan = blockChainParameters.getTargetTimespan() / 4;
		if (nActualTimespan > blockChainParameters.getTargetTimespan() * 4)
			nActualTimespan = blockChainParameters.getTargetTimespan() * 4;

		// Retarget
		BigInteger d = uncompact((int) last.getBits());

		d = d.multiply(BigInteger.valueOf(nActualTimespan));
		d = d.divide(BigInteger.valueOf(blockChainParameters.getTargetTimespan()));

		if (d.compareTo(blockChainParameters.getPowLimit()) > 0) {
			return compact(blockChainParameters.getPowLimit());
		}

		return compact(d);

	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public static long calculatBlockIndexRange(long index) {
		
		return Math.floorDiv(index, 2016);
		
	}
	
	public static boolean isPeerNetworkNode(BigInteger bigInteger) {
		
		BigInteger result = bigInteger.and(BigInteger.ONE);
		
		if (result.equals(BigInteger.ONE))
			return true;
		
		return false;
		
	}

	public static byte[] reverseBytesClone(byte[] rawHashBytes) {

		byte[] copy = ArrayUtils.clone(rawHashBytes);

		ArrayUtils.reverse(copy);
		
		return copy;

	}
	
	public static byte[] reverseBytesnoClone(byte[] rawHashBytes) {

		ArrayUtils.reverse(rawHashBytes);
		
		return rawHashBytes;

	}

}
