package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.impl.BlockHeaders;
import org.gmagnotta.bitcoin.message.impl.NetworkAddress;
import org.gmagnotta.bitcoin.wire.Utils;

public class BlockHeadersSerializer {
	
	private boolean serializeTime;
	
	public BlockHeadersSerializer() {
	}
	
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

	public byte[] serialize(BlockHeaders networkAddress) {

//		ByteBuffer buffer;
//		
//		if (serializeTime) {
//
//			buffer = ByteBuffer.allocate(4 + 8 + 16 + 2);
//
//			buffer.put(Utils.writeInt32LE(networkAddress.getTime()));
//
//		} else {
//
//			buffer = ByteBuffer.allocate(8 + 16 + 2);
//			
//		}
//
//		buffer.put(Utils.writeInt64LE(networkAddress.getServices().longValue()));
//
//		if (networkAddress.getInetAddress() instanceof Inet6Address) {
//
//			buffer.put(networkAddress.getInetAddress().getAddress());
//
//		} else {
//
//			buffer.put(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF,
//					(byte) 0xFF });
//
//			buffer.put(networkAddress.getInetAddress().getAddress());
//
//		}
//
//		buffer.put(Utils.writeInt16BE(networkAddress.getPort()));
//
//		return buffer.array();
		
		return null;

	}

}
