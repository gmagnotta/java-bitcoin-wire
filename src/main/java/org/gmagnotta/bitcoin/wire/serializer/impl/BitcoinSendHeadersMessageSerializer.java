package org.gmagnotta.bitcoin.wire.serializer.impl;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinSendHeadersMessage;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;

public class BitcoinSendHeadersMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload) throws BitcoinMessageSerializerException {
		
		// payload is zero byte length
		
		// return assembled message
		return new BitcoinSendHeadersMessage();
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		return new byte[] {};

	}
	
}
