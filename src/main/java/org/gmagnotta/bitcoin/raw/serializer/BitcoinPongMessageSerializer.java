package org.gmagnotta.bitcoin.raw.serializer;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.BitcoinPongMessage;
import org.gmagnotta.bitcoin.raw.Utils;

public class BitcoinPongMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload) throws Exception {
		
		// nonce
		BigInteger nonce = Utils.readUint64LE(payload, 0);

		// return assembled message
		return new BitcoinPingMessage(nonce);
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BitcoinPongMessage message = ((BitcoinPongMessage) messageToSerialize);
		
		ByteBuffer buffer = ByteBuffer.allocate(8);
		
		buffer.put(Utils.writeInt64LE(message.getNonce().longValue()));
		
		return buffer.array();

	}
	
}
