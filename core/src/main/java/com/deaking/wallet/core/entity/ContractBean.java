package com.deaking.wallet.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.08.11 02:59
 */
@NoArgsConstructor
@Data
public class ContractBean {
    /**
     * contractAddress : mycontract
     * data : 00
     * gasLimit : 250000
     * gasPrice : 4.0E-7
     * amount : 0
     */

    private String contractAddress;
    private String data;
    private int gasLimit;
    private double gasPrice;
    private int amount;
}
