package org.gmagnotta.bitcoin.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.wire.serializer.impl.BlockHeadersSerializer;

public class Utils {
	
	public static Sha256Hash computeBlockHeaderHash(BlockHeader blockHeader) {
		
		BlockHeadersSerializer blockHeadersSerializer = new BlockHeadersSerializer();
		
		Sha256Hash blockHash = Sha256Hash.twiceOf(Arrays.copyOfRange(blockHeadersSerializer.serialize(blockHeader), 0, 4 + 32 + 32  + 4 + 4 + 4));
		
		return blockHash;
		
	}
	
	public static double uncompact(int compact) {

		int mantissa = (0x00FFFFFF & compact);

		int exp = compact >>> 24;

		double d = Math.pow(256, exp - 3);

		return (d * mantissa);

	}
	
	public static boolean isShaMatchesTarget(String sha256, int target) {
		
		double uncompact = uncompact(target);
		
		BigInteger b = new BigInteger(sha256, 16);
		
		if (b.doubleValue() <= uncompact) {
			
			return true;
			
		} else {

			return false;
			
		}
		
	}
	
}
