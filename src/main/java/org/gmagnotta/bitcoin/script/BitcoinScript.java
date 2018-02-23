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
	
	public int indexOf(ScriptItem scriptItem) {
		
		for (int index = 0; index < items.size(); index++) {
			
			ScriptItem item = items.get(index);
			
			if (item.equals(scriptItem)) {
				return index;
			}
			
		}
		
		return -1;
	}
	
	public BitcoinScript subScript(int index) {
		
		List<ScriptItem> newItems = items.subList(index, items.size());
		
		return new BitcoinScript(newItems);
		
	}

	@Override
	public String toString() {
		
		String str = "Script: ";
		for (ScriptItem i : items) {
			str += " " + i;
		}
		
		return str;
		
	}
	
}
