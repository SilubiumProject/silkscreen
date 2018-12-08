package com.deaking.wallet.core.util;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionType implements BaseEnum {
    RECHARGE("充值"),
    WITHDRAW("提现"),
    TRANSFER_ACCOUNTS("转账"),
    EXCHANGE("币币交易"),
    OTC_BUY("法币买入"),
    OTC_SELL("法币卖出"),
    ACTIVITY_AWARD("活动奖励"),
    PROMOTION_AWARD("推广奖励"),
    DIVIDEND("分红"),
    VOTE("投票"),
    ADMIN_RECHARGE("人工充值"),
    MATCH("配对");

    private String cnName;
    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }

    public static TransactionType valueOfOrdinal(int ordinal){
        switch (ordinal){
            case 0:return RECHARGE;
            case 1:return WITHDRAW;
            case 2:return TRANSFER_ACCOUNTS;
            case 3:return EXCHANGE;
            case 4:return OTC_BUY;
            case 5:return OTC_SELL;
            case 6:return ACTIVITY_AWARD;
            case 7:return PROMOTION_AWARD;
            case 8:return DIVIDEND;
            case 9:return VOTE;
            case 10:return ADMIN_RECHARGE;
        }
        return null;
    }
}
