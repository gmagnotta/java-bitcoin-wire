package org.gmagnotta.bitcoin.peer;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeoutException;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;

public class ResponseManager {
	
	private Object syncObj = new Object();
	private Queue<ResponseWaiter> queue = new ArrayBlockingQueue<ResponseWaiter>(100);
	
	public BitcoinMessage waitResponse(BitcoinCommand command, long timeout) throws InterruptedException, TimeoutException {
		
		ResponseWaiter waiter = new ResponseWaiter(command);
		
		synchronized(syncObj) {
		
			queue.add(waiter);
			
		}
		
		// this block until a response is returned or timeout
		waiter.waitResponse(timeout);
		
		return waiter.getResponse();
		
	}
	
	public boolean responseArrived(BitcoinMessage response) {
		
		synchronized(syncObj) {
			
			for (ResponseWaiter waiter : queue) {
				
				if (response.getCommand().equals(waiter.waitingFor())) {
					
					waiter.setResponse(response);
					queue.remove(waiter);
					return true;
					
				}
				
			}
			
			return false;
			
		}
		
	}
	
}
