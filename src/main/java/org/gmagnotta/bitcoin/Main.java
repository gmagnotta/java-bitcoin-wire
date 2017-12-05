package org.gmagnotta.bitcoin;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.BitcoinGetHeadersMessage;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.BitcoinPongMessage;
import org.gmagnotta.bitcoin.message.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.raw.BitcoinCommand;
import org.gmagnotta.bitcoin.raw.NetworkAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());
	
	public static void main(String[] args) throws Exception {
		
		BitcoinClient bitcoinClient = new BitcoinClient("52.225.217.168", 19000);
		
		bitcoinClient.connect();

		NetworkAddress receiving = new NetworkAddress(0, new BigInteger("0"), InetAddress.getLocalHost(), 0);

		BitcoinVersionMessage versionMessage = new BitcoinVersionMessage(70000L, new BigInteger("0"), new BigInteger("" + System.currentTimeMillis() / 1000), receiving, receiving, new BigInteger("123"), "PeppeLibrary", 0, false);
		
		bitcoinClient.writeMessage(versionMessage);
		
		BitcoinMessage message = bitcoinClient.getMessage();
		
		System.out.println("Read: " + message);
		
		message = bitcoinClient.getMessage();
		
		System.out.println("Read: " + message);
		
		message = bitcoinClient.getMessage();
		
		System.out.println("Read: " + message);
		
		if (message.getCommand().equals(BitcoinCommand.PING)) {
			
			BigInteger nonce = ((BitcoinPingMessage) message).getNonce();
			
			BitcoinPongMessage pong = new BitcoinPongMessage(nonce);
			
			bitcoinClient.writeMessage(pong);
		}
		
		BitcoinPingMessage ping = new BitcoinPingMessage(new BigInteger("1234"));
		
		bitcoinClient.writeMessage(ping);
		
		message = bitcoinClient.getMessage();
		
		List<Sha256Hash> h = new ArrayList<Sha256Hash>();
		
		Sha256Hash last = Sha256Hash.of(Hex.decode("0f9188f13cb7b2c71f2a335e3a4fc328bf5beb436012afca590b1a11466e2206"));
		
		h.add(last);
		BitcoinGetHeadersMessage get = new BitcoinGetHeadersMessage(70000, h);
		
//		BitcoinGetBlocksMessage get = new BitcoinGetBlocksMessage(70000, h);
		
		bitcoinClient.writeMessage(get);
		
		System.out.println("Read: " + message);
		
		message = bitcoinClient.getMessage();
		
		bitcoinClient.disconnect();
		
	}
	
}
