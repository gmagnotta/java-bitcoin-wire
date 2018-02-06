package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BlockMessage;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.wire.Utils;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;

import com.subgraph.orchid.encoders.Hex;

public class BitcoinBlockMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload, int offset, int lenght) throws BitcoinMessageSerializerException {
		
		try {
			
			BlockHeadersSerializer blockHeadersSerializer = new BlockHeadersSerializer();
			
			BlockHeader header = blockHeadersSerializer.deserialize(payload, offset, lenght);
			
			VarInt v = new VarInt(header.getTxnCount());
			
			TransactionSerializer transactionSerializer = new TransactionSerializer();
			
			transactionSerializer.deserialize(payload,  offset + 80 + v.getSizeInBytes(), payload.length);
			// return assembled message
			return null;
		
		} catch (Exception ex) {
			throw new BitcoinMessageSerializerException("Exception", ex);
		}
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BitcoinHeadersMessage message = ((BitcoinHeadersMessage) messageToSerialize);
		
		VarInt v = new VarInt(message.getHeaders().size());
		
		ByteBuffer buffer = ByteBuffer.allocate(4 + v.getSizeInBytes() + 81 * message.getHeaders().size() + 81);
		
		buffer.put(v.encode());
		
		BlockHeadersSerializer blockHeadersSerializer = new BlockHeadersSerializer();
		
		for (BlockHeader header : message.getHeaders()) {
			
			buffer.put(blockHeadersSerializer.serialize(header));
			
		}
		
		buffer.put(Hex.decode("0000000000000000000000000000000000000000000000000000000000000000"));
		
		return buffer.array();

	}
	
	public static void main(String[] args) {
		
		VarInt v = new VarInt(2);
		
		v.encode();
	}
	
}

