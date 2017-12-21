package org.gmagnotta.bitcoin.parser;

import org.gmagnotta.bitcoin.wire.MagicVersion;

public class MagicState implements MessageState {

	private Context context;
	private byte[] magic;
	private int expected = 0;
	
	public MagicState(MagicVersion magicVersion, Context context) {
		this.magic = magicVersion.getBytes();
		this.context = context;
	}
	
	@Override
	public void process(byte buffer) {
		
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
