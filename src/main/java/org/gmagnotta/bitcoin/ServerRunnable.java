package org.gmagnotta.bitcoin;

import java.net.ServerSocket;
import java.net.Socket;

import org.gmagnotta.bitcoin.server.BitcoinServer;

public class ServerRunnable implements Runnable {

	@Override
	public void run() {

		try {

			// Start server
			ServerSocket serverSocket = new ServerSocket(19000);
			
			Socket clientSocket = serverSocket.accept();
			
			BitcoinServer server = new BitcoinServer(clientSocket);
			
			server.start();

		} catch (Exception ex) {
			
			ex.printStackTrace();

		}

	}

}
