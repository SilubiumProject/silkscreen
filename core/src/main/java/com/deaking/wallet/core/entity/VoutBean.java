package com.deaking.wallet.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.08.11 02:49
 */
@NoArgsConstructor
@Data
public class VoutBean {
    /**
     * value : 0
     * n : 0
     * scriptPubKey : {"asm":"OP_RETURN 126893","hex":"6a03adef01","type":"nulldata"}
     */

    private double value;
    private int n;
    private ScriptPubKeyBean scriptPubKey;
}
