package org.gmagnotta.bitcoin.user;

import org.gmagnotta.bitcoin.message.BitcoinMessage;

public interface ClientState {
	
	public void onMessageReceived(BitcoinMessage bitcoinMessage);

}
