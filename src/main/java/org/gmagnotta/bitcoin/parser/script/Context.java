package org.gmagnotta.bitcoin.parser.script;

public interface Context {

	public void setNetxtState(ScriptState scriptState);
	
	public void push(byte[] array);
	
	public byte[] pop();
	
}
