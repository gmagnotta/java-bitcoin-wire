package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetDataMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinInvMessage;
import org.gmagnotta.bitcoin.message.impl.InventoryVector;
import org.gmagnotta.bitcoin.message.impl.InventoryVector.Type;
import org.gmagnotta.bitcoin.wire.Utils;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;

public class BitcoinInvMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload) throws BitcoinMessageSerializerException {
		
		// read varint
		VarInt count = new VarInt(payload, 0);
		
		// how many bytes represents the value?
		int len = count.getSizeInBytes();
		
		List<InventoryVector> vector = new ArrayList<InventoryVector>();
		
		for (int i = 0; i < (count.value); i++) {

			byte[] array = Arrays.copyOfRange(payload,  len + i * 36, len + i * 36 + 36);
			
			long type = Utils.readUint32LE(array, 0);
			
			Sha256Hash hash = Sha256Hash.wrap(Arrays.copyOfRange(array, 4, 36));
			
			vector.add(new InventoryVector(Type.valueOf((int)type), hash));
			
		}
		
		// return assembled message
		return new BitcoinInvMessage(vector);
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
