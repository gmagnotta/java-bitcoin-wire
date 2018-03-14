package org.gmagnotta.bitcoin.peer;

import java.util.concurrent.TimeoutException;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;

public class OutputRequest {
	
	private BitcoinMessage outputMessage;
	private BitcoinCommand expectedResponseType;
	private boolean expired;
	private long timeout;
	private BitcoinMessage response;
	private final Object syncObject = new Object();
	
	public OutputRequest(BitcoinMessage bitcoinMessage) {
		this.outputMessage = bitcoinMessage;
		this.expired = false;
	}
	
	public OutputRequest(BitcoinMessage bitcoinMessage, BitcoinCommand expectedResponseType, long timeout) {
		this.outputMessage = bitcoinMessage;
		this.expectedResponseType = expectedResponseType;
		this.expired = false;
		this.timeout = timeout;
	}
	
	public BitcoinMessage getResponse() throws InterruptedException, TimeoutException, Exception {
		
		synchronized (syncObject) {

			syncObject.wait(timeout);
				
			if (response == null) {
				expired = true;
				throw new TimeoutException();
			} else if (response.getCommand().equals(BitcoinCommand.REJECT)) {
				throw new Exception("Message rejected!");
			}
			
			return response;
			
		}
		
	}
	
	public BitcoinMessage getBitcoinMessage() {
		return outputMessage;
	}
	
	public BitcoinCommand getExpectedResponseType() {
		return expectedResponseType;
	}
	
	public void receiveResponse(BitcoinMessage bitcoinMessage) {
		
		synchronized (syncObject) {
			
			response = bitcoinMessage;
			
			syncObject.notify();
			
		}
		
	}
	
	public boolean isExpired() {
		
		synchronized (syncObject) {
			return expired;
		}
	}

}
