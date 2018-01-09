package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.impl.BlockHeaders;
import org.gmagnotta.bitcoin.wire.Utils;

public class BlockHeadersSerializer {
	
	public BlockHeaders deserialize(byte[] payload) throws UnknownHostException {

		long version = Utils.readUint32LE(payload, 0);
		
		Sha256Hash prevBlock = Sha256Hash.wrapReversed(Arrays.copyOfRange(payload, 4, 32 + 4));

		Sha256Hash merkle = Sha256Hash.wrapReversed(Arrays.copyOfRange(payload, 32 + 4, 32 + 4 + 32));
		
		long timestamp = Utils.readUint32LE(payload, 32 + 4 + 32);
		
		long bits = Utils.readUint32LE(payload, 32 + 4 + 32 + 4);
		
		long nonce = Utils.readUint32LE(payload, 32 + 4 + 32 + 4 + 4);
		
		// read varint
		VarInt varint = new VarInt(payload, 32 + 4 + 32 + 4 + 4 + 4);

		return new BlockHeaders(version, prevBlock, merkle, timestamp, bits, nonce, varint.value);

	}

	public byte[] serialize(BlockHeaders blockHeaders) {

		ByteBuffer buffer = ByteBuffer.allocate(4 + 32 + 32 + 4 + 4 + 4 + 1);
		
		buffer.put(Utils.writeInt32LE(blockHeaders.getVersion()));
		
		buffer.put(blockHeaders.getPrevBlock().getReversedBytes());
		
		buffer.put(blockHeaders.getMerkleRoot().getReversedBytes());
		
		buffer.put(Utils.writeInt32LE(blockHeaders.getTimestamp()));
		
		buffer.put(Utils.writeInt32LE(blockHeaders.getBits()));
		
		buffer.put(Utils.writeInt32LE(blockHeaders.getNonce()));
		
		VarInt varInt = new VarInt(blockHeaders.getTxnCount());
		
		buffer.put(varInt.encode());

		return buffer.array();
		
	}

}
