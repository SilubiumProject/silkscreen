package com.deaking.wallet.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shenzucai
 * @time 2018.08.17 14:13
 */
@NoArgsConstructor
@Data
public class ResultBean {
    /**
     * txid : 2a66a34fbf69070517b1ae5b72d60a612354e637022b76b1a3cad41443c2c704
     * hash : 2a66a34fbf69070517b1ae5b72d60a612354e637022b76b1a3cad41443c2c704
     * version : 2
     * size : 293
     * vsize : 293
     * locktime : 0
     * vin : [{"txid":"7910a4313c5a565ad66643875efcfa293dca2c3cb5ba34fae24ab4ef22aef633","vout":7,"scriptSig":{"asm":"3044022067432d6772eee04ce2ce396e631be43d431d735c1a173ca5a137a3912970b5c002207d445df0f0a8ed6c534998e6d784bc4c223d8a1c64be750ccf6e897ad4dbe32c[ALL] 021fe4249738cda8db71138b065eb55fab85003566222f21991ef0511d9e832c43","hex":"473044022067432d6772eee04ce2ce396e631be43d431d735c1a173ca5a137a3912970b5c002207d445df0f0a8ed6c534998e6d784bc4c223d8a1c64be750ccf6e897ad4dbe32c0121021fe4249738cda8db71138b065eb55fab85003566222f21991ef0511d9e832c43"},"sequence":4294967295}]
     * vout : [{"value":0,"n":0,"scriptPubKey":{"asm":"OP_RETURN 53696c6b73637265656e3031343164303838346133663930666538613135643134386361666130333231316534393364363765313030383161333064303936366131633032373966316138623233356635646331633064623833","hex":"6a4c5a53696c6b73637265656e3031343164303838346133663930666538613135643134386361666130333231316534393364363765313030383161333064303936366131633032373966316138623233356635646331633064623833","type":"nulldata"}},{"value":0.45925,"n":1,"scriptPubKey":{"asm":"OP_DUP OP_HASH160 2ca435386a712b12c3e833e13c734bc946d77c4a OP_EQUALVERIFY OP_CHECKSIG","hex":"76a9142ca435386a712b12c3e833e13c734bc946d77c4a88ac","reqSigs":1,"type":"pubkeyhash","addresses":["SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6"]}}]
     * hex : 020000000133f6ae22efb44ae2fa34bab53c2cca3d29fafc5e874366d65a565a3c31a41079070000006a473044022067432d6772eee04ce2ce396e631be43d431d735c1a173ca5a137a3912970b5c002207d445df0f0a8ed6c534998e6d784bc4c223d8a1c64be750ccf6e897ad4dbe32c0121021fe4249738cda8db71138b065eb55fab85003566222f21991ef0511d9e832c43ffffffff0200000000000000005d6a4c5a53696c6b73637265656e303134316430383834613366393066653861313564313438636166613033323131653439336436376531303038316133306430393636613163303237396631613862323335663564633163306462383388c2bc02000000001976a9142ca435386a712b12c3e833e13c734bc946d77c4a88ac00000000
     * blockhash : c5cc8aed55859bfd1c0c42134b65871dc237eb33211362c0d57b00a965a22126
     * confirmations : 145
     * time : 1534465600
     * blocktime : 1534465600
     */

    private String txid;
    private String hash;
    private int version;
    private int size;
    private int vsize;
    private int locktime;
    private String hex;
    private String blockhash;
    private int confirmations;
    private int time;
    private int blocktime;
    private List<VinBean> vin;
    private List<VoutBean> vout;
}
