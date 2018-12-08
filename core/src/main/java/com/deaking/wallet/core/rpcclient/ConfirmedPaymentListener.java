package com.deaking.wallet.core.rpcclient;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author shenzucai
 * @time 2018.08.10 09:37
 */
public abstract class ConfirmedPaymentListener extends SimpleBitcoinPaymentListener {
    public int minConf;
    protected Set<String> processed;

    public ConfirmedPaymentListener(int minConf) {
        this.processed = Collections.synchronizedSet(new HashSet());
        this.minConf = minConf;
    }

    public ConfirmedPaymentListener() {
        this(6);
    }

    protected boolean markProcess(String txId) {
        return this.processed.add(txId);
    }

    public void transaction(Bitcoin.Transaction transaction) {
        if(transaction.confirmations() >= this.minConf) {
            if(this.markProcess(transaction.txId() + "-" + transaction.address())) {
                this.confirmed(transaction);
            }
        }
    }

    public abstract void confirmed(Bitcoin.Transaction var1);
}