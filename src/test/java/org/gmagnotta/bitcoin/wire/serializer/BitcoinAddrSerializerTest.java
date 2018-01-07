package org.gmagnotta.bitcoin.wire.serializer;

import java.math.BigInteger;
import java.net.InetAddress;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinAddrMessage;
import org.gmagnotta.bitcoin.message.impl.NetworkAddress;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinAddrMessageSerializer;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinVersionMessageSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

public class BitcoinAddrSerializerTest {
	
private static byte[] message;
	
	static {
		message = Hex.decode("02272d525a0d0000000000000000000000000000000000ffff904c4739479d4c2d525a0d0000000000000000000000000000000000ffff34a4bf2e479d");
	}

	@Test
	public void testDeserialize() throws Exception {
		
		BitcoinAddrMessage addrMessage = (BitcoinAddrMessage) new BitcoinAddrMessageSerializer().deserialize(message);
		
		Assert.assertEquals(2, addrMessage.getNetworkAddress().size());
		
		NetworkAddress receiver = new NetworkAddress(1515334951, new BigInteger("13"), InetAddress.getByAddress(new byte[] { (byte) 144, (byte) 76, (byte) 71, (byte) 57 }), 18333);
		
		Assert.assertEquals(receiver, addrMessage.getNetworkAddress().get(0));
		
		NetworkAddress receiver2 = new NetworkAddress(1515334988, new BigInteger("13"), InetAddress.getByAddress(new byte[] { (byte) 52, (byte) 164, (byte) 191, (byte) 46 }), 18333);
		
		Assert.assertEquals(receiver2, addrMessage.getNetworkAddress().get(1));
		
	}

//	@Test
	public void testSerialize() throws Exception {
		
		BitcoinMessage versionMessage = new BitcoinVersionMessageSerializer().deserialize(message);
		
		byte[] serialized = new BitcoinVersionMessageSerializer().serialize(versionMessage);
		
		Assert.assertArrayEquals(message, serialized);
	}

}
