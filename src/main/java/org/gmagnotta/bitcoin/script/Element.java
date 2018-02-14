package org.gmagnotta.bitcoin.script;

// Just encapsulates data
public class Element extends ScriptItem {
	
	private byte[] data;
	
	public Element(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	};
}
