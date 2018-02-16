package org.gmagnotta.bitcoin.script;

import java.util.Stack;

public interface ScriptItem {

	public void doOperation(Stack<byte[]> stack) throws Exception;

}
