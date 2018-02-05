package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import org.bitcoinj.core.Sha256Hash;
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
	public BitcoinMessage deserialize(byte[] payload) throws BitcoinMessageSerializerException {
		
		try {
			// read varint
			VarInt version = new VarInt(payload, 0);
			
			// how many bytes represents the value?
			int len = version.getSizeInBytes();
			
			Sha256Hash prevBlock = Sha256Hash.wrapReversed(Arrays.copyOfRange(payload, len, len + 32));

			Sha256Hash merkle = Sha256Hash.wrapReversed(Arrays.copyOfRange(payload, len + 32, len + 32 + 32));
			
			long timestamp = Utils.readUint32LE(payload, len + 32 + 32);
			
			long bits = Utils.readUint32LE(payload, len + 32 + 32 + 4);
			
			long nonce = Utils.readUint32LE(payload, len + 32 + 32 + 4 + 4);
			
			// read varint
			VarInt txnCount = new VarInt(payload, len + 32 + 32 + 4 + 4 + 4);

//			byte[] array = Arrays.copyOfRange(payload,  padding + i * 80, padding + i * 80 + 80);

			BlockMessage blockHeader = new BlockMessage(version.value, prevBlock, merkle, timestamp, bits, nonce, txnCount.value, new ArrayList<Object>());
			
			// return assembled message
			return blockHeader;
		
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

