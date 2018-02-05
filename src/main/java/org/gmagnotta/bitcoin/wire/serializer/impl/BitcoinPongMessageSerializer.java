package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;
import org.gmagnotta.bitcoin.wire.Utils;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;

public class BitcoinPongMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload, int offset, int lenght) throws BitcoinMessageSerializerException {
		
		// nonce
		BigInteger nonce = Utils.readUint64LE(payload, offset + 0);

		// return assembled message
		return new BitcoinPongMessage(nonce);
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BitcoinPongMessage message = ((BitcoinPongMessage) messageToSerialize);
		
		ByteBuffer buffer = ByteBuffer.allocate(8);
		
		buffer.put(Utils.writeInt64LE(message.getNonce().longValue()));
		
		return buffer.array();

	}
	
}
