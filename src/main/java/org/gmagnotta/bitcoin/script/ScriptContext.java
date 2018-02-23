package org.gmagnotta.bitcoin.script;

import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.message.impl.TransactionOutput;

public interface ScriptContext {
	
	public TransactionInput getTransactionInput();
	
	public long getIndex();

	public TransactionOutput getTransactionOutput();
	
	public Transaction getTransaction();
}
