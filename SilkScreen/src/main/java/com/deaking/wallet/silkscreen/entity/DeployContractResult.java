package com.deaking.wallet.silkscreen.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.08.17 19:11
 */
@NoArgsConstructor
@Data
public class DeployContractResult {

    /**
     * txid : fa10db4eb6043f068227dcec7dc3c4fa5576bd1eed6e5b07eb6f6d321b4b0ec6
     * sender : SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm
     * hash160 : f4ba7aa729ff2a33901045bddd83143d7d986190
     * address : 8b5525363f3cf29405e2c7e27741128dcf60f258
     */

    private String txid;
    private String sender;
    private String hash160;
    private String address;
}
