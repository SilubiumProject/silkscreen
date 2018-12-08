package com.deaking.wallet.core.rpcclient;

/**
 * @author shenzucai
 * @time 2018.08.10 09:34
 */
public interface BitcoinPaymentListener {
    void block(String var1);

    void transaction(Bitcoin.Transaction var1);
}
