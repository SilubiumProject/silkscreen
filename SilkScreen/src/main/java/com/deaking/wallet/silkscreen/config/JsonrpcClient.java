package com.deaking.wallet.silkscreen.config;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.deaking.wallet.core.entity.*;
import com.deaking.wallet.core.util.BinaryHexUtil;
import com.deaking.wallet.core.util.JsonRpcHttpClient;
import com.deaking.wallet.core.util.MessageResult;
import com.deaking.wallet.silkscreen.constant.ContractCompanyFunction;
import com.deaking.wallet.silkscreen.constant.ContractManagerFunction;
import com.deaking.wallet.silkscreen.constant.TokenOptions;
import com.deaking.wallet.silkscreen.entity.DeployContractResult;
import com.deaking.wallet.silkscreen.util.AddressUtil;
import com.deaking.wallet.silkscreen.util.AmountFormate;
import com.deaking.wallet.silkscreen.util.FeeUtil;
import com.deaking.wallet.silkscreen.util.StringHexUtil;
import com.google.gson.*;

import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang.StringUtils;
import org.jhblockchain.crypto.bitcoin.BTCTransaction;
import org.jhblockchain.crypto.utils.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes1;
import org.web3j.abi.datatypes.generated.Uint8;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import java.net.URL;
import java.util.*;


/**
 * 基于比特币rpc接口开发新的功能
 * <p>
 * TODO
 * </p>
 *
 * @author: shangxl
 * @Date : 2017年11月16日 下午6:10:02
 */
public class JsonrpcClient extends JsonRpcHttpClient {


    private final static Gson gson = new GsonBuilder().registerTypeAdapter(Double.class,
            new JsonSerializer<Double>() {
                @Override
                public JsonElement serialize(Double value,
                                             Type theType, JsonSerializationContext context) {

                    // Keep 5 decimal digits only
                    return new JsonPrimitive((new BigDecimal(value)).setScale(5, BigDecimal.ROUND_HALF_UP));
                }
            }).serializeNulls().create();


    private Logger logger = LoggerFactory.getLogger(JsonrpcClient.class);

    public JsonrpcClient(URL serviceUrl) {
        super(serviceUrl);
    }

    public String decimal;


    @Value("${network.type:test}")
    private String network;

    /***
     * 获取RawTransaction 中的加密数据
     * @author shenzucai
     * @time 2018.07.13 16:51
     * @param txid
     * @return true
     */
    public String getRawTransaction(String txid) throws Throwable {

        Type type = new TypeToken<ResultBean>() {
        }.getType();
        ResultBean  resultBean= gson.fromJson(gson.toJson(invoke("getrawtransaction", new Object[]{txid, true},Object.class)),type);
        if(resultBean != null){
            List<VoutBean> voutBeans = resultBean.getVout();
            if(!voutBeans.isEmpty()){
                for(VoutBean voutBean:voutBeans){
                    if(voutBean.getScriptPubKey().getType().equalsIgnoreCase("nulldata") && voutBean.getScriptPubKey().getAsm().startsWith("OP_RETURN")){
                        return voutBean.getScriptPubKey().getAsm().substring(10);
                    }
                }
                return voutBeans.get(1).getScriptPubKey().getHex();
            }else{
                return null;
            }
        }
        return null;

    }

    /***
     * 构造RawTransaction
     * @author shenzucai
     * @time 2018.07.13 16:51
     * @param address
     * @param hexData
     * @return true
     */
    public String createRawTransaction(String address,String hexData) throws Throwable {


        if(StringUtils.isEmpty(hexData)){
            return null;
        }
        Type type = new TypeToken<List<UnSpent>>() {
        }.getType();

        List<TxVout> txVouts = new ArrayList<>();
        String[] strings = new String[]{address};
        List<UnSpent> listunspent = gson.fromJson(gson.toJson(invoke("listunspent", new Object[]{6,9999999,strings},Object.class)),type);
        double sumAmount = 0;
        for(UnSpent unSpent1:listunspent){
            if(sumAmount < 0.0002) {
                TxVout txVout = new TxVout();
                txVout.setTxid(unSpent1.getTxid());
                txVout.setVout(unSpent1.getVout());
                txVouts.add(txVout);
            }else{
                break;
            }
            sumAmount =FeeUtil.add(sumAmount,unSpent1.getAmount());
        }
        if(StringHexUtil.toNormalString(hexData).getBytes("utf-8").length > 1020){
            throw new Exception("数据过大");
        }

        JSONObject jsonObject = new JSONObject();
        double change = FeeUtil.sub(sumAmount,0.0002);
        if(change <0 ){
            throw new Exception("余额不足");
        }
        jsonObject.put(address,change);
        jsonObject.put("data",hexData);
        String hexTransation= gson.fromJson(gson.toJson(invoke("createrawtransaction", new Object[]{txVouts, jsonObject},Object.class)),String.class);
        return hexTransation;

    }


    /***
     * 签名交易
     * @author shenzucai
     * @time 2018.07.13 16:51
     * @param hexData
     * @return true
     */
    public SignResult signRawTransaction(String hexData) throws Throwable {


        Type type = new TypeToken<SignResult>() {
        }.getType();


        String json = gson.toJson(invoke("signrawtransaction", new Object[]{hexData},Object.class));
        SignResult signResult = gson.fromJson(json,type);

        return signResult;

    }


    /***
     * 广播交易
     * @author shenzucai
     * @time 2018.07.13 16:51
     * @param signResult
     * @return true
     */
    public String sendRawTransaction(SignResult signResult) throws Throwable {


        Type type = new TypeToken<String>() {
        }.getType();


        String txid = gson.fromJson(gson.toJson(invoke("sendrawtransaction", new Object[]{signResult.getHex()},Object.class)),type);

        return txid;

    }


    public static double normalizeAmount(double amount) {
        return (double)((long)(0.5D + amount / 1.0E-8D)) * 1.0E-8D;
    }


    /**
     * token transaction 操作
     * @author shenzucai
     * @time 2018.08.5 9:08
     * @param mainSluAddress 付款地址
     * @param tokenAddress 合约地址
     * @param userAddress 收款地址
     * @param amount 金额
     * @return true
     */
    public MessageResult tokenOption(String mainSluAddress, String tokenAddress, String userAddress, BigDecimal amount, final String optionType) throws Throwable {


        BigDecimal bigDecimal = BigDecimal.ZERO;
        // 获取hash160字符串
        String mainSluAddress64 = AddressUtil.to32bytes64char(AddressUtil.SLUtoHash160(mainSluAddress));
        String userAddress64 = AddressUtil.to32bytes64char(AddressUtil.SLUtoHash160(userAddress));
        String hexData = null;
        String hexTemp = null;
        Type type = new TypeToken<CallContractResult>() {
        }.getType();
        CallContractResult callContractResult = null;
        MessageResult messageResult = new MessageResult();
        switch (optionType){
            // 授权挖矿
            case TokenOptions.APPROVE_MINER:
                hexTemp = TokenOptions.IS_APPROVE_MINER+ userAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(!TokenOptions.OUTPUT.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "the address "+userAddress+" is alredy miner");
                }
                hexTemp = TokenOptions.MANAGER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(!mainSluAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "only manager can approve miner");
                }
                hexData = optionType + userAddress64;
                break;
            // 更换管理
            case TokenOptions.CHANGE_MANAGER:
                hexTemp = TokenOptions.OWNER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(!mainSluAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "only owner can change manager");
                }
                hexTemp = TokenOptions.MANAGER+ userAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(userAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "the address "+userAddress+" is alredy manager");
                }
                hexData = optionType + userAddress64;
                break;
            // 更换拥有者
            case TokenOptions.CHANGE_OWNER:
                hexTemp = TokenOptions.OWNER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(userAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "the address "+userAddress+" is alredy owner");
                }
                if(!mainSluAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "only owner can change owner");
                }
                hexData = optionType + userAddress64;
                break;
            // 移除挖矿
            case TokenOptions.REMOVE_MINER:
                hexTemp = TokenOptions.MANAGER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(!mainSluAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "only manager can approve miner");
                }
                hexTemp = TokenOptions.IS_APPROVE_MINER+ userAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(!TokenOptions.TRUE.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "the address "+userAddress+" is not miner,no need to remove");
                }
                hexData = optionType + userAddress64;
                break;
            // 初始化管理
            case TokenOptions.INITIAL_MANAGER:
                hexTemp = TokenOptions.MANAGER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(!TokenOptions.OUTPUT.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "alredy initial");
                }
                hexTemp = TokenOptions.OWNER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(userAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "the address "+userAddress+" is alredy owner,can't be manager");
                }
                if(!mainSluAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "only owner can initial manager");
                }
                hexTemp = TokenOptions.BALANCE_OF+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(callContractResult != null){
                    ExecutionResultBean executionResultBean = callContractResult.getExecutionResult();
                    if(executionResultBean != null){
                        String tamount = executionResultBean.getOutput();
                        if(!org.springframework.util.StringUtils.isEmpty(tamount)){
                            bigDecimal = AmountFormate.amount(tamount,"8");
                        }
                        if(bigDecimal.compareTo(amount)==-1){
                            return new MessageResult(1, "balance not enough");
                        }
                    }
                }
                hexData = optionType + userAddress64+ AmountFormate.toHexAmount(amount,"8");
                break;
            // 挖矿
            case TokenOptions.MINT:
                hexTemp = TokenOptions.IS_APPROVE_MINER+ userAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(!TokenOptions.TRUE.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "the address "+userAddress+" is not miner");
                }
                hexTemp = TokenOptions.MANAGER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(!mainSluAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "only manager can mint");
                }

                hexTemp = TokenOptions.OWNER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                hexTemp = TokenOptions.BALANCE_OF+ callContractResult.getExecutionResult().getOutput();
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(callContractResult != null){
                    ExecutionResultBean executionResultBean = callContractResult.getExecutionResult();
                    if(executionResultBean != null){
                        String tamount = executionResultBean.getOutput();
                        if(!org.springframework.util.StringUtils.isEmpty(tamount)){
                            bigDecimal = AmountFormate.amount(tamount,"8");
                        }
                        if(bigDecimal.compareTo(amount)==-1){
                            return new MessageResult(1, "balance not enough");
                        }
                    }
                }
                hexData = optionType +userAddress64+ AmountFormate.toHexAmount(amount,"8");
                break;
            // 回收
            case TokenOptions.RECYCLE:
                hexTemp = TokenOptions.MANAGER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(!mainSluAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "only manager can recycle");
                }
                hexTemp = TokenOptions.BALANCE_OF+ userAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(callContractResult != null){
                    ExecutionResultBean executionResultBean = callContractResult.getExecutionResult();
                    if(executionResultBean != null){
                        String tamount = executionResultBean.getOutput();
                        if(!org.springframework.util.StringUtils.isEmpty(tamount)){
                            bigDecimal = AmountFormate.amount(tamount,"8");
                        }
                        if(bigDecimal.compareTo(amount)==-1){
                            return new MessageResult(1, "balance not enough");
                        }
                    }
                }
                hexData = optionType + userAddress64+ AmountFormate.toHexAmount(amount,"8");
                break;
            // 获取管理员
            case TokenOptions.MANAGER:
                hexTemp = TokenOptions.MANAGER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                messageResult.setCode(0);
                messageResult.setMessage("success");
                if(TokenOptions.OUTPUT.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    messageResult.setData(null);
                }else {
                    messageResult.setData(AddressUtil.Hex64Hash160toSlu(callContractResult.getExecutionResult().getOutput(), network));
                }
                return messageResult;
            // 获取拥有者
            case TokenOptions.OWNER:
                hexTemp = TokenOptions.OWNER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                messageResult = new MessageResult();
                messageResult.setCode(0);
                messageResult.setMessage("success");
                if(TokenOptions.OUTPUT.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())) {
                    messageResult.setData(null);
                }else {
                    messageResult.setData(AddressUtil.Hex64Hash160toSlu(callContractResult.getExecutionResult().getOutput(), network));
                }
                return messageResult;
            // 判断是否是miner
            case TokenOptions.IS_APPROVE_MINER:
                hexTemp = TokenOptions.IS_APPROVE_MINER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                messageResult = new MessageResult();
                messageResult.setCode(0);
                messageResult.setMessage("success");
                messageResult.setData(TokenOptions.TRUE.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())?true:false);
                return messageResult;
            // 获取token余额
            case TokenOptions.BALANCE_OF:
                hexTemp = TokenOptions.BALANCE_OF+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                messageResult = new MessageResult();
                messageResult.setCode(0);
                messageResult.setMessage("success");
                messageResult.setData(AmountFormate.amount(callContractResult.getExecutionResult().getOutput(),decimal));
                return messageResult;
            // 添加管理员（上链）
            case TokenOptions.ADD_MANAGER:
                hexTemp = TokenOptions.IS_APPROVED_MANAGER+ userAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(!TokenOptions.OUTPUT.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "the address "+userAddress+" is alredy manager");
                }
                hexTemp = TokenOptions.OWNER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(userAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "the address "+userAddress+" is alredy owner,can't be manager");
                }
                if(!mainSluAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "only owner can initial manager");
                }
                hexData = optionType + userAddress64;
                break;
            // 是否是管理员（上链）
            case TokenOptions.IS_APPROVED_MANAGER:
                hexTemp = TokenOptions.IS_APPROVED_MANAGER+ userAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(TokenOptions.TRUE.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(0, "true");
                }else{
                    return new MessageResult(1, "false");
                }
            // 移除管理员 （上链）
            case TokenOptions.REMOVEMANAGER:
                hexTemp = TokenOptions.OWNER+ mainSluAddress64;
                callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexTemp,mainSluAddress},Object.class)),type);
                if(userAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "the address "+userAddress+" is owner,can't be remove");
                }
                if(!mainSluAddress64.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())){
                    return new MessageResult(1, "only owner can remove manager");
                }
                hexData = optionType + userAddress64;
                break;
            default:
        }
        Map<String,Object> o = (Map<String,Object>)invoke("sendtocontract", new Object[]{tokenAddress, hexData,0,2500000,new BigDecimal("0.0000001").toPlainString(),mainSluAddress},Object.class);
        String txid= String.valueOf(o.get("txid"));
        return MessageResult.success(txid);
    }


    /**
     * 发送合约通用方法
     * @author shenzucai
     * @time 2018.08.13 16:25
     * @param mainSluAddress 16进制地址
     * @param tokenAddress
     * @param hexData
     * @return true
     */
    public MessageResult sendContractOption(String mainSluAddress, String tokenAddress, String hexData) throws Throwable {

        Map<String,Object> o = (Map<String,Object>)invoke("sendtocontract", new Object[]{tokenAddress, hexData,0,2500000,new BigDecimal("0.0000002").toPlainString(),mainSluAddress},Object.class);
        String txid= String.valueOf(o.get("txid"));

        return MessageResult.success(txid);
    }

    /**
     * call调用合约通用方法
     * @author shenzucai
     * @time 2018.08.13 16:25
     * @param mainSluAddress 16进制地址
     * @param tokenAddress
     * @param hexData
     * @return true
     */
    public CallContractResult callContractOption(String mainSluAddress, String tokenAddress, String hexData) throws Throwable {

        Type type = new TypeToken<CallContractResult>() {
        }.getType();
        CallContractResult callContractResult = null;
        callContractResult = gson.fromJson(gson.toJson(invoke("callcontract", new Object[]{tokenAddress, hexData,mainSluAddress},Object.class)),type);
        return callContractResult;
    }


    /**
     *根据单位名称和数据名称到通用智能合约查询单位编号和数据类型编号
     * @author shenzucai
     * @time 2018.08.16 10:36
     * @param address
     * @param companyName
     * @param dataType
     * @return true
     */
    public List<String> getNumsOfCompany(String address,String tokenAddress,String companyName,String dataType){

        Function fn = new Function(ContractCompanyFunction.GETNUMSOFCOMPANY, Arrays.asList(new Utf8String(companyName),new Utf8String(dataType)),  Arrays.asList(new TypeReference<Uint8>() {},new TypeReference<Uint8>() {}));
        String hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {

            CallContractResult callContractResult = callContractOption(address, tokenAddress, hexData);
            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(),fn.getOutputParameters());
                if(list != null && list.size() == 2) {
                    List<String> strings = new ArrayList<>();
                    strings.add(BinaryHexUtil.BinaryToHexString(((Uint8)list.get(0)).getValue().toByteArray()));
                    strings.add(BinaryHexUtil.BinaryToHexString(((Uint8)list.get(1)).getValue().toByteArray()));
                    return strings;
                }else{
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    /**
     *获取协议名称
     * @author shenzucai
     * @time 2018.08.16 10:36
     * @param address

     * @return true
     */
    public String getProtocolName(String address,String tokenAddress){

        Function fn = new Function("protocolName", Arrays.asList(),  Collections.singletonList(new TypeReference<Utf8String>() {}));
        String hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {

            CallContractResult callContractResult = callContractOption(address, tokenAddress, hexData);
            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(),fn.getOutputParameters());
                if(list != null && !list.isEmpty()){
                    return String.valueOf(list.get(0));
                }else{
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    /**
     *获取协议版本
     * @author shenzucai
     * @time 2018.08.16 10:36
     * @param address

     * @return true
     */
    public String getProtocolVersion(String address,String tokenAddress){

        Function fn = new Function("protocolVersion", Arrays.asList(),  Collections.singletonList(new TypeReference<Bytes1>() {}));
        String hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {

            CallContractResult callContractResult = callContractOption(address, tokenAddress, hexData);
            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(),fn.getOutputParameters());
                if(list != null && !list.isEmpty()){
                    return BinaryHexUtil.BinaryToHexString(((Bytes1)list.get(0)).getValue());
                }else{
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    /**
     * 获取节点是否具有写入权限
     * getAuthOfNodes(address _address, string _name, string _dataType)
     * @author shenzucai
     * @time 2018.08.17 9:00
     * @param address
     * @param name
     * @param dataType
     * @param tokenAddress
     * @return true
     */
    public Boolean getAuthOfNodes(String address,String name,String dataType,String tokenAddress){

        Function fn = new Function(ContractManagerFunction.GETAUTHOFNODES, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address)),new Utf8String(name),new Utf8String(dataType)),  Collections.singletonList(new TypeReference<Bool>() {}));
        String hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {

            CallContractResult callContractResult = callContractOption(address, tokenAddress, hexData);
            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(),fn.getOutputParameters());
                if(list != null && !list.isEmpty()){
                    return ((Bool)list.get(0)).getValue();
                }else{
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }


    /**
     * 创建合约
     * @author shenzucai
     * @time 2018.08.13 16:25
     * @param fromAddres
     * @param hexData
     * @return true
     */
    public DeployContractResult createContract(String fromAddres, String hexData) throws Throwable {

        Type type = new TypeToken<DeployContractResult>() {
        }.getType();
        DeployContractResult deployContractResult = null;
        deployContractResult = gson.fromJson(gson.toJson(invoke("createcontract", new Object[]{hexData,40000000,0.0000001,fromAddres,true},Object.class)),type);
        return deployContractResult;
    }


    /**
     * 从指定地址发送金额到某个地址
     * @author shenzucai
     * @time 2018.08.18 9:22
     * @param fromAddress
     * @param address
     * @param amount
     * @return true
     */
    public String createRawTransaction(String fromAddress,String address,double amount) throws Throwable {

        Type type = new TypeToken<List<UnSpent>>() {
        }.getType();

        List<TxVout> txVouts = new ArrayList<>();
        String[] strings = new String[]{fromAddress};
        List<UnSpent> listunspent = gson.fromJson(gson.toJson(invoke("listunspent", new Object[]{6,9999999,strings},Object.class)),type);
        double sumAmount = 0;
        for(UnSpent unSpent1:listunspent){
            if(sumAmount < amount) {
                TxVout txVout = new TxVout();
                txVout.setTxid(unSpent1.getTxid());
                txVout.setVout(unSpent1.getVout());
                txVouts.add(txVout);
            }else{
                break;
            }
            sumAmount = FeeUtil.add(sumAmount,unSpent1.getAmount());
        }

        JSONObject jsonObject = new JSONObject();
        double change = FeeUtil.sub(sumAmount,amount);
        change = FeeUtil.sub(change,FeeUtil.getActualFee(amount));
        if(change<0){
            throw  new Exception("余额不足");
        }
        jsonObject.put(fromAddress,change);
        jsonObject.put(address,normalizeAmount(amount));
        String hexTransation= gson.fromJson(gson.toJson(invoke("createrawtransaction", new Object[]{txVouts, jsonObject},Object.class)),String.class);
        return hexTransation;

    }

}
