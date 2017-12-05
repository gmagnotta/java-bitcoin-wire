import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.gmagnotta.bitcoin.raw.NetworkAddress;
import org.gmagnotta.bitcoin.raw.serializer.NetworkAddressSerializer;
import org.junit.Assert;
import org.junit.Test;

import com.subgraph.orchid.encoders.Hex;

public class NetworkAddressSerializerTest {

	private static byte[] bytearray1 = new byte[] { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, (byte) 0xff, (byte) 0xff, (byte) 0x97, 0x0d, 0x1b, (byte) 0xda, (byte) 0xe1, (byte) 0x9a };
	
	private static byte[] bytearray2 = new byte[] { 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, (byte) 0xff, (byte) 0xff, 0x00, 0x00, 0x00, 0x00, 0x4a, 0x38 };
	
	private static byte[] bytearray3;
	
	static {
		
		bytearray3 = Hex.decode("a31e255a000000000000000000000000000000000000ffff970d1bda4a38");
		
	}

	@Test
	public void test1() throws UnknownHostException {
		
		NetworkAddress address = new NetworkAddressSerializer(false).deserialize(bytearray1);
		
		Assert.assertEquals(InetAddress.getByAddress(new byte[] { (byte) 0x97, (byte) 0xd, (byte) 0x1b, (byte) 0xda }), address.getInetAddress());
		
		Assert.assertEquals(57754, address.getPort());
		
		Assert.assertEquals(new BigInteger("1"), address.getServices());
		
		Assert.assertEquals(0, address.getTime());
		
		Assert.assertArrayEquals(bytearray1, new NetworkAddressSerializer(false).serialize(address));
	}
	
	@Test
	public void test2() throws UnknownHostException {
		
		NetworkAddress address = new NetworkAddressSerializer(false).deserialize(bytearray2);
		
		Assert.assertEquals(InetAddress.getByAddress(new byte[] { 0, 0, 0, 0 }), address.getInetAddress());
		
		Assert.assertEquals(19000, address.getPort());
		
		Assert.assertEquals(new BigInteger("5"), address.getServices());
		
		Assert.assertEquals(0, address.getTime());
		
		Assert.assertArrayEquals(bytearray2, new NetworkAddressSerializer(false).serialize(address));
	}
	
	@Test
	public void test3() throws UnknownHostException {
		
		NetworkAddress address = new NetworkAddressSerializer(true).deserialize(bytearray3);
		
		Assert.assertEquals(InetAddress.getByAddress(new byte[] { (byte) 0x97, 0xd, 0x1b, (byte) 0xda }), address.getInetAddress());
		
		Assert.assertEquals(19000, address.getPort());
		
		Assert.assertEquals(new BigInteger("0"), address.getServices());
		
		Assert.assertEquals(1512382115, address.getTime());
		
		Assert.assertArrayEquals(bytearray3, new NetworkAddressSerializer(true).serialize(address));
	}

}
