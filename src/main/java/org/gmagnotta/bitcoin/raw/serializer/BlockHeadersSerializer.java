package org.gmagnotta.bitcoin.raw.serializer;

import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.raw.BlockHeaders;
import org.gmagnotta.bitcoin.raw.NetworkAddress;
import org.gmagnotta.bitcoin.raw.Utils;

public class BlockHeadersSerializer {
	
	private boolean serializeTime;
	
	public BlockHeadersSerializer() {
	}
	
	public BlockHeaders deserialize(byte[] payload) throws UnknownHostException {

		BlockHeaders blockHeaders = null;

		long version = Utils.readUint32LE(payload, 0);
		
		Sha256Hash prevBlock = Sha256Hash.wrapReversed(Arrays.copyOfRange(payload, 4, 32 + 4));

		BigInteger services = Utils.readUint64LE(payload, 4);

		InetAddress inetAddress = Inet6Address.getByAddress(Arrays.copyOfRange(payload, 12, 28));
		// only
		// ipv4
		// for
		// the
		// moment

		int port = Utils.readUint16BE(payload, 28);

//		} else {
//
//			BigInteger services = Utils.readUint64LE(payload, 0);
//
//			InetAddress inetAddress = Inet6Address.getByAddress(Arrays.copyOfRange(payload, /* 8 */20, 24)); // take
//																												// only
//																												// ipv4
//																												// for
//																												// the
//																												// moment
//
//			int port = Utils.readUint16BE(payload, 24);
//
//			blockHeaders = new NetworkAddress(0, services, inetAddress, port);
//
//		}

		return blockHeaders;

	}

	public byte[] serialize(NetworkAddress networkAddress) {

		ByteBuffer buffer;
		
		if (serializeTime) {

			buffer = ByteBuffer.allocate(4 + 8 + 16 + 2);

			buffer.put(Utils.writeInt32LE(networkAddress.getTime()));

		} else {

			buffer = ByteBuffer.allocate(8 + 16 + 2);
			
		}

		buffer.put(Utils.writeInt64LE(networkAddress.getServices().longValue()));

		if (networkAddress.getInetAddress() instanceof Inet6Address) {

			buffer.put(networkAddress.getInetAddress().getAddress());

		} else {

			buffer.put(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF,
					(byte) 0xFF });

			buffer.put(networkAddress.getInetAddress().getAddress());

		}

		buffer.put(Utils.writeInt16BE(networkAddress.getPort()));

		return buffer.array();

	}

}
