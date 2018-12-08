package com.deaking.wallet.silkscreen.controller;

import com.deaking.wallet.core.entity.CallContractResult;
import com.deaking.wallet.core.util.ECCUtil;
import com.deaking.wallet.core.util.MessageResult;
import com.deaking.wallet.core.util.WalletOperationUtil;
import com.deaking.wallet.silkscreen.config.JsonrpcClient;
import com.deaking.wallet.silkscreen.constant.ContractCompanyFunction;
import com.deaking.wallet.silkscreen.constant.TokenOptions;
import com.deaking.wallet.silkscreen.util.AddressUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;

import java.security.interfaces.ECPrivateKey;
import java.util.*;

/**
 * @author shenzucai
 * @time 2018.08.13 17:40
 */
@RestController
@RequestMapping("/company")
@Api(value = "SilkScreen Controller", tags = {"公司接口操作"})
public class CompanyController {

    @Autowired
    private JsonrpcClient rpcClient;
    private Logger logger = LoggerFactory.getLogger(CompanyController.class);


    /**
     * 合约地址
     */
    @Value("${token.address}")
    private String tokenAddress;

    /**
     * 经过椭圆曲线加密的密码
     */
    @Value("${coin.password}")
    private String password;

    /**
     * 椭圆曲线私钥
     */
    @Value("${dncrypt.password.privatekey}")
    private String privateKey;


    /**
     * 数据名
     */
    @Value("${data.name}")
    private String dataName;

    @Value("${network.type:test}")
    private String network;


    /**
     * 录入单位数据信息
     *companyInputDataInfo(string _dataType, bool _dataEncryption, string _publicKey)
     * @author shenzucai
     * @time 2018.08.14 15:42
     * @param fromAddress
     * @param dataEncryption
     * @param dataType
     * @param publicKey
     * @return true
     */
    @GetMapping("input/datainfo")
    @ApiOperation(value = "录入单位数据信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataType", value = "存入的数据类型(如温度，天气，健康状况等，非程序数据类型)", dataType = "String", paramType = "query", required = true, defaultValue = "温度"),
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(公司)", dataType = "String", paramType = "query", defaultValue = "SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6", required = true),
            @ApiImplicitParam(name = "dataEncryption", value = "是否加密（true,false）", dataType = "Boolean", required = true, paramType = "query"),
            @ApiImplicitParam(name = "publicKey", value = "用于数据加密的公钥", dataType = "String", required = false, paramType = "query")})
    public MessageResult companyInputDataInfo(String fromAddress, String dataEncryption, String dataType, String publicKey) {
        logger.info("option:fromAddress={},dataEncryption={},dataType = {},publicKey = {} ", fromAddress, dataEncryption, dataType, publicKey);

        // 进行解密
        if (StringUtils.isEmpty(privateKey)) {
            MessageResult result = new MessageResult(1, "wrong password");
            return result;
        }

        if (password.length() > 40) {
            try {
                ECPrivateKey privateKey1 = ECCUtil.string2PrivateKey(privateKey);
                password = ECCUtil.privateDecrypt(password, privateKey1);
            } catch (Exception e) {
                e.printStackTrace();
                return MessageResult.error(500, "error:" + e.getMessage());
            }
        }

        try {
            Function fn1 = new Function(ContractCompanyFunction.ISAPPROVEDCOMPANY, Arrays.asList(new Address(AddressUtil.SLUtoHash160(fromAddress))), Collections.singletonList(new TypeReference<Bool>() {}));
            String hexData = FunctionEncoder.encode(fn1);
            if(hexData.startsWith("0x")){
                hexData = hexData.substring(2);
            }
            // 判断是否是授权的公司
            CallContractResult callContractResult = rpcClient.callContractOption(fromAddress,tokenAddress,hexData);
            if (!TokenOptions.TRUE.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())) {
                return MessageResult.error(1, "只能授权的公司操作");
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        Function fn = new Function(ContractCompanyFunction.COMPANYINPUTDATAINFO, Arrays.asList(new Utf8String(dataType),new Bool(Boolean.valueOf(dataEncryption)),new Utf8String(publicKey)), Collections.<TypeReference<?>>emptyList());
        String hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletpassphrase(rpcClient, password);
            }
            MessageResult messageResult = rpcClient.sendContractOption(fromAddress, tokenAddress, hexData);
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletlock(rpcClient);
            }
            return messageResult;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        MessageResult result = new MessageResult(1, "fail");
        return result;
    }


    /**
     * 录入节点数据信息
     *nodeInputInfo(string _name, string _dataType, string _publicKey)
     * @author shenzucai
     * @time 2018.08.14 15:42
     * @param fromAddress
     * @param name
     * @param dataType
     * @param publicKey
     * @return true
     */
    @GetMapping("nodeinput/datainfo")
    @ApiOperation(value = "录入节点数据信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataType", value = "存入的数据类型(如温度，天气，健康状况等，非程序数据类型)", dataType = "String", paramType = "query", required = true, defaultValue = "温度"),
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(节点)", dataType = "String", paramType = "query", defaultValue = "SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9", required = true),
            @ApiImplicitParam(name = "name", value = "公司名称", dataType = "String", required = true, paramType = "query",defaultValue = "佛系"),
            @ApiImplicitParam(name = "publicKey", value = "用于数据加密的公钥", dataType = "String", required = true, paramType = "query",defaultValue = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEcRdItr0tUEN6XrakIw/GUrO55BzV52RMvb98Gb21lWI8RSdDFRYWH5RElg058rZCnE5/nZi2QWghEswp5znsyw==")})
    public MessageResult nodeInputInfo(String fromAddress, String name, String dataType, String publicKey) {
        logger.info("option:fromAddress={},name={},dataType = {},publicKey = {} ", fromAddress, name, dataType, publicKey);

        // 进行解密
        if (StringUtils.isEmpty(privateKey)) {
            MessageResult result = new MessageResult(1, "wrong password");
            return result;
        }

        if (password.length() > 40) {
            try {
                ECPrivateKey privateKey1 = ECCUtil.string2PrivateKey(privateKey);
                password = ECCUtil.privateDecrypt(password, privateKey1);
            } catch (Exception e) {
                e.printStackTrace();
                return MessageResult.error(500, "error:" + e.getMessage());
            }
        }

        Function fn = new Function(ContractCompanyFunction.NODEINPUTINFO, Arrays.asList(new Utf8String(name),new Utf8String(dataType),new Utf8String(publicKey)), Collections.<TypeReference<?>>emptyList());
        String hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletpassphrase(rpcClient, password);
            }
            MessageResult messageResult = rpcClient.sendContractOption(fromAddress, tokenAddress, hexData);
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletlock(rpcClient);
            }
            return messageResult;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        MessageResult result = new MessageResult(1, "fail");
        return result;
    }





    /**
     * 查询加密秘钥
     *companyGetEncryptedKey(string _dataType)
     * @author shenzucai
     * @time 2018.08.14 15:42
     * @param fromAddress
     * @param dataType
     * @return true
     */
    @GetMapping("encryptedkey")
    @ApiOperation(value = "查询加密秘钥")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataType", value = "存入的数据类型(如温度，天气，健康状况等，非程序数据类型)", dataType = "String", paramType = "query", required = true, defaultValue = "温度"),
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(公司)", dataType = "String", paramType = "query", defaultValue = "SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6", required = true)})
    public MessageResult companyGetEncryptedKey(String fromAddress,String dataType) {
        logger.info("option:fromAddress={},dataType = {}", fromAddress, dataType);
        try {
            Function fn1 = new Function(ContractCompanyFunction.ISAPPROVEDCOMPANY, Arrays.asList(new Address(AddressUtil.SLUtoHash160(fromAddress))), Collections.singletonList(new TypeReference<Bool>() {}));
            String hexData = FunctionEncoder.encode(fn1);
            if(hexData.startsWith("0x")){
                hexData = hexData.substring(2);
            }
            // 判断是否是授权的公司
            CallContractResult callContractResult = rpcClient.callContractOption(fromAddress,tokenAddress,hexData);
            if (!TokenOptions.TRUE.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())) {
                return MessageResult.error(1, "只能授权的公司操作");
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        Function fn = new Function(ContractCompanyFunction.COMPANYGETENCRYPTEDKEY, Arrays.asList(new Utf8String(dataType)),  Collections.singletonList(new TypeReference<Utf8String>() {}));
        String hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {

            CallContractResult callContractResult = rpcClient.callContractOption(fromAddress, tokenAddress, hexData);
            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(),fn.getOutputParameters());
                MessageResult messageResult = new MessageResult();
                messageResult.setCode(0);
                messageResult.setMessage(callContractResult.getExecutionResult().getExcepted());
                messageResult.setData(list);
                return messageResult;
            } else {
                return MessageResult.error(1, callContractResult.getExecutionResult().getExcepted());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        MessageResult result = new MessageResult(1, "fail");
        return result;
    }


    /**
     * 查询节点加密秘钥
     *nodeGetEncryptedKey(string _name, string _dataType)
     * @author shenzucai
     * @time 2018.08.14 15:42
     * @param fromAddress
     * @param dataType
     * @return true
     */
    @GetMapping("nodeEncryptedkey")
    @ApiOperation(value = "查询节点加密秘钥")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataType", value = "存入的数据类型(如温度，天气，健康状况等，非程序数据类型)", dataType = "String", paramType = "query", required = true, defaultValue = "温度"),
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(节点)", dataType = "String", paramType = "query", defaultValue = "SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9", required = true),
            @ApiImplicitParam(name = "name", value = "公司名称", dataType = "String", paramType = "query", defaultValue = "佛系", required = true)})
    public MessageResult nodeGetEncryptedKey(String fromAddress,String name,String dataType) {
        logger.info("option:fromAddress={},dataType = {},name = {}", fromAddress, dataType,name);

        Function fn = new Function(ContractCompanyFunction.NODEGETENCRYPTEDKEY, Arrays.asList(new Utf8String(name),new Utf8String(dataType)),  Collections.singletonList(new TypeReference<Utf8String>() {}));
        String hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {

            CallContractResult callContractResult = rpcClient.callContractOption(fromAddress, tokenAddress, hexData);
            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(),fn.getOutputParameters());
                MessageResult messageResult = new MessageResult();
                messageResult.setCode(0);
                messageResult.setMessage(callContractResult.getExecutionResult().getExcepted());
                messageResult.setData(list);
                return messageResult;
            } else {
                return MessageResult.error(1, callContractResult.getExecutionResult().getExcepted());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        MessageResult result = new MessageResult(1, "fail");
        return result;
    }


    /**
     * 移除节点数据类型（当数类型没有的时候，节点会被移除）
     * removeNode(address _address, string _name, string _dataType)
     * @author shenzucai
     * @time 2018.08.14 15:42
     * @param fromAddress
     * @param address
     * @param dataType
     * @param name
     * @return true
     */
    @GetMapping("removenode")
    @ApiOperation(value = "移除节点数据类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataType", value = "存入的数据类型(如温度，天气，健康状况等，非程序数据类型)", dataType = "String", paramType = "query", required = true, defaultValue = "温度"),
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(公司)", dataType = "String", paramType = "query", defaultValue = "SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6", required = true),
            @ApiImplicitParam(name = "address", value = "节点地址", dataType = "String", required = true, paramType = "query",defaultValue = "SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"),
            @ApiImplicitParam(name = "name", value = "公司名称", dataType = "String", required = false, paramType = "query",defaultValue = "佛系")})
    public MessageResult removeNode(String fromAddress, String address, String dataType, String name) {
        logger.info("option:fromAddress={},address={},dataType = {},name = {} ", fromAddress, address, dataType, name);

        // 进行解密
        if (StringUtils.isEmpty(privateKey)) {
            MessageResult result = new MessageResult(1, "wrong password");
            return result;
        }

        if (password.length() > 40) {
            try {
                ECPrivateKey privateKey1 = ECCUtil.string2PrivateKey(privateKey);
                password = ECCUtil.privateDecrypt(password, privateKey1);
            } catch (Exception e) {
                e.printStackTrace();
                return MessageResult.error(500, "error:" + e.getMessage());
            }
        }

        try {
            Function fn1 = new Function(ContractCompanyFunction.ISAPPROVEDCOMPANY, Arrays.asList(new Address(AddressUtil.SLUtoHash160(fromAddress))), Collections.singletonList(new TypeReference<Bool>() {}));
            String hexData = FunctionEncoder.encode(fn1);
            if(hexData.startsWith("0x")){
                hexData = hexData.substring(2);
            }
            // 判断是否是授权的公司
            CallContractResult callContractResult = rpcClient.callContractOption(fromAddress,tokenAddress,hexData);
            if (!TokenOptions.TRUE.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())) {
                return MessageResult.error(1, "只能授权的公司操作");
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        Function fn = new Function(ContractCompanyFunction.REMOVENODE, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address)),new Utf8String(name),new Utf8String(dataType)), Collections.<TypeReference<?>>emptyList());
        String hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletpassphrase(rpcClient, password);
            }
            MessageResult messageResult = rpcClient.sendContractOption(fromAddress, tokenAddress, hexData);
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletlock(rpcClient);
            }
            return messageResult;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        MessageResult result = new MessageResult(1, "fail");
        return result;
    }

    /**
     * 授权节点
     * approveNode(address _address, string _name, string _dataType)
     * @author shenzucai
     * @time 2018.08.14 15:42
     * @param fromAddress
     * @param address
     * @param dataType
     * @param name
     * @return true
     */
    @GetMapping("approvenode")
    @ApiOperation(value = "授权节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataType", value = "存入的数据类型(如温度，天气，健康状况等，非程序数据类型)", dataType = "String", paramType = "query", required = true, defaultValue = "温度"),
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(公司)", dataType = "String", paramType = "query", defaultValue = "SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6", required = true),
            @ApiImplicitParam(name = "address", value = "节点地址", dataType = "Boolean", required = true, paramType = "query",defaultValue = "SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"),
            @ApiImplicitParam(name = "name", value = "公司名称", dataType = "String", required = false, paramType = "query",defaultValue = "佛系")})
    public MessageResult approveNode(String fromAddress, String address, String dataType, String name) {
        logger.info("option:fromAddress={},address={},dataType = {},name = {} ", fromAddress, address, dataType, name);

        // 进行解密
        if (StringUtils.isEmpty(privateKey)) {
            MessageResult result = new MessageResult(1, "wrong password");
            return result;
        }

        if (password.length() > 40) {
            try {
                ECPrivateKey privateKey1 = ECCUtil.string2PrivateKey(privateKey);
                password = ECCUtil.privateDecrypt(password, privateKey1);
            } catch (Exception e) {
                e.printStackTrace();
                return MessageResult.error(500, "error:" + e.getMessage());
            }
        }

        try {
            Function fn1 = new Function(ContractCompanyFunction.ISAPPROVEDCOMPANY, Arrays.asList(new Address(AddressUtil.SLUtoHash160(fromAddress))), Collections.singletonList(new TypeReference<Bool>() {}));
            String hexData = FunctionEncoder.encode(fn1);
            if(hexData.startsWith("0x")){
                hexData = hexData.substring(2);
            }
            // 判断是否是授权的公司
            CallContractResult callContractResult = rpcClient.callContractOption(fromAddress,tokenAddress,hexData);
            if (!TokenOptions.TRUE.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())) {
                return MessageResult.error(1, "只能授权的公司操作");
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        Function fn = new Function(ContractCompanyFunction.APPROVENODE, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address)),new Utf8String(name),new Utf8String(dataType)), Collections.<TypeReference<?>>emptyList());
        String hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletpassphrase(rpcClient, password);
            }
            MessageResult messageResult = rpcClient.sendContractOption(fromAddress, tokenAddress, hexData);
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletlock(rpcClient);
            }
            return messageResult;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        MessageResult result = new MessageResult(1, "fail");
        return result;
    }

}
