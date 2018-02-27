package org.gmagnotta.bitcoin.parser.script;

import java.nio.ByteBuffer;

import org.gmagnotta.bitcoin.script.ScriptElement;
import org.gmagnotta.bitcoin.wire.Utils;

/**
 * This state is used to implement the "next n bytes contains the number of bytes to be pushed onto the stack."
 */
public class IntermediateReadDataState implements ScriptParserState {
	
	private int read = 0;
	private int amount;
	private ByteBuffer byteBuffer;
	private Context context;
	private OpCode opCode;
	
	public IntermediateReadDataState(Context context, OpCode opcode, int amount) {
		this.context = context;
		this.opCode = opcode;
		this.amount = amount;
		this.byteBuffer = ByteBuffer.allocate(amount);
	}
	
	@Override
	public void parse(byte value) {
		
		if (read == amount) {
			
			// interpret amount
			
			context.setNextParserState(new ReadDataState(context, opCode, 1));
			
		} else {
			
			byteBuffer.put((byte)value);
			read++;
			
			if (read == amount) {
				
				byte[] array = byteBuffer.array();
				
				long size = 0;
				
				if (array.length == 1) {
					
					size = array[0];
					
				} else if (array.length == 2) {
					
					size = Utils.readUint16LE(array, 0);
					
				} else if (array.length == 4) {
					
					size = Utils.readUint32LE(array, 0);
					
				}
				
				context.setNextParserState(new ReadDataState(context, opCode, size));
				
			}
			
		}
		
	}

}
