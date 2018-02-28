package org.gmagnotta.bitcoin.parser.script;

import java.nio.ByteBuffer;
import java.util.Stack;

import org.gmagnotta.bitcoin.script.PayloadScriptElement;
import org.gmagnotta.bitcoin.script.ScriptContext;

/**
 * This state is used to implement the "next opcode bytes is data to be pushed onto the stack"
 */
public class ReadDataState implements ScriptParserState {
	
	private int read = 0;
	private long amount;
	private ByteBuffer byteBuffer;
	private Context context;
	private OpCode opCode;
	
	public ReadDataState(Context context, OpCode opcode, long amount) {
		this.context = context;
		this.opCode = opcode;
		this.amount = amount;
		this.byteBuffer = ByteBuffer.allocate((int)amount);
	}
	
	@Override
	public void parse(byte value) {
		
		if (read == amount) {
			
			context.add(new PayloadScriptElement(opCode, byteBuffer.array()) {
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
			});
			context.setNextParserState(new ByteParseState(context));
			
		} else {
			
			byteBuffer.put((byte)value);
			read++;
			
			if (read == amount) {
				
				context.add(new PayloadScriptElement(opCode, byteBuffer.array()) {
					@Override
					public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
						stack.push(getPayload());
					}
				});
				context.setNextParserState(new ByteParseState(context));
				
			}
			
		}
		
	}

}
