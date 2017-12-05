package org.gmagnotta.bitcoin.wire.serializer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinGetBlocksMessage;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.Utils;

import com.subgraph.orchid.encoders.Hex;

public class BitcoinGetBlocksMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload) throws Exception {
		
		// nonce
		long version = Utils.readUint32LE(payload, 0);
		
		// read varint
		VarInt varint = new VarInt(payload, 4);
		
		// how many bytes represents the value?
		int len = varint.getSizeInBytes();
		
		List<Sha256Hash> hashes = new ArrayList<Sha256Hash>();
		
		for (int i = 0; i < (varint.value + 1); i++) {

			byte[] array = Arrays.copyOfRange(payload,  4 + len + i * 32, 4 + len + i * 32 + 32);
			
			Sha256Hash hash = Sha256Hash.wrapReversed(array);
			
			hashes.add(hash);
		}
		
		// return assembled message
		return new BitcoinGetBlocksMessage(version, hashes);
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BitcoinGetBlocksMessage message = ((BitcoinGetBlocksMessage) messageToSerialize);
		
		VarInt v = new VarInt(message.getHash().size());
		
		ByteBuffer buffer = ByteBuffer.allocate(4 + v.getSizeInBytes() + (32 * message.getHash().size()) + 32);
		
		buffer.put(Utils.writeInt32LE((int) message.getVersion()));
		
		buffer.put(v.encode());

		for (Sha256Hash hash : message.getHash()) {
			
			buffer.put(hash.getReversedBytes());
			
		}
		
		buffer.put(Hex.decode("0000000000000000000000000000000000000000000000000000000000000000"));
		
		return buffer.array();

	}
	
}
