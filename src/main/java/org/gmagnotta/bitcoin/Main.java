package org.gmagnotta.bitcoin;

import java.math.BigInteger;

import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;
import org.gmagnotta.bitcoin.peer.BitcoinPeer;
import org.gmagnotta.bitcoin.peer.BitcoinPeerManager;
import org.gmagnotta.bitcoin.peer.BitcoinPeerManagerImpl;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.log.LogLevel;
import org.gmagnotta.log.impl.system.ConsoleLogEventWriter;

public class Main {

	public static void main(String[] args) throws Exception {
		
		org.gmagnotta.log.LogEventCollector.getInstance().setLogLevelThreshold(LogLevel.INFO);
		
		org.gmagnotta.log.LogEventCollector.getInstance().addLogEventWriter(new ConsoleLogEventWriter());
		
		final BitcoinPeerManager bitcoinPeerManager = new BitcoinPeerManagerImpl(MagicVersion.TESTNET3);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					bitcoinPeerManager.listen(4000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "bitcoinPeerManagerListener").start();
		
//		bitcoinPeerManager.connect("52.167.211.151", 19000);
		
		bitcoinPeerManager.connect("13.125.54.76", 18333);
		
//		bitcoinPeerManager.connect("127.0.0.1", 4000);
//		
//		for (BitcoinPeer p : bitcoinPeerManager.getConnectedPeers()) {
//
//			long nonce = System.currentTimeMillis();
//			
//			BitcoinPingMessage bitcoinPingMessage = new BitcoinPingMessage(new BigInteger("" + nonce));
//			
//			BitcoinPongMessage pong = p.sendPing(bitcoinPingMessage);
//			
//		}
		
		System.in.read();
		
	}
	
}
