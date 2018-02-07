package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.wire.Utils;

public class BlockHeadersSerializer {
	
	public BlockHeader deserialize(byte[] payload, int offset, int lenght) throws UnknownHostException {

		long version = Utils.readUint32LE(payload, offset + 0);
		
		Sha256Hash prevBlock = Sha256Hash.wrapReversed(payload, offset + 4, 32);

		Sha256Hash merkle = Sha256Hash.wrapReversed(payload, offset + 32 + 4, 32);
		
		long timestamp = Utils.readUint32LE(payload, offset + 32 + 4 + 32);
		
		long bits = Utils.readUint32LE(payload, offset + 32 + 4 + 32 + 4);
		
		long nonce = Utils.readUint32LE(payload, offset + 32 + 4 + 32 + 4 + 4);
		
		// read varint
		VarInt varint = new VarInt(payload, offset + 32 + 4 + 32 + 4 + 4 + 4);

		return new BlockHeader(version, prevBlock, merkle, timestamp, bits, nonce, varint.value);

	}

	public byte[] serialize(BlockHeader blockHeaders) {

		ByteBuffer buffer = ByteBuffer.allocate(4 + 32 + 32 + 4 + 4 + 4 + 1);
		
		buffer.put(Utils.writeInt32LE(blockHeaders.getVersion()));
		
		buffer.put(blockHeaders.getPrevBlock().getReversedBytes());
		
		buffer.put(blockHeaders.getMerkleRoot().getReversedBytes());
		
		buffer.put(Utils.writeInt32LE(blockHeaders.getTimestamp()));
		
		buffer.put(Utils.writeInt32LE(blockHeaders.getBits()));
		
		buffer.put(Utils.writeInt32LE(blockHeaders.getNonce()));
		
		// Please note that per bitcoin specification the txn_count is always 0. This is not used for hashing
		//VarInt varInt = new VarInt(blockHeaders.getTxnCount());
		//buffer.put(varInt.encode());
		
		buffer.put((byte) 0);

		return buffer.array();
		
	}

}
