package org.gmagnotta.bitcoin.wire.serializer;

import java.util.ArrayList;
import java.util.List;

import org.gmagnotta.bitcoin.parser.script.OpCode;
import org.gmagnotta.bitcoin.script.BitcoinScript;
import org.gmagnotta.bitcoin.script.BitcoinScriptSerializer;
import org.gmagnotta.bitcoin.script.PayloadScriptElement;
import org.gmagnotta.bitcoin.script.ScriptElement;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

public class BitcoinScriptParserTest {
	
	private static byte[] scriptErr0;
	private static byte[] scriptErr1;
	private static byte[] script;
	private static byte[] ifScript;
	private static byte[] p2pkhScript;
	
	static {
		scriptErr0 = Hex.decode("47304402");
		scriptErr1 = Hex.decode("47304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1daabb");
		script = Hex.decode("47304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901410411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3ac");
		ifScript = Hex.decode("5163606168");
		p2pkhScript = Hex.decode("76a91447522b3bfaefedf88eb5e2f96f5d18668ee754b988ac");
	}
	
	@Test
	public void testFromBytes() throws Exception {
		
		BitcoinScriptSerializer bitcoinScriptSerializer = new BitcoinScriptSerializer();

		try {
			BitcoinScript bitcoinscript = bitcoinScriptSerializer.deserialize(scriptErr0, 0);
			Assert.fail();
		} catch (Exception ex) {
			// Expected
		}
		
		try {
			BitcoinScript bitcoinscript = bitcoinScriptSerializer.deserialize(scriptErr1, 0);
		} catch (Exception ex) {
			// Expected
		}
		
		BitcoinScript bitcoinscript = bitcoinScriptSerializer.deserialize(script, 0);
		
		Assert.assertArrayEquals(script, bitcoinScriptSerializer.serialize(bitcoinscript));
		
		try {
			List<ScriptElement> elements = new ArrayList<ScriptElement>();
			
			elements.add(new PayloadScriptElement(OpCode.NA_71, Hex.decode("304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d09")));
			
			Assert.assertArrayEquals(script, bitcoinScriptSerializer.serialize(new BitcoinScript(elements)));
			Assert.fail();
		} catch (Exception ex) {
			//Expected
		}
		
		List<ScriptElement> elements = new ArrayList<ScriptElement>();
		
		elements.add(new PayloadScriptElement(OpCode.NA_71, Hex.decode("304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901")));
		elements.add(new PayloadScriptElement(OpCode.NA_65, Hex.decode("0411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3")));
		elements.add(new ScriptElement(OpCode.OP_CHECKSIG));
		
		Assert.assertArrayEquals(script, bitcoinScriptSerializer.serialize(new BitcoinScript(elements)));
		
		//
		bitcoinscript = bitcoinScriptSerializer.deserialize(ifScript, 0);
		Assert.assertArrayEquals(ifScript, bitcoinScriptSerializer.serialize(bitcoinscript));
		
		
		bitcoinscript = bitcoinScriptSerializer.deserialize(p2pkhScript, 0);
		Assert.assertArrayEquals(p2pkhScript, bitcoinScriptSerializer.serialize(bitcoinscript));
		
	}
}
