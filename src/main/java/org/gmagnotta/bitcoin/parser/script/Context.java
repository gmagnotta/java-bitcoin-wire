package org.gmagnotta.bitcoin.parser.script;

import org.gmagnotta.bitcoin.script.ScriptItem;

public interface Context {

	public void setNextParserState(ScriptParserState scriptParserState);
	
	public void add(ScriptItem item);
	
}
