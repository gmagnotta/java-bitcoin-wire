package org.gmagnotta.bitcoin.wire.serializer;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.BitcoinRejectMessage;
import org.gmagnotta.bitcoin.message.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.message.NetworkAddress;
import org.gmagnotta.bitcoin.wire.Utils;

public class BitcoinRejectMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload) throws Exception {
		
		// version
		long version = Utils.readUint32LE(payload, 0);

		// services
		BigInteger services = Utils.readUint64LE(payload, 4);

		// timestamp
		BigInteger timestamp = Utils.readUint64LE(payload, 12);
		
		NetworkAddressSerializer networkAddressSerializer = new NetworkAddressSerializer(false);

		// addre_recv
		NetworkAddress addressReceiving = networkAddressSerializer.deserialize(Arrays.copyOfRange(payload, 20, 20 + 26));

		// addre_from
		NetworkAddress addressEmitting = networkAddressSerializer.deserialize(Arrays.copyOfRange(payload, 46, 46 + 26));

		// nonce
		BigInteger nonce = Utils.readUint64LE(payload, 72);

		// user agent
		byte len = payload[80];
		String userAgent = null;
		if (len > 0) {

			userAgent = new String(Arrays.copyOfRange(payload, 81, 81 + len));

		}

		// start height
		long startHeight = Utils.readUint32LE(payload, 81 + len);

		boolean relay = false;

		if (version >= 70001) {

			byte b = payload[81 + len + 4];

			if (b == 0) {
				relay = false;
			} else {
				relay = true;
			}

		}

		// return assembled message
		return new BitcoinVersionMessage((int) version, services, timestamp, addressReceiving, addressEmitting, nonce,
				userAgent, startHeight, relay);
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) {
		
		BitcoinRejectMessage message = ((BitcoinRejectMessage) messageToSerialize);
		
		byte[] messageBytes = message.getMessage().getBytes();
		VarInt v = new VarInt(messageBytes.length);
//		stream.write(messageBytes);
//		stream.write(code.code);

		byte[] messageBytes2 = message.getReason().getBytes();
		VarInt v2 = new VarInt(messageBytes2.length);
		
		ByteBuffer buffer = ByteBuffer.allocate(v.getSizeInBytes() + messageBytes.length + 1 + v2.getSizeInBytes() + messageBytes2.length);
		
		buffer.put(v.encode());
		
		buffer.put(messageBytes);
		
		buffer.put(message.getCcode());
		
		buffer.put(v2.encode());
		
		buffer.put(messageBytes2);
		
		return buffer.array();

	}
	
}
