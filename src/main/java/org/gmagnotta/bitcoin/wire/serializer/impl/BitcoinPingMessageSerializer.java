package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.wire.Utils;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;

public class BitcoinPingMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload) throws BitcoinMessageSerializerException {
		
		// nonce
		BigInteger nonce = Utils.readUint64LE(payload, 0);

		// return assembled message
		return new BitcoinPingMessage(nonce);
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BitcoinPingMessage message = ((BitcoinPingMessage) messageToSerialize);
		
		ByteBuffer buffer = ByteBuffer.allocate(8);
		
		buffer.put(Utils.writeInt64LE(message.getNonce().longValue()));
		
		return buffer.array();

	}
	
}
