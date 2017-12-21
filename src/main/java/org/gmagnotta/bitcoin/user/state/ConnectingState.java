package org.gmagnotta.bitcoin.user.state;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinVerackMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.user.ClientContext;
import org.gmagnotta.bitcoin.user.ClientState;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In this state we sent a Version and we are waiting a VerAck from the peer.
 * The peer can send unsolicited its Version message and we need to reply with a VerAck 
 */
public class ConnectingState implements ClientState {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectingState.class);
	
	private ClientContext clientContext;
	
	public ConnectingState(ClientContext clientContext) {
		this.clientContext = clientContext;
	}

	@Override
	public void onMessageReceived(BitcoinMessage bitcoinMessage) {
		
		if (bitcoinMessage.getCommand().equals(BitcoinCommand.VERACK)) {
			
			LOGGER.info("RECEIVED VERACK changing to IdleState");
			
			ClientState clientState = new IdleState(clientContext);
			
			clientContext.setNextState(clientState);
			
		} else if (bitcoinMessage.getCommand().equals(BitcoinCommand.VERSION)) {
			
			LOGGER.info("RECEIVED VERSION");
			
			BitcoinVersionMessage bitcoinVersionMessage = (BitcoinVersionMessage) bitcoinMessage;
			
			LOGGER.info("Connected with {} {}", bitcoinVersionMessage.getAddressEmitting(), bitcoinVersionMessage.getUserAgent()); 
			
			BitcoinVerackMessage verack = new BitcoinVerackMessage();
			
			try {
				
				LOGGER.info("SENDING VERACK");
	
				clientContext.writeMessage(verack);
			
			} catch (Exception ex) {
				
				LOGGER.error("Exception", ex);
				
			}
			
		} else {
			
			LOGGER.info("IGNORING " + bitcoinMessage.getCommand());
			
		}
		
	}

}
