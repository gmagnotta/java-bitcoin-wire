package org.gmagnotta.bitcoin.raw.serializer;

import java.nio.ByteBuffer;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.BitcoinVerackMessage;
import org.gmagnotta.bitcoin.message.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.raw.Utils;

public class BitcoinVerackMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload) throws Exception {
		
		// payload is zero byte length
		
		// return assembled message
		return new BitcoinVerackMessage();
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BitcoinVersionMessage message = ((BitcoinVersionMessage) messageToSerialize);
		
		VarInt v = new VarInt(message.getUserAgent().length());

		ByteBuffer buffer = ByteBuffer.allocate(85 + message.getUserAgent().length() + v.encode().length);
		
		buffer.put(Utils.writeInt32LE((int) message.getVersion()));
		
		buffer.put(Utils.writeInt64LE(message.getServices().longValue()));
		
		buffer.put(Utils.writeInt64LE(message.getTimestamp().longValue()));
		
		NetworkAddressSerializer networkAddressSerializer = new NetworkAddressSerializer(false);
		
		buffer.put(networkAddressSerializer.serialize(message.getAddressReceiving()));
		
		buffer.put(networkAddressSerializer.serialize(message.getAddressEmitting()));
		
		buffer.put(Utils.writeInt64LE(message.getNonce().longValue()));
		
		buffer.put(v.encode());
		
		buffer.put(message.getUserAgent().getBytes());
		
		buffer.put(Utils.writeInt32LE((int) message.getStartHeight()));
		
		buffer.put(message.getRelay() == true ? (byte) 1 : (byte) 0);
		
		return buffer.array();

	}
	
}
