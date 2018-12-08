package com.deaking.wallet.core.rpcclient;

/**
 * @author shenzucai
 * @time 2018.08.10 09:38
 */
public class SimpleBitcoinPaymentListener implements BitcoinPaymentListener {
    public SimpleBitcoinPaymentListener() {
    }

    @Override
    public void block(String blockHash) {
    }
    @Override
    public void transaction(Bitcoin.Transaction transaction) {
    }
}