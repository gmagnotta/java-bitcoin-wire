package org.gmagnotta.bitcoin;

public class MagicState implements MessageState {

	private Context context;
	private byte[] magic = { (byte) 0xFA, (byte) 0xBF, (byte) 0xB5, (byte) 0xDA };
	private int expected = 0;
	
	public MagicState(Context context) {
		this.context = context;
	}
	
	@Override
	public void read(byte buffer) {
		
		// convert byte to unsigned int
		if (buffer == magic[expected]) {
			
			if (expected == 3) {
				
				context.setMagic(magic);
				
				context.setNextState(new CommandState(context));
				
			} else { 

				expected++;
			
			}
			
		} else {
			
			// read garbage
			expected = 0;
			
			if (buffer == magic[expected]) {
				
				expected++;
				
			}
			
		}
		
	}

}
