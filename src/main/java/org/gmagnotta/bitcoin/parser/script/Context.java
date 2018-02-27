package org.gmagnotta.bitcoin.parser.script;

import org.gmagnotta.bitcoin.script.ScriptElement;

public interface Context {

	public void setNextParserState(ScriptParserState scriptParserState);
	
	public void add(ScriptElement element);
	
}
