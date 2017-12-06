package org.gmagnotta.bitcoin;

import java.net.ServerSocket;
import java.net.Socket;

import org.gmagnotta.bitcoin.server.BitcoinServer;
import org.gmagnotta.bitcoin.wire.MagicVersion;

public class ServerRunnable implements Runnable {

	@Override
	public void run() {

		try {

			// Start server
			ServerSocket serverSocket = new ServerSocket(18333);
			
			Socket clientSocket = serverSocket.accept();
			
			BitcoinServer server = new BitcoinServer(MagicVersion.TESTNET3, clientSocket);
			
			server.start();

		} catch (Exception ex) {
			
			ex.printStackTrace();

		}

	}

}
