package org.gmagnotta.bitcoin.server;

import org.gmagnotta.bitcoin.server.ServerState;

public interface ServerContext {
	
	public void setNextState(ServerState serverState);

}
