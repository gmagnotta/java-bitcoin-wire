package org.gmagnotta.bitcoin.script;

import java.util.List;

public class BitcoinScript {
	
	private List<ScriptItem> items;
	
	public BitcoinScript(List<ScriptItem> items) {
		this.items = items;
	}
	
	public List<ScriptItem> getItems() {
		return items;
	}

}
