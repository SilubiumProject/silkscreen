package com.deaking.wallet.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.08.11 02:58
 */
@NoArgsConstructor
@Data
public class TxContract {

    /**
     * contract : {"contractAddress":"mycontract","data":"00","gasLimit":250000,"gasPrice":4.0E-7,"amount":0}
     */

    private ContractBean contract;
}
