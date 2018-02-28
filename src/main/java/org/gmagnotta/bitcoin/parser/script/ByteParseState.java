package org.gmagnotta.bitcoin.parser.script;

public class ByteParseState implements ScriptParserState {
	
	private Context context;
	
	public ByteParseState(Context context) {
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

}
