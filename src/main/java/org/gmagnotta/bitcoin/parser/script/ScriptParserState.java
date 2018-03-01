package org.gmagnotta.bitcoin.parser.script;

public interface ScriptParserState {

	public void parse(byte value) throws Exception;
	
	public boolean isStillExpectingData();
	
}
