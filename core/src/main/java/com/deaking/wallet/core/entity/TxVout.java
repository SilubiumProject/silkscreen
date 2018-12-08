package com.deaking.wallet.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.08.11 02:52
 */
@NoArgsConstructor
@Data
public class TxVout {


    /**
     * txid : myid
     * vout : 0
     */

    private String txid;
    private int vout;
}
