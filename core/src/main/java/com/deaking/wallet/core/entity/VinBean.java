package com.deaking.wallet.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.08.11 02:48
 */
@NoArgsConstructor
@Data
public class VinBean {
    /**
     * txid : ff4440201ac89d67744166856d11b65ae33f22543b513934cbeac5c415da1fd7
     * vout : 0
     * scriptSig : {"asm":"30440220654a4736e640611ee0dbc0108f188e284f2023639a86ef65fc27927a264a9720022033ccdf539a1a3c6493a33ab791c7e72c1669ba57b238c7ac280c8dc8291677e0[ALL] 02de692632d51762b1e6de9143835e8f1e03dbf5a76621bbfab17ce0457e12b5ab","hex":"4730440220654a4736e640611ee0dbc0108f188e284f2023639a86ef65fc27927a264a9720022033ccdf539a1a3c6493a33ab791c7e72c1669ba57b238c7ac280c8dc8291677e0012102de692632d51762b1e6de9143835e8f1e03dbf5a76621bbfab17ce0457e12b5ab"}
     * sequence : 4294967295
     */

    private String txid;
    private int vout;
    private ScriptSigBean scriptSig;
    private long sequence;
}
