package com.deaking.wallet.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.08.11 03:39
 */
@NoArgsConstructor
@Data
public class UnSpent {

    /**
     * txid : 0b879928bcabdb71fae7d8953251ba005e460be34f00f90acdc5820c0059fc31
     * vout : 7
     * address : SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm
     * account : yes
     * scriptPubKey : 76a914f4ba7aa729ff2a33901045bddd83143d7d98619088ac
     * amount : 0.0601316
     * confirmations : 3366
     * spendable : true
     * solvable : true
     * safe : true
     */

    private String txid;
    private int vout;
    private String address;
    private String account;
    private String scriptPubKey;
    private double amount;
    private int confirmations;
    private boolean spendable;
    private boolean solvable;
    private boolean safe;
}
