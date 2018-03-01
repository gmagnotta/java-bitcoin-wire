package org.gmagnotta.bitcoin.script;

import java.util.List;

/**
 * This class represents a Bitcoin Script
 * 
 */
public class BitcoinScript {
	
	private List<ScriptElement> elements;
	
	public BitcoinScript(List<ScriptElement> elements) {
		this.elements = elements;
	}
	
	public List<ScriptElement> getElements() {
		return elements;
	}
	
	public int indexOf(ScriptElement scriptElement) {
		
		for (int index = 0; index < elements.size(); index++) {
			
			ScriptElement item = elements.get(index);
			
			if (item.equals(scriptElement)) {
				return index;
			}
			
		}
		
		return -1;
	}
	
	public int lastIndexOf(ScriptElement scriptElement) {
		
		for (int index = (elements.size() - 1); index > -1; index--) {
			
			ScriptElement item = elements.get(index);
			
			if (item.equals(scriptElement)) {
				return index;
			}
			
		}
		
		return -1;
		
	}
	
	public BitcoinScript subScript(int index) {
		
		List<ScriptElement> newElements = elements.subList(index, elements.size());
		
		return new BitcoinScript(newElements);
		
	}

	@Override
	public String toString() {
		
		String str = "Script:";
		for (ScriptElement i : elements) {
			str += " " + i;
		}
		
		return str;
		
	}
	
}
