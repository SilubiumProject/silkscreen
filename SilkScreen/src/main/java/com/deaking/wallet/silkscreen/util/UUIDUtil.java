package com.deaking.wallet.silkscreen.util;

import java.util.Random;
import java.util.UUID;

/**
 * @author shenzucai
 * @time 2018.07.19 09:37
 */
public class UUIDUtil {

    public static String get33Char(){
        String char32 = UUID.randomUUID().toString().replaceAll("-","").toLowerCase();
        Random random = new Random();
        return char32+random.nextInt(9);
    }
}
