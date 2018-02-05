package org.gmagnotta.bitcoin.wire.serializer.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
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
	public BitcoinMessage deserialize(byte[] payload, int offset, int lenght) throws BitcoinMessageSerializerException {
		
		try {
			// read varint
			VarInt varint = new VarInt(payload, offset + 0);
			
			// how many bytes represents the value?
			int len = varint.getSizeInBytes();
			
			// how many items?
			long count = varint.value;
			
			List<NetworkAddress> networkAddresses = new ArrayList<NetworkAddress>();
			
			for (int i = 0; i < count; i ++) {
			
				// deserialize networkaddress
				NetworkAddress networkAddress = new NetworkAddressSerializer().deserialize(payload, offset + 30 * i + len, 30);
				
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
		
		BitcoinAddrMessage message = ((BitcoinAddrMessage) messageToSerialize);
		
		VarInt v = new VarInt(message.getNetworkAddress().size());

		ByteBuffer buffer = ByteBuffer.allocate(30 * message.getNetworkAddress().size() + v.encode().length);
		
		buffer.put(v.encode());
		
		NetworkAddressSerializer networkAddressSerializer = new NetworkAddressSerializer(true);
		
		for (NetworkAddress n : message.getNetworkAddress()) {
		
			buffer.put(networkAddressSerializer.serialize(n));
		
		}
		
		return buffer.array();

	}
	
}
