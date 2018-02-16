package org.gmagnotta.bitcoin.parser.script;

import java.nio.ByteBuffer;

import org.gmagnotta.bitcoin.script.impl.Element;

public class ReadDataState implements ScriptParserState {
	
	private int read = 0;
	private int amount;
	private ByteBuffer byteBuffer;
	private Context context;
	private OpCode opCode;
	
	public ReadDataState(Context context, OpCode opcode, int amount) {
		this.context = context;
		this.opCode = opcode;
		this.amount = amount;
		this.byteBuffer = ByteBuffer.allocate(amount);
	}
	
	@Override
	public void parse(byte value) {
		
		if (read == amount) {
			
			context.add(new Element(opCode, byteBuffer.array()));
			context.setNextParserState(new ParseState(context));
			
		} else {
			
			byteBuffer.put((byte)value);
			read++;
			
			if (read == amount) {
				
				context.add(new Element(opCode, byteBuffer.array()));
				context.setNextParserState(new ParseState(context));
				
			}
			
		}
		
	}

}
