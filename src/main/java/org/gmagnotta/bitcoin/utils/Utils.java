package org.gmagnotta.bitcoin.utils;

import java.util.Arrays;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeaders;
import org.gmagnotta.bitcoin.wire.serializer.impl.BlockHeadersSerializer;

public class Utils {
	
	public static Sha256Hash computeBlockHeaderHash(BlockHeaders blockHeader) {
		
		BlockHeadersSerializer blockHeadersSerializer = new BlockHeadersSerializer();
		
		Sha256Hash blockHash = Sha256Hash.twiceOf(Arrays.copyOfRange(blockHeadersSerializer.serialize(blockHeader), 0, 4 + 32 + 32  + 4 + 4 + 4));
		
		return blockHash;
		
	}

}
