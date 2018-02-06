package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;

public class BitcoinHeadersMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload, int offset, int lenght) throws BitcoinMessageSerializerException {
		
		try {
			// read varint
			VarInt varint = new VarInt(payload, offset + 0);
			
			// how many bytes represents the value?
			int len = varint.getSizeInBytes();
			
			List<BlockHeader> headers = new ArrayList<BlockHeader>();
			
			BlockHeadersSerializer blockHeadersSerializer = new BlockHeadersSerializer();
			
			for (int i = 0; i < (varint.value); i++) {
	
				BlockHeader header = blockHeadersSerializer.deserialize(payload, offset + len + i * 81, 81);

				headers.add(header);

			}

			// return assembled message
			return new BitcoinHeadersMessage(headers);
		
		} catch (Exception ex) {
			throw new BitcoinMessageSerializerException("Exception", ex);
		}
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BitcoinHeadersMessage message = ((BitcoinHeadersMessage) messageToSerialize);
		
		VarInt v = new VarInt(message.getHeaders().size());
		
		ByteBuffer buffer = ByteBuffer.allocate(v.getSizeInBytes() + 81 * message.getHeaders().size());
		
		buffer.put(v.encode());
		
		BlockHeadersSerializer blockHeadersSerializer = new BlockHeadersSerializer();
		
		for (BlockHeader header : message.getHeaders()) {
			
			buffer.put(blockHeadersSerializer.serialize(header));
			
		}
		
		return buffer.array();

	}
	
	public static void main(String[] args) {
		
		VarInt v = new VarInt(2);
		
		v.encode();
	}
	
}

