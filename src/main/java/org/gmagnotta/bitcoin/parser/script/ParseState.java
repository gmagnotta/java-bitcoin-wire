package org.gmagnotta.bitcoin.parser.script;

public class ParseState implements ScriptState {
	
	private Context context;
	
	public ParseState(Context context) {
		this.context = context;
	}

	@Override
	public void process(byte buffer) {
		try {
		
			Opcode opcode = Opcode.fromByte(buffer);
		
		} catch (Exception e) {
			
		}
		
	}

}
