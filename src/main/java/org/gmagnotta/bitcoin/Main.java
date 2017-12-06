package org.gmagnotta.bitcoin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.BitcoinGetHeadersMessage;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.BitcoinPingMessage;
import org.gmagnotta.bitcoin.user.BitcoinClient;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());
	
	public static void main(String[] args) throws Exception {
		
		new Thread(new ServerRunnable()).start();
		
		BitcoinClient bitcoinClient = new BitcoinClient(MagicVersion.TESTNET3, "13.229.23.149", 18333);
		
		bitcoinClient.connect();

		BitcoinPingMessage ping = new BitcoinPingMessage(new BigInteger("1234"));
		
		bitcoinClient.writeMessage(ping);
		
		BitcoinMessage message = bitcoinClient.getMessage();
		
		List<Sha256Hash> h = new ArrayList<Sha256Hash>();
		
		Sha256Hash last = Sha256Hash.of(Hex.decode("0f9188f13cb7b2c71f2a335e3a4fc328bf5beb436012afca590b1a11466e2206"));
		
		h.add(last);
		BitcoinGetHeadersMessage get = new BitcoinGetHeadersMessage(70012L, h);
		
//		BitcoinGetBlocksMessage get = new BitcoinGetBlocksMessage(70000, h);
		
		bitcoinClient.writeMessage(get);
		
		System.out.println("Read: " + message);
		
		message = bitcoinClient.getMessage();
		
		bitcoinClient.disconnect();
		
	}
	
}
