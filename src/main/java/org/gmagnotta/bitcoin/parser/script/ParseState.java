package org.gmagnotta.bitcoin.parser.script;

public class ParseState implements ScriptState {
	
	private Context context;
	
	public ParseState(Context context) {
		this.context = context;
	}

	@Override
	public void process(byte buffer) {
		
		if (buffer == 0) {
			
			// push empty byte
			context.push(new byte[]{});
			
		} else if (buffer >= (byte)0x01 && buffer <= (byte)0x4b) {
			
			context.setNetxtState(new PushDataState(context, buffer));
			
		} else if (buffer == (byte)0xac) {
			
			new OpCheckSig(context).execute();
			
		} else if (buffer == (byte) 0x61) {
			
			// DO NOTHING
			
		}
		
	}

}
