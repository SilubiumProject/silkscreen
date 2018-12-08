package com.deaking.wallet.core.rpcclient;

/**
 * @author shenzucai
 * @time 2018.08.10 09:33
 */
public class BitcoinException extends Exception {
    public BitcoinException() {
    }

    public BitcoinException(String msg) {
        super(msg);
    }

    public BitcoinException(Throwable cause) {
        super(cause);
    }

    public BitcoinException(String message, Throwable cause) {
        super(message, cause);
    }
}

