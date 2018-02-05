package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.gmagnotta.bitcoin.message.impl.NetworkAddress;
import org.gmagnotta.bitcoin.wire.Utils;

public class NetworkAddressSerializer {
	
	private boolean serializeTime;
	
	public NetworkAddressSerializer() {
		this(true);
	}
	
	public NetworkAddressSerializer(boolean serializeTime) {
		this.serializeTime = serializeTime;
	}

	public NetworkAddress deserialize(byte[] payload, int offset, int lenght) throws UnknownHostException {

		NetworkAddress networkAddress = null;

		if (lenght == 30) {

			long time = Utils.readUint32LE(payload, offset + 0);

			BigInteger services = Utils.readUint64LE(payload, offset + 4);

			InetAddress inetAddress = Inet6Address.getByAddress(Arrays.copyOfRange(payload, offset + 12, offset + 28));
			// only
			// ipv4
			// for
			// the
			// moment

			int port = Utils.readUint16BE(payload, offset + 28);

			networkAddress = new NetworkAddress(time, services, inetAddress, port);

		} else {

			BigInteger services = Utils.readUint64LE(payload, offset + 0);

			InetAddress inetAddress = Inet6Address.getByAddress(Arrays.copyOfRange(payload, /* 8 */ offset + 20, offset + 24)); // take
																												// only
																												// ipv4
																												// for
																												// the
																												// moment

			int port = Utils.readUint16BE(payload, offset + 24);

			networkAddress = new NetworkAddress(0, services, inetAddress, port);

		}

		return networkAddress;

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
