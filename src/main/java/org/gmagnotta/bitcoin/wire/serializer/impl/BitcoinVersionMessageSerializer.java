package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.message.impl.NetworkAddress;
import org.gmagnotta.bitcoin.wire.Utils;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;

public class BitcoinVersionMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload) throws BitcoinMessageSerializerException {
		
		try {
			
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
			
		} catch (Exception ex) {
			throw new BitcoinMessageSerializerException("Exception", ex);
		}
		
	}

	@Override
	public byte[] serialize(BitcoinMessage messageToSerialize) throws BitcoinMessageSerializerException {
		
		BitcoinVersionMessage message = ((BitcoinVersionMessage) messageToSerialize);
		
		VarInt v = new VarInt(message.getUserAgent().length());

		ByteBuffer buffer = ByteBuffer.allocate(85 + message.getUserAgent().length() + v.encode().length);
		
		buffer.put(Utils.writeInt32LE((int) message.getVersion()));
		
		buffer.put(Utils.writeInt64LE(message.getServices().longValue()));
		
		buffer.put(Utils.writeInt64LE(message.getTimestamp().longValue()));
		
		NetworkAddressSerializer networkAddressSerializer = new NetworkAddressSerializer(false);
		
		buffer.put(networkAddressSerializer.serialize(message.getAddressReceiving()));
		
		buffer.put(networkAddressSerializer.serialize(message.getAddressEmitting()));
		
		buffer.put(Utils.writeInt64LE(message.getNonce().longValue()));
		
		buffer.put(v.encode());
		
		buffer.put(message.getUserAgent().getBytes());
		
		buffer.put(Utils.writeInt32LE((int) message.getStartHeight()));
		
		buffer.put(message.getRelay() == true ? (byte) 1 : (byte) 0);
		
		return buffer.array();

	}
	
}
