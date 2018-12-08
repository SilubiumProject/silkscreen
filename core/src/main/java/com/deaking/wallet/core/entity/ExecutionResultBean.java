package com.deaking.wallet.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.07.13 16:05
 */
@NoArgsConstructor
@Data
public class ExecutionResultBean {
    /**
     * output : 000000000000000000000000000000000000000000000000016340ec365088f0
     * gasUsed : 23344
     * gasForDeposit : 0
     * depositSize : 0
     * codeDeposit : 0
     * gasRefunded : 0
     * excepted : None
     * newAddress : c66f069e3c3600737b1116b06bc76903ae988cef
     */

    private String output;
    private int gasUsed;
    private int gasForDeposit;
    private int depositSize;
    private int codeDeposit;
    private int gasRefunded;
    private String excepted;
    private String newAddress;
}
