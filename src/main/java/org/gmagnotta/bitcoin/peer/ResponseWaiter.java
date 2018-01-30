package org.gmagnotta.bitcoin.peer;

import java.util.concurrent.TimeoutException;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;

public class ResponseWaiter {
	
	private long timestamp;
	private BitcoinCommand bitcoinCommand;
	private final Object waitObj;
	private BitcoinMessage response;
	
	public ResponseWaiter(BitcoinCommand bitcoinCommand) {
		
		this.timestamp = System.currentTimeMillis();
		this.bitcoinCommand = bitcoinCommand;
		this.waitObj = new Object();
		
	}
	
	public void setResponse(BitcoinMessage response) {
		
		synchronized (waitObj) {
			
			this.response = response;
			
			waitObj.notify();
			
		}
	}
	
	public BitcoinMessage getResponse() {
		
		synchronized (waitObj) {
			
			return response;

		}
		
	}
	
	public BitcoinCommand waitingFor() {
		return bitcoinCommand;
	}
	
	public void waitResponse(long timeout) throws InterruptedException, TimeoutException {
		
		synchronized (waitObj) {
			
			waitObj.wait(timeout);
			
			if (response == null) {
				throw new TimeoutException();
			}
			
		}
		
	}
	
}
