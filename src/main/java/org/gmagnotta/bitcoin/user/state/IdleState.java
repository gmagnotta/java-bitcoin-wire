package org.gmagnotta.bitcoin.user.state;

import java.math.BigInteger;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinVerackMessage;
import org.gmagnotta.bitcoin.user.ClientContext;
import org.gmagnotta.bitcoin.user.ClientState;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * State designed to do nothing. Only to reply to ping and receive unsolicited messages
 */
public class IdleState implements ClientState {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IdleState.class);
	
	private ClientContext clientContext;
	
	public IdleState(ClientContext clientContext) {
		this.clientContext = clientContext;
	}

	@Override
	public void onMessageReceived(BitcoinMessage bitcoinMessage) {
		
		if (bitcoinMessage.getCommand().equals(BitcoinCommand.VERSION)) {
			
			LOGGER.info("RECEIVED VERSION");
			
			BitcoinVerackMessage verack = new BitcoinVerackMessage();
			
			try {
				
				LOGGER.info("SENDING VERACK");
	
				clientContext.writeMessage(verack);
			
			} catch (Exception ex) {
				
				LOGGER.error("Exception", ex);
				
			}
			
		} else if (bitcoinMessage.getCommand().equals(BitcoinCommand.PING)) {
			
			try {
				
				LOGGER.info("RECEIVED PING");
				
				BitcoinPingMessage ping = (BitcoinPingMessage) bitcoinMessage;
				
				BigInteger nonce = ping.getNonce();
				
				BitcoinPongMessage pong = new BitcoinPongMessage(nonce);
				
				LOGGER.info("SENDING PONG");
				
				clientContext.writeMessage(pong);
			
			} catch (Exception ex) {
						
				LOGGER.error("Exception", ex);
				
			}
			
		} else {
			
			LOGGER.info("IGNORING " + bitcoinMessage.getCommand());
			
		}
		
	}

}
