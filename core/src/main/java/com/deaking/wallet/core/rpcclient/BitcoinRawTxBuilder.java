package com.deaking.wallet.core.rpcclient;

import java.util.*;

/**
 * @author shenzucai
 * @time 2018.08.10 09:34
 */
public class BitcoinRawTxBuilder {
    public final Bitcoin bitcoin;
    public LinkedHashSet<Bitcoin.TxInput> inputs = new LinkedHashSet();
    public List<Bitcoin.TxOutput> outputs = new ArrayList();
    private HashMap<String, Bitcoin.RawTransaction> txCache = new HashMap();

    public BitcoinRawTxBuilder(Bitcoin bitcoin) {
        this.bitcoin = bitcoin;
    }

    public BitcoinRawTxBuilder in(Bitcoin.TxInput in) {
        this.inputs.add(new BitcoinRawTxBuilder.Input(in.txid(), in.vout()));
        return this;
    }

    public BitcoinRawTxBuilder in(String txid, int vout) {
        this.in(new Bitcoin.BasicTxInput(txid, vout));
        return this;
    }

    public BitcoinRawTxBuilder out(String address, double amount) {
        if(amount <= 0.0D) {
            return this;
        } else {
            this.outputs.add(new Bitcoin.BasicTxOutput(address, amount));
            return this;
        }
    }

    public BitcoinRawTxBuilder in(double value) throws BitcoinException {
        return this.in(value, 6);
    }

    public BitcoinRawTxBuilder in(double value, int minConf) throws BitcoinException {
        List<Bitcoin.Unspent> unspent = this.bitcoin.listUnspent(minConf);
        double v = value;
        Iterator var7 = unspent.iterator();

        while(var7.hasNext()) {
            Bitcoin.Unspent o = (Bitcoin.Unspent)var7.next();
            if(!this.inputs.contains(new BitcoinRawTxBuilder.Input(o))) {
                this.in(o);
                v = BitcoinUtil.normalizeAmount(v - o.amount());
            }

            if(v < 0.0D) {
                break;
            }
        }

        if(v > 0.0D) {
            throw new BitcoinException("Not enough bitcoins (" + v + "/" + value + ")");
        } else {
            return this;
        }
    }

    private Bitcoin.RawTransaction tx(String txId) throws BitcoinException {
        Bitcoin.RawTransaction tx = (Bitcoin.RawTransaction)this.txCache.get(txId);
        if(tx != null) {
            return tx;
        } else {
            tx = this.bitcoin.getRawTransaction(txId);
            this.txCache.put(txId, tx);
            return tx;
        }
    }

    public BitcoinRawTxBuilder outChange(String address) throws BitcoinException {
        return this.outChange(address, 0.0D);
    }

    public BitcoinRawTxBuilder outChange(String address, double fee) throws BitcoinException {
        double is = 0.0D;

        Bitcoin.TxInput i;
        for(Iterator var6 = this.inputs.iterator(); var6.hasNext(); is = BitcoinUtil.normalizeAmount(is + ((Bitcoin.RawTransaction.Out)this.tx(i.txid()).vOut().get(i.vout())).value())) {
            i = (Bitcoin.TxInput)var6.next();
        }

        double os = fee;

        Bitcoin.TxOutput o;
        for(Iterator var8 = this.outputs.iterator(); var8.hasNext(); os = BitcoinUtil.normalizeAmount(os + o.amount())) {
            o = (Bitcoin.TxOutput)var8.next();
        }

        if(os < is) {
            this.out(address, BitcoinUtil.normalizeAmount(is - os));
        }

        return this;
    }

    public String create() throws BitcoinException {
        return this.bitcoin.createRawTransaction(new ArrayList(this.inputs), this.outputs);
    }

    public String sign() throws BitcoinException {
        return this.bitcoin.signRawTransaction(this.create());
    }

    public String send() throws BitcoinException {
        return this.bitcoin.sendRawTransaction(this.sign());
    }

    private class Input extends Bitcoin.BasicTxInput {
        public Input(String txid, int vout) {
            super(txid, vout);
        }

        public Input(Bitcoin.TxInput copy) {
            this(copy.txid(), copy.vout());
        }
        @Override
        public int hashCode() {
            return this.txid.hashCode() + this.vout;
        }
        @Override
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            } else if(!(obj instanceof Bitcoin.TxInput)) {
                return false;
            } else {
                Bitcoin.TxInput other = (Bitcoin.TxInput)obj;
                return this.vout == other.vout() && this.txid.equals(other.txid());
            }
        }
    }
}
