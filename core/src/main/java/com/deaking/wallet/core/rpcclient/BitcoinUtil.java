package com.deaking.wallet.core.rpcclient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author shenzucai
 * @time 2018.08.10 09:36
 */
public class BitcoinUtil {
    public BitcoinUtil() {
    }

    public static double normalizeAmount(double amount) {
        return (double)((long)(0.5D + amount / 1.0E-8D)) * 1.0E-8D;
    }

    public static String sendTransaction(Bitcoin bitcoin, String targetAddress, Double amount, Double txFee) throws BitcoinException {
        List<Bitcoin.Unspent> unspents = bitcoin.listUnspent(3);
        amount = Double.valueOf(normalizeAmount(amount.doubleValue()));
        txFee = Double.valueOf(normalizeAmount(txFee.doubleValue()));
        double moneySpent = 0.0D;
        double moneyChange = 0.0D;
        if(unspents.size() == 0) {
            throw new BitcoinException("insufficient coin");
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String account = "acct-change-" + df.format(new Date());
            String changeAddress = bitcoin.getAccountAddress(account);
            if(changeAddress == null) {
                changeAddress = bitcoin.getNewAddress(account);
            }

            System.out.println("change address:" + changeAddress);
            BitcoinRawTxBuilder builder = new BitcoinRawTxBuilder(bitcoin);
            Iterator var13 = unspents.iterator();

            while(var13.hasNext()) {
                Bitcoin.Unspent unspent = (Bitcoin.Unspent)var13.next();
                double amt = normalizeAmount(unspent.amount());
                moneySpent += amt;
                builder.in(new Bitcoin.BasicTxInput(unspent.txid(), unspent.vout()));
                if(moneySpent >= amount.doubleValue() + txFee.doubleValue()) {
                    break;
                }
            }

            if(moneySpent < amount.doubleValue() + txFee.doubleValue()) {
                throw new BitcoinException("insufficient coin");
            } else {
                moneyChange = normalizeAmount(moneySpent - amount.doubleValue() - txFee.doubleValue());
                System.out.println("moneyChange:" + moneyChange);
                builder.out(targetAddress, amount.doubleValue());
                if(moneyChange > 0.0D) {
                    builder.out(changeAddress, moneyChange);
                }

                return builder.send();
            }
        }
    }
}

