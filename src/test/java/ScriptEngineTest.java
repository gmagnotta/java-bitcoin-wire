import java.util.ArrayList;
import java.util.List;

import org.gmagnotta.bitcoin.script.BitcoinScript;
import org.gmagnotta.bitcoin.script.Element;
import org.gmagnotta.bitcoin.script.OpCheckSig;
import org.gmagnotta.bitcoin.script.ScriptEngine;
import org.gmagnotta.bitcoin.script.ScriptItem;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

public class ScriptEngineTest {

	@Test
	public void testEngine() {
		
		List<ScriptItem> items = new ArrayList<ScriptItem>();
		
		// add scriptsig
		items.add(new Element(Hex.decode("304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901")));
		
		// add pubkey
		items.add(new Element(Hex.decode("0411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3")));
		
		items.add(new OpCheckSig());
		
		BitcoinScript bitcoinScript = new BitcoinScript(items);
		
		ScriptEngine scriptEngine = new ScriptEngine();
		
		scriptEngine.isValid(bitcoinScript);
	}
}
