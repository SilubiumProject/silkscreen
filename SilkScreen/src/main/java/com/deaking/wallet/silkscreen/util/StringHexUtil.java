package com.deaking.wallet.silkscreen.util;

import com.deaking.wallet.core.util.BinaryHexUtil;

/**
 * @author shenzucai
 * @time 2018.08.13 15:47
 */
public class StringHexUtil {

    /**
     * 将字符串转变成src的64位16进制
     * @author shenzucai
     * @time 2018.08.13 15:54
     * @param str
     * @return true
     */
    public static String toHexString(String str) throws Exception {

        byte[] bytes = str.getBytes("utf-8");
        if (bytes.length > 64) {
            throw new Exception("too long");
        }
        StringBuilder stringBuilder = new StringBuilder(BinaryHexUtil.BinaryToHexString(bytes));
        while(stringBuilder.length()<64){
            stringBuilder.append("0");
        }
        return stringBuilder.toString();
    }


    /**
     * 将16进制字符串转变成正常的字符串
     * @author shenzucai
     * @time 2018.08.13 15:54
     * @param hexStr
     * @return true
     */
    public static String toNormalString(String hexStr) throws Exception {

        StringBuilder stringBuilder = new StringBuilder(hexStr);
        while(stringBuilder.toString().endsWith("0")){
            if(stringBuilder.charAt(stringBuilder.length()-2) != '0' && stringBuilder.length()%2==0) {
                break;
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return new String(BinaryHexUtil.HexStringToBinary(stringBuilder.toString()),"utf-8");
    }

    public static String toHexLengthString(String str){
        String temp = Integer.toHexString(str.getBytes().length);
        Integer length = temp.length();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i < 64-length;i++){
            stringBuilder.append("0");
        }
        stringBuilder.append(temp);
        return stringBuilder.toString();
    }
}
