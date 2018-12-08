package com.deaking.wallet.silkscreen.controller;

import com.deaking.wallet.core.entity.CallContractResult;
import com.deaking.wallet.core.util.ECCUtil;
import com.deaking.wallet.core.util.MessageResult;
import com.deaking.wallet.core.util.WalletOperationUtil;
import com.deaking.wallet.silkscreen.config.JsonrpcClient;
import com.deaking.wallet.silkscreen.constant.ContractCompanyFunction;
import com.deaking.wallet.silkscreen.constant.ContractManagerFunction;
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
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint8;

import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.util.*;

/**
 * @author shenzucai
 * @time 2018.08.13 17:40
 */
@RestController
@RequestMapping("/manage")
@Api(value = "SilkScreen Controller", tags = {"管理员接口操作"})
public class ManagerController {

    @Autowired
    private JsonrpcClient rpcClient;
    private Logger logger = LoggerFactory.getLogger(ManagerController.class);


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

    /**
     * 网络类型
     */
    @Value("${network.type:test}")
    private String network;


    /**
     * 录入单位地址信息
     *
     * @param fromAddress
     * @param address
     * @param name
     * @return true
     * @author shenzucai
     * @time 2018.08.13 18:22
     */
    @GetMapping("company/save")
    @ApiOperation(value = "录入单位信息")
    @ApiImplicitParams({
            // @ApiImplicitParam(name = "optionType", value = "操作类型", dataType = "String", paramType = "query", required = true, defaultValue = "70a08231"),
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "name", value = "单位名称", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "address", value = "单位地址", dataType = "String", required = true, paramType = "query")})
    public MessageResult optionToken(String fromAddress, String address, String name) {
        logger.info("option:fromAddress={},address={},name = {} ", fromAddress, address, name);

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
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        Function fn = new Function(ContractManagerFunction.ENTERCOMPANYADDRESS, Arrays.asList(new Utf8String(name),new Address(AddressUtil.SLUtoHash160(address))), Collections.<TypeReference<?>>emptyList());
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
     * 录入节点地址信息
     *setNodeApproveInfo(address _address, string _name, string _dataType, bool _needCompany, bool _writeAuth)
     * @param fromAddress
     * @param address
     * @param name
     * @return true
     * @author shenzucai
     * @time 2018.08.13 18:22
     */
    @GetMapping("node/save")
    @ApiOperation(value = "录入节点信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "name", value = "公司名称", dataType = "String", required = true, paramType = "query",defaultValue = "佛系"),
            @ApiImplicitParam(name = "address", value = "节点地址", dataType = "String", required = true, paramType = "query",defaultValue = "SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "Boolean", required = true, paramType = "query",defaultValue = "温度"),
            @ApiImplicitParam(name = "needCompany", value = "是否需要公司授权", dataType = "Boolean", required = true, paramType = "query",defaultValue = "true"),
            @ApiImplicitParam(name = "writeAuth", value = "是否具有写数据权限", dataType = "Boolean", required = true, paramType = "query",defaultValue = "true")})
    public MessageResult setNodeApproveInfo(String fromAddress, String address, String name,String dataType,Boolean needCompany,Boolean writeAuth) {
        logger.info("option:fromAddress={},address={},name = {} ,dataType = {},needCompany = {},name = {}", fromAddress, address, name,dataType,needCompany,writeAuth);

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
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        Function fn = new Function(ContractManagerFunction.SETNODEAPPROVEINFO, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address)),new Utf8String(name),new Utf8String(dataType),new Bool(needCompany),new Bool(writeAuth)), Collections.<TypeReference<?>>emptyList());
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
     * 根据单位名称获取地址信息
     *
     * @param fromAddress
     * @param name
     * @return true
     * @author shenzucai
     * @time 2018.08.13 18:22
     */
    @GetMapping("company/address")
    @ApiOperation(value = "根据单位名称获取地址信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "name", value = "单位名称", dataType = "String", required = true, paramType = "query")})
    public MessageResult getCompanyAddress(String fromAddress, String name) {
        logger.info("option:fromAddress={},name = {} ", fromAddress, name);


        try {
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        String hexData = null;
        Function fn = new Function(ContractManagerFunction.GETCOMPANYADDRESS, Arrays.asList(new Utf8String(name)), Arrays.asList(new TypeReference<Address>() {}));
        hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {
            logger.debug("hexdata: {}",hexData);
            CallContractResult callContractResult = rpcClient.callContractOption(fromAddress, tokenAddress, hexData);
            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())  && !TokenOptions.OUTPUT.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())) {
                Map<String,String> stringMap = new HashMap<>();
                stringMap.put("address",AddressUtil.Hex64Hash160toSlu(callContractResult.getExecutionResult().getOutput().substring(0,64),network));
                MessageResult messageResult = new MessageResult();
                messageResult.setCode(0);
                messageResult.setMessage(callContractResult.getExecutionResult().getExcepted());
                messageResult.setData(stringMap);
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
     * 为单位地址授权
     *
     * @param fromAddress
     * @param address
     * @return true
     * @author shenzucai
     * @time 2018.08.13 18:22
     */
    @GetMapping("company/approve")
    @ApiOperation(value = "为单位地址授权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "address", value = "单位地址", dataType = "String", required = true, paramType = "query")})
    public MessageResult approveCompanyAddress(String fromAddress, String address) {
        logger.info("option:fromAddress={},address = {} ", fromAddress, address);

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
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        String hexData = null;
        Function fn = new Function(ContractManagerFunction.APPROVECOMPANY, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address))), Arrays.asList(new TypeReference<Bool>() {}));
        hexData = FunctionEncoder.encode(fn);
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
     * 为节点地址授权
     *approveNode(address _address, string _name, string _dataType)
     * @param fromAddress
     * @param address
     * @return true
     * @author shenzucai
     * @time 2018.08.13 18:22
     */
    @GetMapping("node/approve")
    @ApiOperation(value = "为节点地址授权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "address", value = "节点地址", dataType = "String", required = true, paramType = "query",defaultValue = "SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"),
            @ApiImplicitParam(name = "name", value = "公司名称", dataType = "String", required = true, paramType = "query",defaultValue = "佛系"),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "Boolean", required = true, paramType = "query",defaultValue = "温度")
    })
    public MessageResult approveNode(String fromAddress, String address,String name,String dataType) {
        logger.info("option:fromAddress={},address = {},name={} ,dataType={}", fromAddress, address,name,dataType);

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
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        String hexData = null;
        Function fn = new Function(ContractManagerFunction.APPROVENODE, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address)),new Utf8String(name),new Utf8String(dataType)), Arrays.asList(new TypeReference<Bool>() {}));
        hexData = FunctionEncoder.encode(fn);
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
     *查询单位公钥
     *
     * @param fromAddress
     * @param address
     * @param dataType
     * @return true
     * @author shenzucai
     * @time 2018.08.13 18:22
     */
    @GetMapping("company/publickey")
    @ApiOperation(value = "查询单位公钥")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "address", value = "单位地址", dataType = "String", required = true, paramType = "query",defaultValue = "SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6"),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "String", required = true, paramType = "query",defaultValue = "温度")})
    public MessageResult getCompanyPublicKey(String fromAddress, String address,String dataType) {
        logger.info("option:fromAddress={},address = {} ，dataType={}", fromAddress, address,dataType);


        try {
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        String hexData = null;
        Function fn = new Function(ContractManagerFunction.GETCOMPANYPUBLICKEY, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address)),new Utf8String(dataType)), Arrays.asList( new TypeReference<Utf8String>() {}));
        hexData = FunctionEncoder.encode(fn);
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
     *查询节点公钥
     *getNodePublicKey(address _address, string _name, string _dataType)
     * @param fromAddress
     * @param address
     * @param dataType
     * @return true
     * @author shenzucai
     * @time 2018.08.13 18:22
     */
    @GetMapping("node/publickey")
    @ApiOperation(value = "查询节点公钥")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "address", value = "节点地址", dataType = "String", required = true, paramType = "query",defaultValue = "SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6"),
            @ApiImplicitParam(name = "name", value = "公司名称", dataType = "String", required = true, paramType = "query",defaultValue = "佛系"),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "String", required = true, paramType = "query",defaultValue = "温度")})
    public MessageResult getNodePublicKey(String fromAddress, String address,String name,String dataType) {
        logger.info("option:fromAddress={},address = {} ，name={},dataType={}", fromAddress, address,name,dataType);


        try {
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        String hexData = null;
        Function fn = new Function(ContractManagerFunction.GETNODEPUBLICKEY, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address)),new Utf8String(name),new Utf8String(dataType)), Arrays.asList(new TypeReference<Bool>() {}, new TypeReference<Utf8String>() {}));
        hexData = FunctionEncoder.encode(fn);
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
     * 管理员录入合作公司数据
     *setManagerKeyInfo(string _name, string _dataType, string _publicKey, string _encryptedKey) public onlyManager returns (bool success)
     * @author shenzucai
     * @time 2018.08.15 10:20
     * @param fromAddress
     * @param name
     * @param dataType
     * @param publicKey
     * @param encryptedKey
     * @return true
     */
    @GetMapping("publickey/save")
    @ApiOperation(value = "管理员录入合作公司数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "name", value = "单位名称", dataType = "String", required = true, paramType = "query",defaultValue = "佛系"),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "String", required = true, paramType = "query",defaultValue = "温度"),
            @ApiImplicitParam(name = "publicKey", value = "管理员公钥", dataType = "String", required = true, paramType = "query",defaultValue = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEcRdItr0tUEN6XrakIw/GUrO55BzV52RMvb98Gb21lWI8RSdDFRYWH5RElg058rZCnE5/nZi2QWghEswp5znsyw=="),
            @ApiImplicitParam(name = "encryptedKey", value = "经公钥加密的通信秘钥", dataType = "String", required = true, paramType = "query",defaultValue = "BJxg7IIKxAfWSfnWlqDB5LCcQ7PGoJgsOf5Gss81Nnn5LCOi4Hyk69D35p2nZGWL4m053AIJO2dvoEbOaIsXDMInbjYlbIQtMif4ezOw/iGK+vndQD665SsjYQyYKufZw/2t")})
    public MessageResult setManagerKeyInfo(String fromAddress, String name,String dataType,String publicKey,String encryptedKey) {
        logger.info("option:fromAddress={},name = {} ,dataType = {} ,publicKey = {} ,encryptedKey = {}", fromAddress, name,dataType,publicKey,encryptedKey);

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
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        String hexData = null;
        Function fn = new Function(ContractManagerFunction.SETMANAGERKEYINFO, Arrays.asList(new Utf8String(name),new Utf8String(dataType),new Utf8String(publicKey),new Utf8String(encryptedKey)), Collections.emptyList());
        hexData = FunctionEncoder.encode(fn);
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
     * 为单位设置加密的秘钥
     *setCompanyKeyInfo(address _address, string _dataType, string _encryptedKey)
     * @author shenzucai
     * @time 2018.08.15 12:04
     * @param fromAddress
     * @param address
     * @param dataType
     * @param encryptedKey
     * @return true
     */
    @GetMapping("encryptedKey/save")
    @ApiOperation(value = "为单位设置加密的秘钥")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "address", value = "单位地址", dataType = "String", required = true, paramType = "query",defaultValue = "SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6"),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "String", required = true, paramType = "query",defaultValue = "温度"),
            @ApiImplicitParam(name = "encryptedKey", value = "经公钥加密的通信秘钥(明文秘钥长度必须小于56字节)", dataType = "String", required = true, paramType = "query",defaultValue = "BPc6QkE3EqceYzE3BsxjyURwj7cfAkXan3UWjkPfcEM3Xp8c5Cc6DZ0nccUT4ns7n6p2kJM1q2TSPuRjXDsCUccqBt5492ILGHgiz9zHstv5NG4clofkZOMhuIYjbFXp0KsQHNxVndXSVxZK/H1fyYbgMJBmR8LBUA==")})
    public MessageResult setCompanyKeyInfo(String fromAddress, String address,String dataType,String encryptedKey) {
        logger.info("option:fromAddress={},address = {} ,dataType = {} ,encryptedKey = {}", fromAddress, address,dataType,encryptedKey);

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
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        String hexData = null;
        Function fn = new Function(ContractManagerFunction.SETCOMPANYKEYINFO, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address)),new Utf8String(dataType),new Utf8String(encryptedKey)), Collections.emptyList());
        hexData = FunctionEncoder.encode(fn);
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
     * 为节点设置加密的秘钥
     *setNodeKeyInfo(address _address, string _name, string _dataType, string _encryptedKey)
     * @author shenzucai
     * @time 2018.08.15 12:04
     * @param fromAddress
     * @param address
     * @param dataType
     * @param encryptedKey
     * @return true
     */
    @GetMapping("nodeEncryptedKey/save")
    @ApiOperation(value = "为节点设置加密的秘钥")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "address", value = "节点地址", dataType = "String", required = true, paramType = "query",defaultValue = "SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"),
            @ApiImplicitParam(name = "name", value = "公司名称", dataType = "String", required = true, paramType = "query",defaultValue = "佛系"),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "String", required = true, paramType = "query",defaultValue = "温度"),
            @ApiImplicitParam(name = "encryptedKey", value = "经公钥加密的通信秘钥(明文秘钥长度必须小于56字节)", dataType = "String", required = true, paramType = "query",defaultValue = "BPc6QkE3EqceYzE3BsxjyURwj7cfAkXan3UWjkPfcEM3Xp8c5Cc6DZ0nccUT4ns7n6p2kJM1q2TSPuRjXDsCUccqBt5492ILGHgiz9zHstv5NG4clofkZOMhuIYjbFXp0KsQHNxVndXSVxZK/H1fyYbgMJBmR8LBUA==")})
    public MessageResult setNodeKeyInfo(String fromAddress, String address,String name,String dataType,String encryptedKey) {
        logger.info("option:fromAddress={},address = {} ,name = {} ,dataType = {} ,encryptedKey = {}", fromAddress, address,name,dataType,encryptedKey);

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
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        String hexData = null;
        Function fn = new Function(ContractManagerFunction.SETNODEKEYINFO, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address)),new Utf8String(name),new Utf8String(dataType),new Utf8String(encryptedKey)), Collections.emptyList());
        hexData = FunctionEncoder.encode(fn);
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
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6", required = true),
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
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
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
     * 移除授权公司
     * removeCompany(address _address, string _dataType)
     * @author shenzucai
     * @time 2018.08.14 15:42
     * @param fromAddress
     * @param address
     * @param dataType
     * @return true
     */
    @GetMapping("removeCompany")
    @ApiOperation(value = "移除授权公司数据类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataType", value = "存入的数据类型(如温度，天气，健康状况等，非程序数据类型)", dataType = "String", paramType = "query", required = true, defaultValue = "温度"),
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6", required = true),
            @ApiImplicitParam(name = "address", value = "公司地址", dataType = "String", required = true, paramType = "query",defaultValue = "SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9")})
    public MessageResult removeCompany(String fromAddress, String address, String dataType) {
        logger.info("option:fromAddress={},address={},dataType = {}", fromAddress, address, dataType);

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
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


        Function fn = new Function(ContractManagerFunction.REMOVECOMPANY, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address)),new Utf8String(dataType)), Collections.<TypeReference<?>>emptyList());
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
     *查询授权公司
     *getApprovedCompanyAddresses (uint _index)
     * @param fromAddress
     * @param index
     * @return true
     * @author shenzucai
     * @time 2018.08.13 18:22
     */
    @GetMapping("getApprovedCompanyAddresses")
    @ApiOperation(value = "查询授权公司")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "index", value = "单位地址", dataType = "String", required = true, paramType = "query",defaultValue = "0")})
    public MessageResult getApprovedCompanyAddresses(String fromAddress, String index) {
        logger.info("option:fromAddress={},index = {}", fromAddress, index);


        try {
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        String hexData = null;
        Function fn = new Function(ContractManagerFunction.GETAPPROVEDCOMPANYADDRESSES, Arrays.asList(new Uint(new BigInteger(index))), Arrays.asList(new TypeReference<Utf8String>() {}));
        hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {
            CallContractResult callContractResult = rpcClient.callContractOption(fromAddress, tokenAddress, hexData);

            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                MessageResult messageResult = new MessageResult();
                messageResult.setCode(0);
                messageResult.setMessage(callContractResult.getExecutionResult().getExcepted());
                if(!TokenOptions.OUTPUT.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())) {
                    messageResult.setData(AddressUtil.Hex64Hash160toSlu(callContractResult.getExecutionResult().getOutput(), network));
                }
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
     *查询授权节点
     *getApprovedNodeAddresses (uint _index)
     * @param fromAddress
     * @param index
     * @return true
     * @author shenzucai
     * @time 2018.08.13 18:22
     */
    @GetMapping("getApprovedNodeAddresses")
    @ApiOperation(value = "查询授权节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "index", value = "节点地址", dataType = "String", required = true, paramType = "query",defaultValue = "0")})
    public MessageResult getApprovedNodeAddresses(String fromAddress, String index) {
        logger.info("option:fromAddress={},index = {}", fromAddress, index);


        try {
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        String hexData = null;
        Function fn = new Function(ContractManagerFunction.GETAPPROVEDNODEADDRESSES, Arrays.asList(new Uint(new BigInteger(index))), Arrays.asList(new TypeReference<Utf8String>() {}));
        hexData = FunctionEncoder.encode(fn);
        if(hexData.startsWith("0x")){
            hexData = hexData.substring(2);
        }
        try {
            CallContractResult callContractResult = rpcClient.callContractOption(fromAddress, tokenAddress, hexData);

            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                MessageResult messageResult = new MessageResult();
                messageResult.setCode(0);
                messageResult.setMessage(callContractResult.getExecutionResult().getExcepted());
                if(!TokenOptions.OUTPUT.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())) {
                    messageResult.setData(AddressUtil.Hex64Hash160toSlu(callContractResult.getExecutionResult().getOutput(), network));
                }
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
     * 查询管理员公钥
     *getManagerKeyInfo(string _name, string _dataType)
     * @author shenzucai
     * @time 2018.08.17 16:40
     * @param fromAddress
     * @param name
     * @param dataType
     * @return true
     */
    @GetMapping("getManagerKeyInfo")
    @ApiOperation(value = "查询管理员公钥")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(管理员)", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "name", value = "公司名称", dataType = "String", required = true, paramType = "query",defaultValue = "佛系"),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "String", required = true, paramType = "query",defaultValue = "温度")})
    public MessageResult getManagerKeyInfo(String fromAddress, String name,String dataType) {
        logger.info("option:fromAddress={},name = {},dataType = {}", fromAddress, name,dataType);


        try {
            // 判断是否是管理员
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, fromAddress,null, TokenOptions.IS_APPROVED_MANAGER);
            if (messageResult.getCode() != 0) {
                MessageResult result = new MessageResult(1, "只有管理员才能操作");
                return result;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        String hexData = null;
        Function fn = new Function(ContractManagerFunction.GETMANAGERKEYINFO, Arrays.asList(new Utf8String(name),new Utf8String(dataType)), Arrays.asList(new TypeReference<Utf8String>() {},new TypeReference<Utf8String>() {}));
        hexData = FunctionEncoder.encode(fn);
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
}
