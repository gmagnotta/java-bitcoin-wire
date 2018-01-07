package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bitcoinj.core.VarInt;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinAddrMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.message.impl.NetworkAddress;
import org.gmagnotta.bitcoin.wire.Utils;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinMessageSerializerException;

public class BitcoinAddrMessageSerializer implements BitcoinMessageSerializer {

	@Override
	public BitcoinMessage deserialize(byte[] payload) throws BitcoinMessageSerializerException {
		
		try {
			// read varint
			VarInt varint = new VarInt(payload, 0);
			
			// how many bytes represents the value?
			int len = varint.getSizeInBytes();
			
			// how many items?
			long count = varint.value;
			
			List<NetworkAddress> networkAddresses = new ArrayList<NetworkAddress>();
			
			for (int i = 0; i < count; i ++) {
			
				byte[] array = Arrays.copyOfRange(payload, 30 * i + len, 30 * i + 30 + len);
				
				// deserialize networkaddress
				NetworkAddress networkAddress = new NetworkAddressSerializer().deserialize(array);
				
				networkAddresses.add(networkAddress);

			}
	
			// return assembled message
			return new BitcoinAddrMessage(networkAddresses);
			
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
