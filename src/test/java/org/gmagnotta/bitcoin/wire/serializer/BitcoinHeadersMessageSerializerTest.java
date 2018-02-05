package org.gmagnotta.bitcoin.wire.serializer;
import org.gmagnotta.bitcoin.message.impl.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.wire.serializer.impl.BitcoinHeadersMessageSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

public class BitcoinHeadersMessageSerializerTest {
	
	private static byte[] message;
	
	static {
		
		message = Hex.decode("020100000043497fd7f826957108f4a30fd9cec3aeba79972084e90ead01ea330900000000bac8b0fa927c0ac8234287e33c5f74d38d354820e24756ad709d7038fc5f31f020e7494dffff001d03e4b672000100000006128e87be8b1b4dea47a7247d5528d2702c96826c7a648497e773b800000000e241352e3bec0a95a6217e10c3abb54adfa05abb12c126695595580fb92e222032e7494dffff001d00d2353400");
	}
	
	@Test
	public void testDeserialize() throws Exception {
		
		BitcoinHeadersMessage headersMessage = (BitcoinHeadersMessage) new BitcoinHeadersMessageSerializer().deserialize(message, 0, message.length);
		
		Assert.assertEquals(2, headersMessage.getHeaders().size());
		
	}

//	@Test
	public void testSerialize() throws Exception {
		
	}

}
