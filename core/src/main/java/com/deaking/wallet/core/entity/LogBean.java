package com.deaking.wallet.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shenzucai
 * @time 2018.07.18 15:00
 */
@NoArgsConstructor
@Data
public class LogBean {
    /**
     * address : b77633ec59685399ee04b557d8b650a52c2c55f5
     * topics : ["ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef","0000000000000000000000009d047a921a97ecd141c71140c5ecd64d6533005e","000000000000000000000000f3f79718297cb0791e0e41dff46ca198cc4b2aed"]
     * data : 000000000000000000000000000000000000000000000000000000174876e800
     */

    private String address;
    private String data;
    private List<String> topics;
}
