package org.gmagnotta.bitcoin.utils;

import java.math.BigInteger;
import java.util.Arrays;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.wire.serializer.impl.BlockHeadersSerializer;

import com.subgraph.orchid.encoders.Hex;

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
	
	public static boolean isShaMatchesTarget(Sha256Hash sha256, int target) {
		
		String string = new String(Hex.encode(sha256.getReversedBytes()));
		
		return isShaMatchesTarget(string, target);
		
	}
	
	public static boolean isShaMatchesTarget(String sha256, int target) {
		
		// see https://bitcoin.stackexchange.com/questions/23912/how-is-the-target-section-of-a-block-header-calculated
		
		double uncompact = uncompact(target);
		
		BigInteger b = new BigInteger(sha256, 16);
		
		if (b.doubleValue() <= uncompact) {
			
			return true;
			
		} else {

			return false;
			
		}
		
	}

}
