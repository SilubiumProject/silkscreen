package com.deaking.wallet.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.08.11 04:16
 */
@NoArgsConstructor
@Data
public class SignResult {


    /**
     * hex : 0200000001d71fda15c4c5eacb3439513b54223fe35ab6116d85664174679dc81a204044ff000000006a4730440220654a4736e640611ee0dbc0108f188e284f2023639a86ef65fc27927a264a9720022033ccdf539a1a3c6493a33ab791c7e72c1669ba57b238c7ac280c8dc8291677e0012102de692632d51762b1e6de9143835e8f1e03dbf5a76621bbfab17ce0457e12b5abffffffff020000000000000000056a03adef0190e52d47410b00001976a914f4ba7aa729ff2a33901045bddd83143d7d98619088ac00000000
     * complete : true
     */
    private String hex;
    private boolean complete;
}
