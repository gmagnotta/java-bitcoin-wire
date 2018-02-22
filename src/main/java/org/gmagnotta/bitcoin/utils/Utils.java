package org.gmagnotta.bitcoin.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.blockchain.BlockChainParameters;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.message.impl.DeserializedTransaction;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.wire.serializer.impl.BlockHeadersSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.TransactionSerializer;
import org.spongycastle.util.Arrays;

public class Utils {

	public static Sha256Hash computeBlockHeaderHash(BlockHeader blockHeader) {

		BlockHeadersSerializer blockHeadersSerializer = new BlockHeadersSerializer();

		return Sha256Hash.twiceOf(blockHeadersSerializer.serialize(blockHeader), 0, 80);

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

		String string = sha256.toReversedString();

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
	
	/**
	 * The parameter of this method represents a list of hashes. It requires a first hash of elements
	 * 
	 * @param arrayByteList
	 * @return
	 */
	public static Sha256Hash calculateMerkleRoot(List<Sha256Hash> arrayByteList) {
		
		if (arrayByteList.size() == 1) {
			
			return arrayByteList.get(0);
			
		}
		
		List<Sha256Hash> hashList = new ArrayList<Sha256Hash>();
		
		int len = arrayByteList.size() % 2 == 0 ? arrayByteList.size() : arrayByteList.size() + 1;
		
		for (int i = 0; i < (len / 2); i++) {
			
			Sha256Hash d1 = arrayByteList.get(i * 2);
			
			Sha256Hash d2 = arrayByteList.get(i * 2 +1 > (arrayByteList.size() - 1)? i*2 : i*2+1);
			
			byte[] concat = Arrays.concatenate(d1.getBytes(), d2.getBytes());
			
			hashList.add(Sha256Hash.twiceOf(concat));
			
		}
		
		return calculateMerkleRoot(hashList);
		
	}
	
	public static Sha256Hash calculateMerkleRootTransaction(List<Transaction> txList) {
		
		TransactionSerializer transactionSerializer = new TransactionSerializer();
		
		List<Sha256Hash> hashList = new ArrayList<Sha256Hash>();
		
		for (Transaction transaction : txList) {
			
			if (transaction instanceof DeserializedTransaction) {
				
				DeserializedTransaction hashedTransaction = (DeserializedTransaction) transaction;
				
				hashList.add(hashedTransaction.getTxId());
				
			} else {
			
				hashList.add(Sha256Hash.twiceOf(transactionSerializer.serialize(transaction)));
			
			}
			
		}
		
		return calculateMerkleRoot(hashList);
		
	}

	public static byte[] hash160(byte[] input) {
		byte[] out = new byte[20];
		RIPEMD160Digest rDigest = new RIPEMD160Digest();
		rDigest.update(input, 0, input.length);
		rDigest.doFinal(out, 0);
		return out;
	}
	
	public static Sha256Hash calculateTransactionHash(Transaction transaction) throws Exception {
		
		if (transaction instanceof DeserializedTransaction) {
			
			DeserializedTransaction d = (DeserializedTransaction) transaction;
			
			return d.getTxId();
			
		}
		
		throw new Exception();
		
	}
	
}
