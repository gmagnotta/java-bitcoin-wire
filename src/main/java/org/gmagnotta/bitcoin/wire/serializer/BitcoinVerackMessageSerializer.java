package org.gmagnotta.bitcoin.wire.serializer;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.BitcoinVerackMessage;

public class BitcoinVerackMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload) throws Exception {
		
		// payload is zero byte length
		
		// return assembled message
		return new BitcoinVerackMessage();
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		return new byte[] {};

	}
	
}
