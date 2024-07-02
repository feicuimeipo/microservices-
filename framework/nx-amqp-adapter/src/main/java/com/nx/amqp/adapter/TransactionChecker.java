package com.nx.amqp.adapter;


public interface TransactionChecker {

	public static final String TRANSACTION_PARAM_NAME = "txId";
	boolean check(String transactionId);
}
