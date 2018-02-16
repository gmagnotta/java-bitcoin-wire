package org.gmagnotta.bitcoin.script;

import java.util.List;

import org.gmagnotta.bitcoin.message.impl.TransactionInput;

public class BitcoinScript {
	
	private List<ScriptItem> items;
	private TransactionInput transactionInput;
	
	public BitcoinScript(List<ScriptItem> items, TransactionInput transactionInput) {
		this.items = items;
		this.transactionInput = transactionInput;
	}
	
	public List<ScriptItem> getItems() {
		return items;
	}

	public TransactionInput getTransactionInput() {
		return transactionInput;
	}
	
	@Override
	public String toString() {
		
		String str = "Script: ";
		for (ScriptItem i : items) {
			str += i;
		}
		
		return str;
		
	}
	
}
