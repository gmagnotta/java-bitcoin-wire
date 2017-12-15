package org.gmagnotta.bitcoin.wire.serializer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinGetHeadersMessage;
import org.gmagnotta.bitcoin.message.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.BlockHeaders;
import org.gmagnotta.bitcoin.wire.Utils;

import com.subgraph.orchid.encoders.Hex;

public class BitcoinHeadersMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload) throws Exception {
		
		// read varint
		VarInt varint = new VarInt(payload, 0);
		
		// how many bytes represents the value?
		int len = varint.getSizeInBytes();
		
		List<BlockHeaders> headers = new ArrayList<BlockHeaders>();
		
		BlockHeadersSerializer blockHeadersSerializer = new BlockHeadersSerializer();
		
		for (int i = 0; i < (varint.value + 1); i++) {

			byte[] array = Arrays.copyOfRange(payload,  len + i * 81, len + i * 81 + 81);
			
			headers.add(blockHeadersSerializer.deserialize(array));
		}
		
		// return assembled message
		return new BitcoinHeadersMessage(headers);
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BitcoinHeadersMessage message = ((BitcoinHeadersMessage) messageToSerialize);
		
		VarInt v = new VarInt(message.getHeaders().size());
		
		ByteBuffer buffer = ByteBuffer.allocate(4 + v.getSizeInBytes() + 81 * message.getHeaders().size() + 81);
		
		buffer.put(v.encode());
		
		BlockHeadersSerializer blockHeadersSerializer = new BlockHeadersSerializer();
		
		for (BlockHeaders header : message.getHeaders()) {
			
			buffer.put(blockHeadersSerializer.serialize(header));
			
		}
		
		buffer.put(Hex.decode("0000000000000000000000000000000000000000000000000000000000000000"));
		
		return buffer.array();

	}
	
}
