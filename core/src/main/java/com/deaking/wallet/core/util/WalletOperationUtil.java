package com.deaking.wallet.core.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


/**
 * @author shenzucai
 * @time 2018.04.19 22:43
 */
public class WalletOperationUtil {

    private static Logger logger = LoggerFactory.getLogger(WalletOperationUtil.class);

    //前提，钱包已加锁并重启。
    //钱包解密 walletpassphrase <passphrase> <timeout> [mintonly]
    public static void walletpassphrase(JsonRpcHttpClient rpcClient, String passphrase) throws Throwable {
        if (StringUtils.isEmpty(passphrase)) {
            return;
        }
        rpcClient.invoke("walletpassphrase", new Object[]{passphrase,90}, Object.class);


    }

    //钱包加锁walletlock
    public static void walletlock(JsonRpcHttpClient rpcClient) {
        try {
            rpcClient.invoke("walletlock",new Object[]{},Object.class);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }
}
