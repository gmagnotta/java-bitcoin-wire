package org.gmagnotta.bitcoin.blockchain;

public interface TransactionManager {
	
	public void startTransaction() throws Exception;

	public void commitTransaction() throws Exception;
	
	public void rollbackTransaction() throws  Exception;

	public boolean insideTransaction();

}
