package com.deaking.wallet.core.rpcclient;

/**
 * @author shenzucai
 * @time 2018.08.10 09:35
 */
public class BitcoinRPCException extends BitcoinException {
    public BitcoinRPCException() {
    }

    public BitcoinRPCException(String msg) {
        super(msg);
    }

    public BitcoinRPCException(Throwable cause) {
        super(cause);
    }

    public BitcoinRPCException(String message, Throwable cause) {
        super(message, cause);
    }
}
