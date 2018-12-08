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
public class TokenTransaction {

    /**
     * blockHash : ae90f868b680dbd36c9b269881279affb4e311c7a897072274dfdb2f00940079
     * blockNumber : 178367
     * transactionHash : fb36a3aa258ad377cbb23464cf1661de9b5386716b81f1a79c49109956e45ae4
     * transactionIndex : 2
     * from : 9d047a921a97ecd141c71140c5ecd64d6533005e
     * to : b77633ec59685399ee04b557d8b650a52c2c55f5
     * cumulativeGasUsed : 51392
     * gasUsed : 51392
     * contractAddress : b77633ec59685399ee04b557d8b650a52c2c55f5
     * excepted : None
     * log : [{"address":"b77633ec59685399ee04b557d8b650a52c2c55f5","topics":["ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef","0000000000000000000000009d047a921a97ecd141c71140c5ecd64d6533005e","000000000000000000000000f3f79718297cb0791e0e41dff46ca198cc4b2aed"],"data":"000000000000000000000000000000000000000000000000000000174876e800"}]
     */

    private String blockHash;
    private int blockNumber;
    private String transactionHash;
    private int transactionIndex;
    private String from;
    private String to;
    private int cumulativeGasUsed;
    private int gasUsed;
    private String contractAddress;
    private String excepted;
    private List<LogBean> log;
}
