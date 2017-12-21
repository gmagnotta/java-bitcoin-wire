package org.gmagnotta.bitcoin;

import org.gmagnotta.bitcoin.user.BitcoinClient;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.log.LogLevel;
import org.gmagnotta.log.impl.system.ConsoleLogEventWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());
	
	public static void main(String[] args) throws Exception {
		
		org.gmagnotta.log.LogEventCollector.getInstance().setLogLevelThreshold(LogLevel.INFO);
		
		org.gmagnotta.log.LogEventCollector.getInstance().addLogEventWriter(new ConsoleLogEventWriter());
		
		BitcoinClient bitcoinClient = new BitcoinClient(MagicVersion.TESTNET3, "13.125.54.76", 18333);
		
		bitcoinClient.connect();

		System.in.read();
		
		bitcoinClient.disconnect();
		
	}
	
}
