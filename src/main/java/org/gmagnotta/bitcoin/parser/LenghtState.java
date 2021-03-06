package org.gmagnotta.bitcoin.parser;

public class LenghtState implements MessageState {
	
	private Context context;
	
	private byte[] lenght = new byte[4];
	private int index = 0;
	
	public LenghtState(Context context) {
		this.context = context;
	}

	@Override
	public void process(byte buffer) {
		
		lenght[index] = buffer;

		if (index == 3) {
			
			context.setLength(lenght);
			
			context.setNextState(new ChecksumState(context));
			
		}
		
		index++;
		
	}
	
}
