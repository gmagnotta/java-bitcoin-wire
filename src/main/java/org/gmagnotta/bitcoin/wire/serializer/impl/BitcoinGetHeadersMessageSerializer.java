package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetHeadersMessage;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.wire.Utils;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;
import org.spongycastle.util.encoders.Hex;

public class BitcoinGetHeadersMessageSerializer implements BitcoinMessageSerializer {

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

			Sha256Hash hash = Sha256Hash.wrapReversed(payload, offset + 4 + len + i * 32, 32);
			
			hashes.add(hash);
		}
		
		// return assembled message
		return new BitcoinGetHeadersMessage(version, hashes);
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BitcoinGetHeadersMessage message = ((BitcoinGetHeadersMessage) messageToSerialize);
		
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
