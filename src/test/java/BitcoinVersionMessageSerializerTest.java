import java.math.BigInteger;
import java.net.InetAddress;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.raw.NetworkAddress;
import org.gmagnotta.bitcoin.raw.serializer.BitcoinVersionMessageSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

public class BitcoinVersionMessageSerializerTest {
	
	private static byte[] message;
	
	static {
		message = Hex.decode("7c1101000500000000000000ddf3ed5900000000010000000000000000000000000000000000ffff970d1bdae19a050000000000000000000000000000000000ffff000000004a385502c949e5e88d92102f5361746f7368693a302e31322e312fe915000001");
	}

	@Test
	public void testDeserialize() throws Exception {
		
		BitcoinVersionMessage versionMessage = (BitcoinVersionMessage) new BitcoinVersionMessageSerializer().deserialize(message);
		
		Assert.assertEquals(70012, versionMessage.getVersion());
		Assert.assertEquals(new BigInteger("5"), versionMessage.getServices());
		Assert.assertEquals(new BigInteger("1508766685"), versionMessage.getTimestamp());
		Assert.assertEquals(new BigInteger("10560352772736746069"), versionMessage.getNonce());
		Assert.assertEquals("/Satoshi:0.12.1/", versionMessage.getUserAgent());
		Assert.assertEquals(5609, versionMessage.getStartHeight());
		Assert.assertEquals(true, versionMessage.getRelay());
		
		NetworkAddress receiver = new NetworkAddress(0, new BigInteger("1"), InetAddress.getByAddress(new byte[] { (byte) 0x97, (byte) 0xd, (byte) 0x1b, (byte) 0xda }), 57754);
		
		Assert.assertEquals(receiver, versionMessage.getAddressReceiving());
		
		NetworkAddress emitter = new NetworkAddress(0, new BigInteger("5"), InetAddress.getByAddress(new byte[] { (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0 }), 19000);
		
		Assert.assertEquals(emitter, versionMessage.getAddressEmitting());
	}

	@Test
	public void testSerialize() throws Exception {
		
		BitcoinMessage versionMessage = new BitcoinVersionMessageSerializer().deserialize(message);
		
		byte[] serialized = new BitcoinVersionMessageSerializer().serialize(versionMessage);
		
		Assert.assertArrayEquals(message, serialized);
	}

}
