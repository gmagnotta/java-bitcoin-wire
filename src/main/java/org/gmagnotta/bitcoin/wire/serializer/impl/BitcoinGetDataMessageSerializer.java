package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetDataMessage;
import org.gmagnotta.bitcoin.message.impl.InventoryVector;
import org.gmagnotta.bitcoin.message.impl.InventoryVector.Type;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.wire.Utils;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;

public class BitcoinGetDataMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload, int offset, int lenght) throws BitcoinMessageSerializerException {
		
		// read varint
		VarInt varint = new VarInt(payload, offset + 0);
		
		// how many bytes represents the value?
		int len = varint.getSizeInBytes();
		
		List<InventoryVector> vectors = new ArrayList<InventoryVector>();
		
		for (int i = 0; i < varint.value; i++) {

			long type = Utils.readUint32LE(payload, offset + len + i * 36);
			
			Sha256Hash hash = Sha256Hash.wrapReversed(payload, offset + len + 4 + i * 36, 32);
			
			vectors.add(new InventoryVector(Type.valueOf((int)type), hash));
		}
		
		// return assembled message
		return new BitcoinGetDataMessage(vectors);
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BitcoinGetDataMessage message = ((BitcoinGetDataMessage) messageToSerialize);
		
		VarInt count = new VarInt(message.getInventoryVectors().size());
		
		ByteBuffer buffer = ByteBuffer.allocate(count.getSizeInBytes() + (36 * message.getInventoryVectors().size()));
		
		buffer.put(count.encode());
		
		for (InventoryVector iv : message.getInventoryVectors()) {
			
			buffer.put(Utils.writeInt32LE((int) iv.getType().ordinal()));
			
			buffer.put(iv.getHash().getReversedBytes());
			
		}
		
		return buffer.array();

	}
	
}
