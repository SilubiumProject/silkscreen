package com.deaking.wallet.silkscreen.util;

import com.deaking.wallet.core.util.BinaryHexUtil;
import com.deaking.wallet.silkscreen.entity.SilkScreenData;

import java.io.UnsupportedEncodingException;

/**
 * @author shenzucai
 * @time 2018.08.10 07:56
 */
public class SilkScreenDataUtil {


    public static String packageData(SilkScreenData silkScreenData) {


        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append(BinaryHexUtil.BinaryToHexString(silkScreenData.getProtocolName().getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        stringBuilder.append(silkScreenData.getProtocolVersion());
        stringBuilder.append(silkScreenData.getContractAddress());
        stringBuilder.append(silkScreenData.getCompanyNum());
        stringBuilder.append(silkScreenData.getCurrentDataTypeNum());
        try {
            stringBuilder.append(BinaryHexUtil.BinaryToHexString(silkScreenData.getDataOfCompany().getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        stringBuilder.append(silkScreenData.getVerify());

        return stringBuilder.toString();

    }
}
