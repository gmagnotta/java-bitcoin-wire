package org.gmagnotta.bitcoin.wire.serializer;

import org.gmagnotta.bitcoin.script.BitcoinScript;
import org.gmagnotta.bitcoin.script.BitcoinScriptSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

public class BitcoinScriptParserTest {
	
	private static byte[] script;
	
	static {
		script = Hex.decode("47304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901410411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3ac");
	}
	
	@Test
	public void testFromBytes() throws Exception {
		
		BitcoinScriptSerializer bitcoinScriptSerializer = new BitcoinScriptSerializer();

		BitcoinScript bitcoinscript = bitcoinScriptSerializer.deserialize(script, 0);
		
		Assert.assertArrayEquals(script, bitcoinScriptSerializer.serialize(bitcoinscript));
		
	}
}
