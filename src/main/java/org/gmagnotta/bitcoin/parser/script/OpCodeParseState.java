package org.gmagnotta.bitcoin.parser.script;

public class OpCodeParseState implements ScriptParserState {
	
	private Context context;
	
	public OpCodeParseState(Context context) {
		this.context = context;
	}

	@Override
	public void parse(byte buffer) throws Exception {
		
		// Retrieve OpCode from byte
		OpCode opcode = OpCode.fromByte(buffer);
		
		if (opcode.requiresParameters()) {
			
			ScriptParserState state = opcode.getScriptParserState(context);
			context.setNextParserState(state);
			
		} else {
			context.add(opcode.getScriptElement());
		}
	
	}

	@Override
	public boolean isStillExpectingData() {
		//TODO create an internal state for if-else-endif if there is an unclosed, etc
		return false;
	}

}
