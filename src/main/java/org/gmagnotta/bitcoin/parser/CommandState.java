package org.gmagnotta.bitcoin.parser;

public class CommandState implements MessageState {

	private Context context;
	
	// Read 12 bytes
	private byte[] command = new byte[12];
	private int index = 0;
	
	public CommandState(Context context) {
		this.context = context;
	}
	
	@Override
	public void process(byte buffer) {
		
		command[index] = buffer;

		if (index == 11) {
			
			context.setCommand(command);
			
			context.setNextState(new LenghtState(context));
			
		}
		
		index++;
		
	}

}
