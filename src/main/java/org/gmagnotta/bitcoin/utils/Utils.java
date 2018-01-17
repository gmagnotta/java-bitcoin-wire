package org.gmagnotta.bitcoin.utils;

import java.math.BigInteger;
import java.util.Arrays;

import org.bitcoinj.core.Sha256Hash;
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

	public static long calculateNextWorkRequired(long finalTimestap, long initialTimestamp, long nPowTargetTimespan,
			int nbits) {

		// https://bitcoin.stackexchange.com/questions/22581/how-was-the-new-target-for-block-32256-calculated?rq=1

		// Limit adjustment step
		long nActualTimespan = finalTimestap - initialTimestamp;

		if (nActualTimespan < nPowTargetTimespan / 4)
			nActualTimespan = nPowTargetTimespan / 4;
		if (nActualTimespan > nPowTargetTimespan * 4)
			nActualTimespan = nPowTargetTimespan * 4;

		// Retarget
		BigInteger d = uncompact(nbits);

		d = d.multiply(BigInteger.valueOf(nActualTimespan));
		d = d.divide(BigInteger.valueOf(nPowTargetTimespan));

		// if (bnNew > bnPowLimit)
		// bnNew = bnPowLimit;
		//
		return compact(d);

	}

}
