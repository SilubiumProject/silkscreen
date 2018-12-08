package com.deaking.wallet.core.util;

/**
 * @author shenzucai
 * @time 2018.08.10 09:29
 */
public class JSON {
    public JSON() {
    }

    public static String stringify(Object o) {
        return com.alibaba.fastjson.JSON.toJSONString(o);
    }

    public static Object parse(String s) {
        return CrippledJavaScriptParser.parseJSExpr(s);
    }
}
