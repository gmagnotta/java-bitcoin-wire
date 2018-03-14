package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinRejectMessage;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;

public class BitcoinRejectMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload, int offset, int lenght) throws BitcoinMessageSerializerException {
		
		try {
			// read varint
			VarInt varint = new VarInt(payload, offset + 0);
			
			String message = new String(payload, varint.getSizeInBytes(), (int)varint.value);
			
			byte code = payload[(int)varint.value];
			
			return new BitcoinRejectMessage(message, code, "", new byte[]{});
			
		} catch (Exception ex) {
			throw new BitcoinMessageSerializerException("Exception", ex);
		}
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BitcoinRejectMessage message = ((BitcoinRejectMessage) messageToSerialize);
		
		byte[] messageBytes = message.getMessage().getBytes();
		VarInt v = new VarInt(messageBytes.length);
//		stream.write(messageBytes);
//		stream.write(code.code);

		byte[] messageBytes2 = message.getReason().getBytes();
		VarInt v2 = new VarInt(messageBytes2.length);
		
		ByteBuffer buffer = ByteBuffer.allocate(v.getSizeInBytes() + messageBytes.length + 1 + v2.getSizeInBytes() + messageBytes2.length);
		
		buffer.put(v.encode());
		
		buffer.put(messageBytes);
		
		buffer.put(message.getCcode());
		
		buffer.put(v2.encode());
		
		buffer.put(messageBytes2);
		
		return buffer.array();

	}
	
}
