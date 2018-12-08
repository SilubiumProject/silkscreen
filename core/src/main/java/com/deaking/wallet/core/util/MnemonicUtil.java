package com.deaking.wallet.core.util;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;

import java.security.SecureRandom;

/**
 * @author shenzucai
 * @time 2018.06.08 19:50
 */
public class MnemonicUtil {

    /**
     * 生成助记词
     *
     * @param
     * @return true
     * @author shenzucai
     * @time 2018.06.08 20:11
     */
    public static String get12Word() {


        StringBuilder sb = new StringBuilder();
        byte[] entropy = new byte[Words.TWELVE.byteLength()];
        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE)
                .createMnemonic(entropy, sb::append);
        System.out.println(sb.toString());
        return sb.toString();
    }
}
