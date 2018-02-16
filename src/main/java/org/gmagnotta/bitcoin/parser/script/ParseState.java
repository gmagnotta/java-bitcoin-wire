package org.gmagnotta.bitcoin.parser.script;

public class ParseState implements ScriptParserState {
	
	private Context context;
	
	public ParseState(Context context) {
		this.context = context;
	}

	@Override
	public void parse(byte buffer) {
		
		try {
		
			OpCode opcode = OpCode.fromByte(buffer);
			
			if (opcode.hasParameters()) {
				ScriptParserState state = opcode.getScriptState(context);
				context.setNextParserState(state);
			} else {
				context.add(opcode.getOperation());
			}
			
		
		} catch (Exception e) {
			
		}
		
	}

}
