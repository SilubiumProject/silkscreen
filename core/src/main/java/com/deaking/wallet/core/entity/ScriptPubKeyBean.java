package com.deaking.wallet.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2018.08.11 02:49
 */
@NoArgsConstructor
@Data
public class ScriptPubKeyBean {
    /**
     * asm : OP_RETURN 126893
     * hex : 6a03adef01
     * type : nulldata
     */

    private String asm;
    private String hex;
    private String type;
}
