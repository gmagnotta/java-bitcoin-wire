package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetDataMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.InventoryVector;
import org.gmagnotta.bitcoin.wire.Utils;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;

public class BitcoinGetDataMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload, int offset, int lenght) throws BitcoinMessageSerializerException {
		
		// nonce
		long version = Utils.readUint32LE(payload, offset + 0);
		
		// read varint
		VarInt varint = new VarInt(payload, offset + 4);
		
		// how many bytes represents the value?
		int len = varint.getSizeInBytes();
		
		List<Sha256Hash> hashes = new ArrayList<Sha256Hash>();
		
		for (int i = 0; i < (varint.value + 1); i++) {

			byte[] array = Arrays.copyOfRange(payload,  offset + 4 + len + i * 32, offset + 4 + len + i * 32 + 32);
			
			Sha256Hash hash = Sha256Hash.wrapReversed(array);
			
			hashes.add(hash);
		}
		
		// return assembled message
		return new BitcoinGetHeadersMessage(version, hashes);
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BitcoinGetDataMessage message = ((BitcoinGetDataMessage) messageToSerialize);
		
		VarInt count = new VarInt(message.getInventoryVectors().size());
		
		ByteBuffer buffer = ByteBuffer.allocate(count.getSizeInBytes() + (36 * message.getInventoryVectors().size()));
		
		buffer.put(count.encode());
		
		for (InventoryVector iv : message.getInventoryVectors()) {
			
			buffer.put(Utils.writeInt32LE((int) iv.getType().ordinal()));
			
			buffer.put(iv.getHash().getBytes());
			
		}
		
		return buffer.array();

	}
	
}
