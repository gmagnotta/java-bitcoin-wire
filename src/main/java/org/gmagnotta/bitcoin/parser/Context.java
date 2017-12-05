package org.gmagnotta.bitcoin.parser;

public interface Context {
	
	public void setMagic(byte[] array);
	
	public void setCommand(byte[] array);
	
	public void setLength(byte[] array);
	
	public byte[] getLength();
	
	public void setChecksum(byte[] array);
	
	public void setPayload(byte[] array);
	
	public void setNextState(MessageState messageState);
	
	public void setComplete();

}
