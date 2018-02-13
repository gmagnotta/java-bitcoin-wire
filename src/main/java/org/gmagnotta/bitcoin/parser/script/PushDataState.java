package org.gmagnotta.bitcoin.parser.script;

import java.nio.ByteBuffer;

public class PushDataState implements ScriptState {
	
	private int read = 0;
	private int amount;
	private ByteBuffer byteBuffer;
	private Context context;
	
	public PushDataState(Context context, int amount) {
		this.amount = amount;
		this.byteBuffer = ByteBuffer.allocate(amount);
		this.context = context;
	}
	
	@Override
	public void process(byte buffer) {
		
		if (read == amount) {
			
			context.push(byteBuffer.array());
			context.setNetxtState(new ParseState(context));
			
		} else {
			
			byteBuffer.put((byte)buffer);
			read++;
			
			if (read == amount) {
				
				context.push(byteBuffer.array());
				context.setNetxtState(new ParseState(context));
				
			}
			
		}
		
	}

}
