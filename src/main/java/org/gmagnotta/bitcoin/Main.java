package org.gmagnotta.bitcoin;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetHeadersMessage;
import org.gmagnotta.bitcoin.user.BitcoinClient;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.log.LogLevel;
import org.gmagnotta.log.impl.system.ConsoleLogEventWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());
	
	public static void main(String[] args) throws Exception {
		
		org.gmagnotta.log.LogEventCollector.getInstance().setLogLevelThreshold(LogLevel.INFO);
		
		org.gmagnotta.log.LogEventCollector.getInstance().addLogEventWriter(new ConsoleLogEventWriter());
		
//		new Thread(new ServerRunnable()).start();
		
		BitcoinClient bitcoinClient = new BitcoinClient(MagicVersion.TESTNET3, "127.0.0.1", 18333);
		
		bitcoinClient.connect();

//		BitcoinPingMessage ping = new BitcoinPingMessage(new BigInteger("1234"));
//		
//		bitcoinClient.writeMessage(ping);
//		
//		BitcoinMessage message = bitcoinClient.getMessage();
//		
		List<Sha256Hash> h = new ArrayList<Sha256Hash>();
		
		Sha256Hash last = Sha256Hash.of(Hex.decode("000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943"));
		
		h.add(last);
		BitcoinGetHeadersMessage get = new BitcoinGetHeadersMessage(70012L, h);
		
//		BitcoinGetBlocksMessage get = new BitcoinGetBlocksMessage(70000, h);
		
		bitcoinClient.writeMessage(get);
//		
//		System.out.println("Read: " + message);
//		
		BitcoinMessage message = bitcoinClient.getMessage();
		
		bitcoinClient.disconnect();
		
	}
	
}
