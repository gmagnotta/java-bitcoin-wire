package org.gmagnotta.bitcoin.parser.script;

public class OpCheckSig implements Operation {
	
	private Context context;
	
	public OpCheckSig(Context context) {
		this.context = context;
	}
	
	public void execute() {
		
	}

}
