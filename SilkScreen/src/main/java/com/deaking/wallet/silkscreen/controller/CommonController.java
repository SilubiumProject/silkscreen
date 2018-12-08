package com.deaking.wallet.silkscreen.controller;


import com.deaking.wallet.core.entity.CallContractResult;
import com.deaking.wallet.core.rpcclient.Bitcoin;
import com.deaking.wallet.core.rpcclient.BitcoinException;
import com.deaking.wallet.core.rpcclient.BitcoinRPCClient;
import com.deaking.wallet.core.util.*;
import com.deaking.wallet.silkscreen.config.JsonrpcClient;
import com.deaking.wallet.silkscreen.constant.ContractCompanyFunction;
import com.deaking.wallet.silkscreen.constant.ContractManagerFunction;
import com.deaking.wallet.silkscreen.constant.TokenOptions;
import com.deaking.wallet.silkscreen.entity.DeployContractResult;
import com.deaking.wallet.silkscreen.entity.SilkScreenData;
import com.deaking.wallet.silkscreen.util.AddressUtil;
import com.deaking.wallet.silkscreen.util.SilkScreenDataUtil;
import com.deaking.wallet.silkscreen.util.StringHexUtil;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jcajce.provider.symmetric.Blowfish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Hash;
import sun.rmi.runtime.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.*;


@RestController
@RequestMapping("/common")
@Api(value = "SilkScreen Controller", tags = {"公共接口操作"})
public class CommonController {
    @Autowired
    private JsonrpcClient rpcClient;

    @Autowired
    private BitcoinRPCClient bitcoinRPCClient;
    private Logger logger = LoggerFactory.getLogger(CommonController.class);


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
     * 密码椭圆曲线私钥
     */
    @Value("${dncrypt.password.privatekey}")
    private String privateKey;

    /**
     * 密码椭圆曲线私钥
     */
    @Value("${dncrypt.data.privatekey}")
    private String dataPrivateKey;


    @GetMapping("address/{account}")
    @ApiOperation(value = "获取SLU新地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账户名称", dataType = "String", paramType = "path", required = true, defaultValue = "DEAKING"),})
    public MessageResult getNewAddress(@PathVariable String account) {
        logger.info("create new address :" + account);

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
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletpassphrase(rpcClient, password);
            }
            String address = bitcoinRPCClient.getNewAddress(account);
            MessageResult messageResult = new MessageResult();
            messageResult.setCode(0);
            messageResult.setMessage("success");
            messageResult.setData(address);
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletlock(rpcClient);
            }
            return messageResult;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return MessageResult.error(500, "error:" + throwable.getMessage());
        }
    }

    @GetMapping("privkey/{address}")
    @ApiOperation(value = "导出地址私钥")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "地址", dataType = "String", paramType = "path", required = true, defaultValue = "SLSPT9L78iVrcsPNKN18rojAYyz1dg6UPqPU"),})
    public MessageResult getPrivKeyByAddress(@PathVariable String address) {
        logger.info("create new address :" + address);

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
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletpassphrase(rpcClient, password);
            }
            String PrivKey = bitcoinRPCClient.dumpPrivKey(address);
            MessageResult messageResult = new MessageResult();
            messageResult.setCode(0);
            messageResult.setMessage("success");
            messageResult.setData(PrivKey);
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletlock(rpcClient);
            }
            return messageResult;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return MessageResult.error(500, "error:" + throwable.getMessage());
        }
    }

    @GetMapping("balance/{address}")
    @ApiOperation(value = "获取地址余额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "地址", dataType = "String", paramType = "path", required = true, defaultValue = "SLSPT9L78iVrcsPNKN18rojAYyz1dg6UPqPU")})
    public MessageResult getBalanceByAddress(@PathVariable String address) {
        logger.info("create new address :" + address);

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
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletpassphrase(rpcClient, password);
            }
            List<Bitcoin.Unspent> unspents = bitcoinRPCClient.listUnspent(10, 9999999, address);
            Iterator var13 = unspents.iterator();

            double balance = 0;
            while (var13.hasNext()) {
                Bitcoin.Unspent unspent = (Bitcoin.Unspent) var13.next();
                double amt = normalizeAmount(unspent.amount());
                balance += amt;
            }
            MessageResult messageResult = new MessageResult();
            messageResult.setCode(0);
            messageResult.setMessage("success");
            messageResult.setData(balance);
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletlock(rpcClient);
            }
            return messageResult;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return MessageResult.error(500, "error:" + throwable.getMessage());
        }
    }

    public double normalizeAmount(double amount) {
        return (double) ((long) (0.5D + amount / 1.0E-8D)) * 1.0E-8D;
    }

    /**
     * token操作
     *
     * @param fromAddress
     * @param toAddress
     * @param optionType
     * @return true
     * @author shenzucai
     * @time 2018.08.04 15:15
     */
    @GetMapping("option")
    @ApiOperation(value = "token操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "optionType", value = "操作类型 (2d06177a) (ac18de43)", dataType = "String", paramType = "query", required = true, defaultValue = "2d06177a"),
            @ApiImplicitParam(name = "fromAddress", value = "发送地址", dataType = "String", paramType = "query", defaultValue = "SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm", required = true),
            @ApiImplicitParam(name = "toAddress", value = "接收地址", dataType = "String", paramType = "query", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL")})
    public MessageResult optionToken(String fromAddress, String toAddress, String optionType) {
        logger.info("option:fromAddress={},toAddress={},optionType = {}", fromAddress, toAddress, optionType);

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
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletpassphrase(rpcClient, password);
            }
            MessageResult messageResult = rpcClient.tokenOption(fromAddress, tokenAddress, toAddress, null, optionType);
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
     * 生成椭圆曲线秘钥对
     *
     * @return true
     * @author shenzucai
     * @time 2018.08.04 15:15
     */
    @GetMapping("generateKeyPair")
    @ApiOperation(value = "生成椭圆曲线秘钥对")
    public MessageResult generateKeyPair() {

        try {
            KeyPair keyPair = ECCUtil.getKeyPair();
            String publicKeyStr = ECCUtil.getPublicKey(keyPair);
            String privateKeyStr = ECCUtil.getPrivateKey(keyPair);
            Map<String, String> stringMap = new HashMap<>();
            stringMap.put("publicKeyStr", publicKeyStr);
            stringMap.put("privateKeyStr", privateKeyStr);
            MessageResult result = new MessageResult(0, "success");
            result.setData(stringMap);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        }

    }

    /**
     * 使用私钥解密
     *
     * @return true
     * @author shenzucai
     * @time 2018.08.04 15:15
     */
    @GetMapping("dncrypt")
    @ApiOperation(value = "使用私钥解密")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "privateKeyStr", value = "私钥", dataType = "String", paramType = "query", defaultValue = "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgM22umyKq4ZsxFzg0w5ChGRnQc93EFRiqMQLd5hpaRdmgCgYIKoZIzj0DAQehRANCAARxF0i2vS1QQ3petqQjD8ZSs7nkHNXnZEy9v3wZvbWVYjxFJ0MVFhYflESWDTnytkKcTn+dmLZBaCESzCnnOezL", required = true),
            @ApiImplicitParam(name = "content", value = "文本", dataType = "String", paramType = "query", defaultValue = "BO+q8UZzhAdssiTpcjsd//119ex0Ip0P6c2epiMIjOBnk+n+SiMLldgMC9VROEMfwlh6K3rwMm4xw/SAzCNPACYprA8NlnvXEMQll2YvzXOyuo5tkjtlXk602yY=", required = true)
    })
    public MessageResult dncrypt(String privateKeyStr, String content) {

        try {
            ECPrivateKey privateKey = ECCUtil.string2PrivateKey(privateKeyStr);
            String privateKeyDncrypt = ECCUtil.privateDecrypt(content, privateKey);
            MessageResult result = new MessageResult(0, "success");
            result.setData(privateKeyDncrypt);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        }

    }

    /**
     * 使用公钥加密
     *
     * @return true
     * @author shenzucai
     * @time 2018.08.04 15:15
     */
    @GetMapping("encrypt")
    @ApiOperation(value = "使用公钥加密")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "publicKeyStr", value = "公钥", dataType = "String", paramType = "query", defaultValue = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEcRdItr0tUEN6XrakIw/GUrO55BzV52RMvb98Gb21lWI8RSdDFRYWH5RElg058rZCnE5/nZi2QWghEswp5znsyw==", required = true),
            @ApiImplicitParam(name = "content", value = "文本", dataType = "String", paramType = "query", defaultValue = "content", required = true)
    })
    public MessageResult encrypt(String publicKeyStr, String content) {

        try {
            ECPublicKey publicKey = ECCUtil.string2PublicKey(publicKeyStr);
            byte[] bytes = content.getBytes("utf-8");
            logger.info("未加密是数据长度 {}", bytes.length);
            String publicEncrypt = ECCUtil.publicEncrypt(bytes, publicKey);
            MessageResult result = new MessageResult(0, "success");
            logger.info("加密数据长度 {}", publicEncrypt.getBytes("utf-8").length);
            result.setData(publicEncrypt);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        }

    }


    /**
     * 公司数据上链接口
     *
     * @return true
     * @author shenzucai
     * @time 2018.08.04 15:15
     */
    @RequestMapping(value = "deploydata", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiOperation(value = "公司数据上链接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "公司地址", dataType = "String", paramType = "form", defaultValue = "SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6", required = true),
            @ApiImplicitParam(name = "companyName", value = "公司名称", dataType = "String", paramType = "form", defaultValue = "佛系", required = true),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "String", paramType = "form", defaultValue = "温度", required = true),
            @ApiImplicitParam(name = "data", value = "上载数据（utf-8明文487个字节,中文字符162个，英文字符487个）", dataType = "String", paramType = "form", defaultValue = "测试数据", required = true),
    })
    public MessageResult deployData(String address, String companyName, String dataType, String data) throws UnsupportedEncodingException {


        try {
            Function fn1 = new Function(ContractCompanyFunction.ISAPPROVEDCOMPANY, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address))), Collections.singletonList(new TypeReference<Bool>() {
            }));
            String hexData = FunctionEncoder.encode(fn1);
            if (hexData.startsWith("0x")) {
                hexData = hexData.substring(2);
            }
            // 判断是否是授权的公司
            CallContractResult callContractResult = rpcClient.callContractOption(address, tokenAddress, hexData);
            if (!TokenOptions.TRUE.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())) {
                return MessageResult.error(1, "只能授权的公司操作");
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

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
        //1,获取协议名称
        String protocolName = rpcClient.getProtocolName(address, tokenAddress);
        //2,获取协议版本号
        String protocolVersion = rpcClient.getProtocolVersion(address, tokenAddress);
        //3,根据单位名称和数据名称到通用智能合约查询单位编号和数据类型编号
        List<String> numsOfCompany = rpcClient.getNumsOfCompany(address, tokenAddress, companyName, dataType);
        if (numsOfCompany == null || numsOfCompany.isEmpty()) {
            return MessageResult.error(1, "没有对应的数据或公司");
        }
        logger.info("***************{}**********", numsOfCompany);
        //3.1获取数据是否加密
        Boolean isEncrypt = false;
        Function fn = new Function(ContractManagerFunction.GETDATAENCRYPTION, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address)), new Utf8String(dataType)), Arrays.asList(new TypeReference<Bool>() {
        }));
        String hexData = FunctionEncoder.encode(fn);
        if (hexData.startsWith("0x")) {
            hexData = hexData.substring(2);
        }
        try {
            CallContractResult callContractResult = rpcClient.callContractOption(address, tokenAddress, hexData);

            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(), fn.getOutputParameters());
                if (list != null && !list.isEmpty()) {
                    isEncrypt = ((Bool) list.get(0)).getValue();
                } else {
                    return MessageResult.error(1, "没有数据");
                }
            } else {
                return MessageResult.error(1, callContractResult.getExecutionResult().getExcepted());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //3.2对数据编号进行组合（1一个字节，首位，1表示加密，0表示不加密 如加密10000001，不加密00000001）
        if (numsOfCompany != null && numsOfCompany.size() == 2) {
            StringBuilder binary = new StringBuilder(BinaryHexUtil.bytes2BinaryStr(BinaryHexUtil.HexStringToBinary(numsOfCompany.get(1))));
            if (isEncrypt) {
                binary.replace(0, 1, "1");
            } else {
                binary.replace(0, 1, "0");
            }
            numsOfCompany.remove(1);
            String nums = Integer.toString(Integer.parseInt(binary.toString(), 2), 16);
            numsOfCompany.add((nums.length() == 1 ? "0" + nums : nums).toLowerCase());
        }
        //4,根据单位和数据类型到通用智能合约查询加密后的密钥（EKey）
        /**
         * 通信秘钥
         */
        String transactionKey = null;
        if (StringUtils.isEmpty(transactionKey) && isEncrypt) {
            fn = new Function(ContractCompanyFunction.COMPANYGETENCRYPTEDKEY, Arrays.asList(new Utf8String(dataType)), Collections.singletonList(new TypeReference<Utf8String>() {
            }));
            hexData = FunctionEncoder.encode(fn);
            if (hexData.startsWith("0x")) {
                hexData = hexData.substring(2);
            }
            try {

                CallContractResult callContractResult = rpcClient.callContractOption(address, tokenAddress, hexData);
                if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                    List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(), fn.getOutputParameters());
                    if (list != null && !list.isEmpty()) {
                        transactionKey = ECCUtil.privateDecrypt(((Utf8String) list.get(0)).getValue(), ECCUtil.string2PrivateKey(dataPrivateKey));
                    } else {
                        return MessageResult.error(1, "没有数据");
                    }
                } else {
                    return MessageResult.error(1, callContractResult.getExecutionResult().getExcepted());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return MessageResult.error(500, "error:" + e.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        //5 数据加密
        if (StringUtils.isEmpty(data)) {
            return MessageResult.error(1, "error:data cannot be null");
        }
        String encryptData = null;
        if (isEncrypt) {
            if (data.getBytes("utf-8").length > 487) {
                return MessageResult.error(1, "数据长度超过最大长度487个字节");
            }
            encryptData = JBlowfish.encrypt(data, transactionKey);
        } else {
            if (data.getBytes("utf-8").length > 987) {
                return MessageResult.error(1, "数据长度超过最大长度987个字节");
            }
            encryptData = data;
        }
        //6 获取数据校验位  获得验证位：对单位编号 + 数据类型编号 + 加密的产品数据进行hash编码，取编码的前1个字节(AA)，放在加密的产品数据后面
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(numsOfCompany.get(0));
        stringBuilder.append(numsOfCompany.get(1));
        stringBuilder.append(encryptData);
        byte[] bytes = new byte[]{Hash.sha256(stringBuilder.toString().getBytes("utf-8"))[0]};
        String verify = BinaryHexUtil.BinaryToHexString(bytes);

        //7 数据组装
        SilkScreenData silkScreenData = SilkScreenData.builder()
                .protocolName(protocolName)
                .protocolVersion(protocolVersion)
                .contractAddress(tokenAddress)
                .companyNum(numsOfCompany.get(0))
                .currentDataTypeNum(numsOfCompany.get(1))
                .dataOfCompany(encryptData)
                .verify(verify)
                .build();
        try {

            String transaction = rpcClient.createRawTransaction(address, SilkScreenDataUtil.packageData(silkScreenData));
            if (StringUtils.isEmpty(transaction)) {
                MessageResult result = new MessageResult(1, "create transaction error");
                return result;
            }
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletpassphrase(rpcClient, password);
            }
            String txid = rpcClient.sendRawTransaction(rpcClient.signRawTransaction(transaction));
            if (StringUtils.isEmpty(txid)) {
                MessageResult result = new MessageResult(1, "send transaction error");
                return result;
            }
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletlock(rpcClient);
            }
            MessageResult result = new MessageResult(0, "success");
            result.setData(txid);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;

    }

    /**
     * 节点数据上链接口
     *
     * @return true
     * @author shenzucai
     * @time 2018.08.04 15:15
     */
    @RequestMapping(value = "nodedeploydata", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiOperation(value = "节点数据上链接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "节点地址", dataType = "String", paramType = "form", defaultValue = "SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9", required = true),
            @ApiImplicitParam(name = "companyName", value = "公司名称", dataType = "String", paramType = "form", defaultValue = "佛系", required = true),
            @ApiImplicitParam(name = "companyAddress", value = "公司地址", dataType = "String", paramType = "form", defaultValue = "SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL", required = true),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "String", paramType = "form", defaultValue = "温度", required = true),
            @ApiImplicitParam(name = "data", value = "上载数据（utf-8明文487个字节,中文字符162个，英文字符487个）", dataType = "String", paramType = "form", defaultValue = "测试数据", required = true),
    })
    public MessageResult nodeDeployData(String address, String companyName, String companyAddress, String dataType, String data) throws UnsupportedEncodingException {

        // 判断该节点是否具有上传数据的权限
        Boolean aBoolean = rpcClient.getAuthOfNodes(address, companyName, dataType, tokenAddress);
        if (!aBoolean) {
            MessageResult result = new MessageResult(1, "该节点没有上载数据的权限");
            return result;
        }
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
        //1,获取协议名称
        String protocolName = rpcClient.getProtocolName(address, tokenAddress);
        //2,获取协议版本号
        String protocolVersion = rpcClient.getProtocolVersion(address, tokenAddress);
        //3,根据单位名称和数据名称到通用智能合约查询单位编号和数据类型编号
        List<String> numsOfCompany = rpcClient.getNumsOfCompany(address, tokenAddress, companyName, dataType);
        //3.1获取数据是否加密
        Boolean isEncrypt = false;
        Function fn = new Function(ContractManagerFunction.GETDATAENCRYPTION, Arrays.asList(new Address(AddressUtil.SLUtoHash160(companyAddress)), new Utf8String(dataType)), Arrays.asList(new TypeReference<Bool>() {
        }));
        String hexData = FunctionEncoder.encode(fn);
        if (hexData.startsWith("0x")) {
            hexData = hexData.substring(2);
        }
        try {
            CallContractResult callContractResult = rpcClient.callContractOption(companyAddress, tokenAddress, hexData);

            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(), fn.getOutputParameters());
                if (list != null && !list.isEmpty()) {
                    isEncrypt = ((Bool) list.get(0)).getValue();
                } else {
                    return MessageResult.error(1, "没有数据");
                }
            } else {
                return MessageResult.error(1, callContractResult.getExecutionResult().getExcepted());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //3.2对数据编号进行组合（1一个字节，首位，1表示加密，0表示不加密 如加密10000001，不加密00000001）
        if (numsOfCompany != null && numsOfCompany.size() == 2) {
            StringBuilder binary = new StringBuilder(BinaryHexUtil.bytes2BinaryStr(BinaryHexUtil.HexStringToBinary(numsOfCompany.get(1))));
            if (isEncrypt) {
                binary.replace(0, 1, "1");
            } else {
                binary.replace(0, 1, "0");
            }
            numsOfCompany.remove(1);
            String nums = Integer.toString(Integer.parseInt(binary.toString(), 2), 16);
            numsOfCompany.add((nums.length() == 1 ? "0" + nums : nums).toLowerCase());
        }
        //4,根据单位和数据类型到通用智能合约查询加密后的密钥（EKey）
        /**
         * 通信秘钥
         */
        String transactionKey = null;
        if (StringUtils.isEmpty(transactionKey) && isEncrypt) {
            fn = new Function(ContractCompanyFunction.NODEGETENCRYPTEDKEY, Arrays.asList(new Utf8String(companyName), new Utf8String(dataType)), Collections.singletonList(new TypeReference<Utf8String>() {
            }));
            hexData = FunctionEncoder.encode(fn);
            if (hexData.startsWith("0x")) {
                hexData = hexData.substring(2);
            }
            try {

                CallContractResult callContractResult = rpcClient.callContractOption(address, tokenAddress, hexData);
                if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                    List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(), fn.getOutputParameters());
                    if (list != null && !list.isEmpty()) {
                        transactionKey = ECCUtil.privateDecrypt(((Utf8String) list.get(0)).getValue(), ECCUtil.string2PrivateKey(dataPrivateKey));
                    } else {
                        return MessageResult.error(1, "没有数据");
                    }
                } else {
                    return MessageResult.error(1, callContractResult.getExecutionResult().getExcepted());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return MessageResult.error(500, "error:" + e.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        //5 数据加密
        if (StringUtils.isEmpty(data)) {
            return MessageResult.error(1, "error:data cannot be null");
        }
        String encryptData = null;
        if (isEncrypt) {
            if (data.getBytes("utf-8").length > 487) {
                return MessageResult.error(1, "数据长度超过最大长度487个字节");
            }
            encryptData = JBlowfish.encrypt(data, transactionKey);
        } else {
            if (data.getBytes("utf-8").length > 987) {
                return MessageResult.error(1, "数据长度超过最大长度987个字节");
            }
            encryptData = data;
        }
        //6 获取数据校验位  获得验证位：对单位编号 + 数据类型编号 + 加密的产品数据进行hash编码，取编码的前1个字节(AA)，放在加密的产品数据后面
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(numsOfCompany.get(0));
        stringBuilder.append(numsOfCompany.get(1));
        stringBuilder.append(encryptData);
        byte[] bytes = new byte[]{Hash.sha256(stringBuilder.toString().getBytes("utf-8"))[0]};
        String verify = BinaryHexUtil.BinaryToHexString(bytes);

        //7 数据组装
        SilkScreenData silkScreenData = SilkScreenData.builder()
                .protocolName(protocolName)
                .protocolVersion(protocolVersion)
                .contractAddress(tokenAddress)
                .companyNum(numsOfCompany.get(0))
                .currentDataTypeNum(numsOfCompany.get(1))
                .dataOfCompany(encryptData)
                .verify(verify)
                .build();
        try {

            String transaction = rpcClient.createRawTransaction(address, SilkScreenDataUtil.packageData(silkScreenData));
            if (StringUtils.isEmpty(transaction)) {
                MessageResult result = new MessageResult(1, "create transaction error");
                return result;
            }
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletpassphrase(rpcClient, password);
            }
            String txid = rpcClient.sendRawTransaction(rpcClient.signRawTransaction(transaction));
            if (StringUtils.isEmpty(txid)) {
                MessageResult result = new MessageResult(1, "send transaction error");
                return result;
            }
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletlock(rpcClient);
            }
            MessageResult result = new MessageResult(0, "success");
            result.setData(txid);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, "error:" + e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;

    }


    /**
     * 解析数据接口
     *
     * @return true
     * @author shenzucai
     * @time 2018.08.04 15:15
     */
    @GetMapping("analysisData")
    @ApiOperation(value = "解析数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address", value = "公司或节点", dataType = "String", paramType = "query", defaultValue = "SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6", required = true),
            @ApiImplicitParam(name = "companyName", value = "公司名称", dataType = "String", paramType = "query", defaultValue = "佛系", required = true),
            @ApiImplicitParam(name = "dataType", value = "数据类型", dataType = "String", paramType = "query", defaultValue = "温度", required = true),
            @ApiImplicitParam(name = "txid", value = "上载数据返回的hash值", dataType = "String", paramType = "query", defaultValue = "2a66a34fbf69070517b1ae5b72d60a612354e637022b76b1a3cad41443c2c704"),
    })
    public MessageResult analysisData(String address, String companyName, String dataType, String txid) throws UnsupportedEncodingException {


        try {
            String data = rpcClient.getRawTransaction(txid);
            if (StringUtils.isEmpty(data)) {
                return MessageResult.error(1, "区块链上没有记录");
            }
            String protocolName = data.substring(0, 20);
            String protocolVersion = data.substring(20, 22);
            String tokenAddress = data.substring(22, 62);
            String numsOfCompany = data.substring(62, 64);
            String numsOfData = data.substring(64, 66);
            String dataOfCompany = data.substring(66, data.length() - 2);
            String verify = data.substring(data.length() - 2);

            // 获取数据校验位  获得验证位：对单位编号 + 数据类型编号 + 加密的产品数据进行hash编码，取编码的前1个字节(AA)，放在加密的产品数据后面
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(numsOfCompany);
            stringBuilder.append(numsOfData);
            stringBuilder.append(dataOfCompany);
            byte[] bytes = new byte[]{Hash.sha256(stringBuilder.toString().getBytes("utf-8"))[0]};
            String checkVerify = BinaryHexUtil.BinaryToHexString(bytes);
            if (verify.equalsIgnoreCase(checkVerify)) {
                String binary = BinaryHexUtil.bytes2BinaryStr(BinaryHexUtil.HexStringToBinary(numsOfData));
                // 判断是否加密，如果是则还原数据编号，和原始数据
                if (binary.startsWith("1")) {
                    numsOfData = Integer.toString(Integer.parseInt(new StringBuffer(binary).replace(0, 1, "0").toString(), 2), 16).toLowerCase();
                    // 判断该地址是否具有读取数据的权限
                    Boolean canRead = false;
                    String transactionKey = null;

                    // 判断该地址是否是授权公司
                    try {
                        Function fn1 = new Function(ContractCompanyFunction.ISAPPROVEDCOMPANY, Arrays.asList(new Address(AddressUtil.SLUtoHash160(address))), Collections.singletonList(new TypeReference<Bool>() {
                        }));
                        String hexData = FunctionEncoder.encode(fn1);
                        if (hexData.startsWith("0x")) {
                            hexData = hexData.substring(2);
                        }
                        // 判断是否是授权的公司
                        CallContractResult callContractResult = rpcClient.callContractOption(address, tokenAddress, hexData);
                        if (!TokenOptions.TRUE.equalsIgnoreCase(callContractResult.getExecutionResult().getOutput())) {
                            canRead = false;
                        } else {
                            canRead = true;
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    // 如果是授权公司，则获取加密的秘钥
                    if (canRead) {
                        Function fn = new Function(ContractCompanyFunction.COMPANYGETENCRYPTEDKEY, Arrays.asList(new Utf8String(dataType)), Collections.singletonList(new TypeReference<Utf8String>() {
                        }));
                        String hexData = FunctionEncoder.encode(fn);
                        if (hexData.startsWith("0x")) {
                            hexData = hexData.substring(2);
                        }
                        try {

                            CallContractResult callContractResult = rpcClient.callContractOption(address, tokenAddress, hexData);
                            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                                List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(), fn.getOutputParameters());
                                if (list != null && !list.isEmpty()) {
                                    transactionKey = ECCUtil.privateDecrypt(((Utf8String) list.get(0)).getValue(), ECCUtil.string2PrivateKey(dataPrivateKey));
                                    canRead = true;
                                } else {
                                    canRead = false;
                                }
                            } else {
                                canRead = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return MessageResult.error(500, "error:" + e.getMessage());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    // 如果canRead = false 则尝试使用节点身份获取加密的秘钥

                    if (!canRead) {
                        Function fn = new Function(ContractCompanyFunction.NODEGETENCRYPTEDKEY, Arrays.asList(new Utf8String(companyName), new Utf8String(dataType)), Collections.singletonList(new TypeReference<Utf8String>() {
                        }));
                        String hexData = FunctionEncoder.encode(fn);
                        if (hexData.startsWith("0x")) {
                            hexData = hexData.substring(2);
                        }
                        try {

                            CallContractResult callContractResult = rpcClient.callContractOption(address, tokenAddress, hexData);
                            if ("None".equalsIgnoreCase(callContractResult.getExecutionResult().getExcepted())) {
                                List list = FunctionReturnDecoder.decode(callContractResult.getExecutionResult().getOutput(), fn.getOutputParameters());
                                if (list != null && !list.isEmpty()) {
                                    transactionKey = ECCUtil.privateDecrypt(((Utf8String) list.get(0)).getValue(), ECCUtil.string2PrivateKey(dataPrivateKey));
                                    canRead = true;
                                } else {
                                    canRead = false;
                                }
                            } else {
                                canRead = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return MessageResult.error(500, "error:" + e.getMessage());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    if (canRead) {
                        dataOfCompany = JBlowfish.dncrypt(dataOfCompany, transactionKey);
                    }
                }

            }
            //numsOfCompany = new BigInteger(numsOfCompany, 16).toString();
            SilkScreenData silkScreenData = SilkScreenData.builder()
                    .protocolName(StringHexUtil.toNormalString(protocolName))
                    .protocolVersion(protocolVersion)
                    .contractAddress(tokenAddress)
                    .companyNum(numsOfCompany)
                    .currentDataTypeNum(numsOfData)
                    .dataOfCompany(StringHexUtil.toNormalString(dataOfCompany))
                    .verify(verify).build();

            MessageResult result = new MessageResult(0, "success");
            result.setData(silkScreenData);
            return result;

        } catch (Exception e) {
            return MessageResult.error(500, "非法数据无法解析：" + e.getMessage());
        } catch (Throwable throwable) {
            return MessageResult.error(500, "非法数据无法解析:" + throwable.getMessage());
        }
    }


    /**
     * 部署合约
     *
     * @param fromAddress
     * @param data
     * @return true
     * @author shenzucai
     * @time 2018.08.15 10:20
     */
    @RequestMapping(value = "deploy/contract", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiOperation(value = "部署合约", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址(拥有者)", dataType = "String", paramType = "form", defaultValue = "SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm"),
            @ApiImplicitParam(name = "data", value = "合约代码", dataType = "String", paramType = "form", defaultValue = "60806040526040805190810160405280600a81526020017f53696c6b73637265656e0000000000000000000000000000000000000000000081525060019080519060200190620000519291906200012a565b5060017f010000000000000000000000000000000000000000000000000000000000000002600260006101000a81548160ff02191690837f01000000000000000000000000000000000000000000000000000000000000009004021790555060006009556000600f60006101000a81548160ff021916908360ff1602179055506000601555348015620000e357600080fd5b50336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550620001d9565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200016d57805160ff19168380011785556200019e565b828001600101855582156200019e579182015b828111156200019d57825182559160200191906001019062000180565b5b509050620001ad9190620001b1565b5090565b620001d691905b80821115620001d2576000816000905550600101620001b8565b5090565b90565b615fb980620001e96000396000f3006080604052600436106101b7576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806314605cc4146101bc57806317f1e926146102175780631d5fa729146102fe5780631dc715dd146104265780631fe41d0f14610533578063260a7c17146105a0578063274aab151461068757806329d8b242146107285780632ae9c600146107595780632d06177a146107c85780632d5a7604146108235780632dd80e5914610905578063392b8913146109a65780633c71fce6146109fd5780633cf079f014610b45578063412456c814610ba0578063449660cf14610c4157806352e98e4f14610d6e578063782ef2c514610d995780637ccd7f0114610df45780638735d03614610eca5780638da5cb5b14610f3757806397f49fdf14610f8e578063a788a87f14611122578063a7965c7a14611221578063a7ed345314611308578063a9c606931461145b578063ac18de431461155d578063c562d1c8146115b8578063c6f0f2a814611625578063d6a16afa14611650578063de5df800146116f9578063e567e869146117cc578063e98296c31461185c578063fa80a2ef14611943575b600080fd5b3480156101c857600080fd5b506101fd600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061199a565b604051808215151515815260200191505060405180910390f35b34801561022357600080fd5b506102e4600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506119ba565b604051808215151515815260200191505060405180910390f35b34801561030a57600080fd5b506103ab600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050611f86565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156103eb5780820151818401526020810190506103d0565b50505050905090810190601f1680156104185780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561043257600080fd5b50610519600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050612270565b604051808215151515815260200191505060405180910390f35b34801561053f57600080fd5b5061055e60048036038101908080359060200190929190505050612625565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b3480156105ac57600080fd5b5061066d600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506126d3565b604051808215151515815260200191505060405180910390f35b34801561069357600080fd5b5061070e600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050612984565b604051808215151515815260200191505060405180910390f35b34801561073457600080fd5b5061073d612a4a565b604051808260ff1660ff16815260200191505060405180910390f35b34801561076557600080fd5b5061076e612a5d565b60405180827effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200191505060405180910390f35b3480156107d457600080fd5b50610809600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050612a8f565b604051808215151515815260200191505060405180910390f35b34801561082f57600080fd5b5061088a600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050612c89565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156108ca5780820151818401526020810190506108af565b50505050905090810190601f1680156108f75780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561091157600080fd5b5061098c600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050612e33565b604051808215151515815260200191505060405180910390f35b3480156109b257600080fd5b506109e7600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050612fc1565b6040518082815260200191505060405180910390f35b348015610a0957600080fd5b50610aca600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050612fd9565b6040518080602001828103825283818151815260200191508051906020019080838360005b83811015610b0a578082015181840152602081019050610aef565b50505050905090810190601f168015610b375780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b348015610b5157600080fd5b50610b86600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506131e9565b604051808215151515815260200191505060405180910390f35b348015610bac57600080fd5b50610c27600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050613445565b604051808215151515815260200191505060405180910390f35b348015610c4d57600080fd5b50610d54600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929050505061382a565b604051808215151515815260200191505060405180910390f35b348015610d7a57600080fd5b50610d83613c38565b6040518082815260200191505060405180910390f35b348015610da557600080fd5b50610dda600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050613c3e565b604051808215151515815260200191505060405180910390f35b348015610e0057600080fd5b50610ea1600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050613c5e565b604051808360ff1660ff1681526020018260ff1660ff1681526020019250505060405180910390f35b348015610ed657600080fd5b50610ef560048036038101908080359060200190929190505050614066565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b348015610f4357600080fd5b50610f4c614114565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b348015610f9a57600080fd5b5061103b600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050614139565b604051808060200180602001838103835285818151815260200191508051906020019080838360005b8381101561107f578082015181840152602081019050611064565b50505050905090810190601f1680156110ac5780820380516001836020036101000a031916815260200191505b50838103825284818151815260200191508051906020019080838360005b838110156110e55780820151818401526020810190506110ca565b50505050905090810190601f1680156111125780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b34801561112e57600080fd5b50611207600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192908035151590602001909291908035151590602001909291905050506143f5565b604051808215151515815260200191505060405180910390f35b34801561122d57600080fd5b506112ee600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050614775565b604051808215151515815260200191505060405180910390f35b34801561131457600080fd5b50611441600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050614fcb565b604051808215151515815260200191505060405180910390f35b34801561146757600080fd5b506114e2600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050615322565b6040518080602001828103825283818151815260200191508051906020019080838360005b83811015611522578082015181840152602081019050611507565b50505050905090810190601f16801561154f5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561156957600080fd5b5061159e600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506154cb565b604051808215151515815260200191505060405180910390f35b3480156115c457600080fd5b506115e360048036038101908080359060200190929190505050615668565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561163157600080fd5b5061163a615716565b6040518082815260200191505060405180910390f35b34801561165c57600080fd5b506116b7600480360381019080803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929050505061571c565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561170557600080fd5b506117b2600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803515159060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050615809565b604051808215151515815260200191505060405180910390f35b3480156117d857600080fd5b506117e1615c23565b6040518080602001828103825283818151815260200191508051906020019080838360005b83811015611821578082015181840152602081019050611806565b50505050905090810190601f16801561184e5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561186857600080fd5b50611929600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050615cc1565b604051808215151515815260200191505060405180910390f35b34801561194f57600080fd5b50611984600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050615ed0565b6040518082815260200191505060405180910390f35b60046020528060005260406000206000915054906101000a900460ff1681565b6000838383601160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020826040518082805190602001908083835b602083101515611a345780518252602082019150602081019050602083039250611a0f565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020816040518082805190602001908083835b602083101515611a9d5780518252602082019150602081019050602083039250611a78565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160039054906101000a900460ff161515611aec57600080fd5b600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1680611d245750600c866040518082805190602001908083835b602083101515611b775780518252602082019150602081019050602083039250611b52565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16148015611d235750601160008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020866040518082805190602001908083835b602083101515611c755780518252602082019150602081019050602083039250611c50565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020856040518082805190602001908083835b602083101515611cde5780518252602082019150602081019050602083039250611cb9565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160009054906101000a900460ff165b5b15611f78576000601160008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020876040518082805190602001908083835b602083101515611da05780518252602082019150602081019050602083039250611d7b565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020866040518082805190602001908083835b602083101515611e095780518252602082019150602081019050602083039250611de4565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160036101000a81548160ff0219169083151502179055506001601660008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825403925050819055506000601660008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020541415611f775760016015540360158190555060136001601460008a73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205403815481101515611f4957fe5b9060005260206000200160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690555b5b600193505050509392505050565b6060338383601160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020826040518082805190602001908083835b6020831015156120005780518252602082019150602081019050602083039250611fdb565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020816040518082805190602001908083835b6020831015156120695780518252602082019150602081019050602083039250612044565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160039054906101000a900460ff1615156120b857600080fd5b601160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020866040518082805190602001908083835b60208310151561212d5780518252602082019150602081019050602083039250612108565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020856040518082805190602001908083835b6020831015156121965780518252602082019150602081019050602083039250612171565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206002018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156122605780601f1061223557610100808354040283529160200191612260565b820191906000526020600020905b81548152906001019060200180831161224357829003601f168201915b5050505050935050505092915050565b6000338484601160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020826040518082805190602001908083835b6020831015156122ea57805182526020820191506020810190506020830392506122c5565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020816040518082805190602001908083835b602083101515612353578051825260208201915060208101905060208303925061232e565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160039054906101000a900460ff1615156123a257600080fd5b60056000600c896040518082805190602001908083835b6020831015156123de57805182526020820191506020810190506020830392506123b9565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020866040518082805190602001908083835b6020831015156124a2578051825260208201915060208101905060208303925061247d565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900460ff1615156124ee57600080fd5b84601160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020886040518082805190602001908083835b602083101515612564578051825260208201915060208101905060208303925061253f565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020876040518082805190602001908083835b6020831015156125cd57805182526020820191506020810190506020830392506125a8565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206001019080519060200190612616929190615ee8565b50600193505050509392505050565b6000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561267f57600080fd5b6013805490508210151561269257600080fd5b6013828154811015156126a157fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050919050565b600080600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561272e57600080fd5b84600b60008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561278757600080fd5b600560008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020856040518082805190602001908083835b6020831015156127fc57805182526020820191506020810190506020830392506127d7565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900460ff16151561284857600080fd5b600a60008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002091508382600201866040518082805190602001908083835b6020831015156128c4578051825260208201915060208101905060208303925061289f565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020600201908051906020019061290d929190615ee8565b50600e60008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff168260010160006101000a81548160ff021916908360ff1602179055506001925050509392505050565b6000600a60008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600201826040518082805190602001908083835b6020831015156129fe57805182526020820191506020810190506020830392506129d9565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160019054906101000a900460ff16905092915050565b600f60009054906101000a900460ff1681565b600260009054906101000a90047f01000000000000000000000000000000000000000000000000000000000000000281565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515612aec57600080fd5b8160008173ffffffffffffffffffffffffffffffffffffffff1614151515612b1357600080fd5b600460008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151515612b6c57600080fd5b6001600460008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550600780549050600860008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555060078390806001815401808255809150509060018203906000526020600020016000909192909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550506001600954016009819055506001915050919050565b606033600b60008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515612ce457600080fd5b600a60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600201836040518082805190602001908083835b602083101515612d5c5780518252602082019150602081019050602083039250612d37565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206002018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015612e265780601f10612dfb57610100808354040283529160200191612e26565b820191906000526020600020905b815481529060010190602001808311612e0957829003601f168201915b5050505050915050919050565b6000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515612e8d57600080fd5b8160008173ffffffffffffffffffffffffffffffffffffffff1614151515612eb457600080fd5b82600c856040518082805190602001908083835b602083101515612eed5780518252602082019150602081019050602083039250612ec8565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600660008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908315150217905550600191505092915050565b60166020528060005260406000206000915090505481565b6060600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561303357600080fd5b601160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020836040518082805190602001908083835b6020831015156130a85780518252602082019150602081019050602083039250613083565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020826040518082805190602001908083835b60208310151561311157805182526020820191506020810190506020830392506130ec565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206001018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156131db5780601f106131b0576101008083540402835291602001916131db565b820191906000526020600020905b8154815290600101906020018083116131be57829003601f168201915b505050505090509392505050565b6000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561324357600080fd5b600660008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561329b57600080fd5b600b60008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff161515156132f457600080fd5b6001600b60008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055506001600f60009054906101000a900460ff1601600f60006101000a81548160ff021916908360ff160217905550600d80549050600e60008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff021916908360ff160217905550600d8290806001815401808255809150509060018203906000526020600020016000909192909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505060019050919050565b6000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561349f57600080fd5b600b60008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1615156134f757600080fd5b600560008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020826040518082805190602001908083835b60208310151561356c5780518252602082019150602081019050602083039250613547565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900460ff1615156135b857600080fd5b6000600560008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020836040518082805190602001908083835b60208310151561362f578051825260208201915060208101905060208303925061360a565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060006101000a81548160ff0219169083151502179055506001601060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825403925050819055506000601060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020541415613820576000600b60008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055506001600f60009054906101000a900460ff1603600f60006101000a81548160ff021916908360ff160217905550600d600e60008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1660ff168154811015156137f257fe5b9060005260206000200160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690555b6001905092915050565b6000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561388457600080fd5b848484601160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020826040518082805190602001908083835b6020831015156138fc57805182526020820191506020810190506020830392506138d7565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020816040518082805190602001908083835b6020831015156139655780518252602082019150602081019050602083039250613940565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160039054906101000a900460ff1615156139b457600080fd5b60056000600c896040518082805190602001908083835b6020831015156139f057805182526020820191506020810190506020830392506139cb565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020866040518082805190602001908083835b602083101515613ab45780518252602082019150602081019050602083039250613a8f565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900460ff161515613b0057600080fd5b84601160008a73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020886040518082805190602001908083835b602083101515613b765780518252602082019150602081019050602083039250613b51565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020876040518082805190602001908083835b602083101515613bdf5780518252602082019150602081019050602083039250613bba565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206002019080519060200190613c28929190615ee8565b5060019350505050949350505050565b60155481565b600b6020528060005260406000206000915054906101000a900460ff1681565b600080600080600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1680613d055750600b60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff165b80613e2e5750601160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020866040518082805190602001908083835b602083101515613d805780518252602082019150602081019050602083039250613d5b565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020856040518082805190602001908083835b602083101515613de95780518252602082019150602081019050602083039250613dc4565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160039054906101000a900460ff165b1515613e3957600080fd5b600c866040518082805190602001908083835b602083101515613e715780518252602082019150602081019050602083039250613e4c565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169150600560008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020856040518082805190602001908083835b602083101515613f3c5780518252602082019150602081019050602083039250613f17565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900460ff161515613f8857600080fd5b600a60008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002090508060010160009054906101000a900460ff1681600201866040518082805190602001908083835b6020831015156140155780518252602082019150602081019050602083039250613ff0565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160009054906101000a900460ff169350935050509250929050565b6000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1615156140c057600080fd5b600780549050821015156140d357600080fd5b6007828154811015156140e257fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050919050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6060806000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561419657600080fd5b600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020856040518082805190602001908083835b60208310151561420b57805182526020820191506020810190506020830392506141e6565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020846040518082805190602001908083835b602083101515614274578051825260208201915060208101905060208303925061424f565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902090508060000181600101818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156143465780601f1061431b57610100808354040283529160200191614346565b820191906000526020600020905b81548152906001019060200180831161432957829003601f168201915b50505050509150808054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156143e25780601f106143b7576101008083540402835291602001916143e2565b820191906000526020600020905b8154815290600101906020018083116143c557829003601f168201915b5050505050905092509250509250929050565b600080600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561445057600080fd5b8660008173ffffffffffffffffffffffffffffffffffffffff161415151561447757600080fd5b60056000600c896040518082805190602001908083835b6020831015156144b3578051825260208201915060208101905060208303925061448e565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020866040518082805190602001908083835b6020831015156145775780518252602082019150602081019050602083039250614552565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900460ff1615156145c357600080fd5b601160008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020876040518082805190602001908083835b6020831015156146385780518252602082019150602081019050602083039250614613565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020866040518082805190602001908083835b6020831015156146a1578051825260208201915060208101905060208303925061467c565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390209150848260000160006101000a81548160ff021916908315150217905550838260000160046101000a81548160ff0219169083151502179055506001601260008a73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff02191690831515021790555060019250505095945050505050565b60008060056000600c866040518082805190602001908083835b6020831015156147b4578051825260208201915060208101905060208303925061478f565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020836040518082805190602001908083835b6020831015156148785780518252602082019150602081019050602083039250614853565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900460ff1615156148c457600080fd5b601260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561491c57600080fd5b600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1680614a2a5750600c846040518082805190602001908083835b6020831015156149a75780518252602082019150602081019050602083039250614982565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16145b1515614a3557600080fd5b601160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020846040518082805190602001908083835b602083101515614aaa5780518252602082019150602081019050602083039250614a85565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020836040518082805190602001908083835b602083101515614b135780518252602082019150602081019050602083039250614aee565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902090508060000160039054906101000a900460ff16151515614b6657600080fd5b600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1615614bd65760018160000160026101000a81548160ff0219169083151502179055505b8060000160009054906101000a900460ff168015614caa5750600c846040518082805190602001908083835b602083101515614c275780518252602082019150602081019050602083039250614c02565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16145b15614ccd5760018160000160016101000a81548160ff0219169083151502179055505b8060000160029054906101000a900460ff161580614d1457508060000160009054906101000a900460ff168015614d1357508060000160019054906101000a900460ff16155b5b15614d3f5760008160000160036101000a81548160ff02191690831515021790555060009150614fc3565b6001601160008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020856040518082805190602001908083835b602083101515614db65780518252602082019150602081019050602083039250614d91565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020846040518082805190602001908083835b602083101515614e1f5780518252602082019150602081019050602083039250614dfa565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160036101000a81548160ff0219169083151502179055506001601660008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055506000601460008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020541415614fbe5760138590806001815401808255809150509060018203906000526020600020016000909192909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050600160155401601581905550601380549050601460008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055505b600191505b509392505050565b6000806000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561502857600080fd5b600c876040518082805190602001908083835b602083101515615060578051825260208201915060208101905060208303925061503b565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169150600b60008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561510e57600080fd5b600360003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020876040518082805190602001908083835b602083101515615183578051825260208201915060208101905060208303925061515e565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020866040518082805190602001908083835b6020831015156151ec57805182526020820191506020810190506020830392506151c7565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020905084816000019080519060200190615239929190615ee8565b5083816001019080519060200190615252929190615ee8565b506001600560008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020876040518082805190602001908083835b6020831015156152ca57805182526020820191506020810190506020830392506152a5565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060006101000a81548160ff021916908315150217905550600192505050949350505050565b6060600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561537c57600080fd5b600a60008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600201826040518082805190602001908083835b6020831015156153f457805182526020820191506020810190506020830392506153cf565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206001018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156154be5780601f10615493576101008083540402835291602001916154be565b820191906000526020600020905b8154815290600101906020018083116154a157829003601f168201915b5050505050905092915050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561552857600080fd5b600460008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561558057600080fd5b6000600460008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055506007600860008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205481548110151561562657fe5b9060005260206000200160006101000a81549073ffffffffffffffffffffffffffffffffffffffff021916905560016009540360098190555060019050919050565b6000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1615156156c257600080fd5b600d80549050821015156156d557600080fd5b600d828154811015156156e457fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050919050565b60095481565b6000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561577657600080fd5b600c826040518082805190602001908083835b6020831015156157ae5780518252602082019150602081019050602083039250615789565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050919050565b60008033600b60008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16151561586557600080fd5b600560003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020866040518082805190602001908083835b6020831015156158da57805182526020820191506020810190506020830392506158b5565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060009054906101000a900460ff16151561592657600080fd5b600a60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002091508482600201876040518082805190602001908083835b6020831015156159a2578051825260208201915060208101905060208303925061597d565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160016101000a81548160ff0219169083151502179055508382600201876040518082805190602001908083835b602083101515615a2a5780518252602082019150602081019050602083039250615a05565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206001019080519060200190615a73929190615ee8565b50600082600201876040518082805190602001908083835b602083101515615ab05780518252602082019150602081019050602083039250615a8b565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160009054906101000a900460ff1660ff161415615c16576001601060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254019250508190555060018260010160018282829054906101000a900460ff160192506101000a81548160ff021916908360ff1602179055508160010160019054906101000a900460ff1682600201876040518082805190602001908083835b602083101515615bc75780518252602082019150602081019050602083039250615ba2565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160006101000a81548160ff021916908360ff1602179055505b6001925050509392505050565b60018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015615cb95780601f10615c8e57610100808354040283529160200191615cb9565b820191906000526020600020905b815481529060010190602001808311615c9c57829003601f168201915b505050505081565b6000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1680615d645750600b60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff165b80615d9a57508373ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16145b1515615da557600080fd5b601160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020836040518082805190602001908083835b602083101515615e1a5780518252602082019150602081019050602083039250615df5565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020826040518082805190602001908083835b602083101515615e835780518252602082019150602081019050602083039250615e5e565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060000160049054906101000a900460ff1690509392505050565b60106020528060005260406000206000915090505481565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10615f2957805160ff1916838001178555615f57565b82800160010185558215615f57579182015b82811115615f56578251825591602001919060010190615f3b565b5b509050615f649190615f68565b5090565b615f8a91905b80821115615f86576000816000905550600101615f6e565b5090565b905600a165627a7a723058209270644cb9d72576e98c965f462cc227746e25467b8b892e2ec1da57d66ab1a60029")})
    public MessageResult createContract(String fromAddress, String data) {
        logger.info("option:fromAddress={},data = {}", fromAddress, data);
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
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletpassphrase(rpcClient, password);
            }
            DeployContractResult deployContractResult = rpcClient.createContract(fromAddress, data);
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletlock(rpcClient);
            }
            MessageResult messageResult = MessageResult.success("success");
            messageResult.setData(deployContractResult);
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
     * 转账
     *
     * @param fromAddress
     * @param toAddress
     * @param amount
     * @return true
     * @author shenzucai
     * @time 2018.08.18 11:15
     */
    @GetMapping(value = "transfer")
    @ApiOperation(value = "转账")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromAddress", value = "发送地址", dataType = "String", paramType = "query", defaultValue = "SLSjc1JSj9oqYkq7fdUFZaGeG8uisYVRihbm"),
            @ApiImplicitParam(name = "toAddress", value = "接收地址", dataType = "String", paramType = "query", defaultValue = "SLSjkdAGPePxS6Hr4iwnBewjcqurNLFYC8Pe"),
            @ApiImplicitParam(name = "amount", value = "金额", dataType = "double", paramType = "query", defaultValue = "0.5")})
    public MessageResult transfer(String fromAddress, String toAddress, double amount) {
        logger.info("option:fromAddress={},toAddress = {},amount = {}", fromAddress, toAddress, amount);
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

            String transaction = rpcClient.createRawTransaction(fromAddress, toAddress, amount);
            if (StringUtils.isEmpty(transaction)) {
                MessageResult result = new MessageResult(1, "create transaction error");
                return result;
            }
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletpassphrase(rpcClient, password);
            }
            String txid = rpcClient.sendRawTransaction(rpcClient.signRawTransaction(transaction));
            if (StringUtils.isEmpty(txid)) {
                MessageResult result = new MessageResult(1, "send transaction error");
                return result;
            }
            if (!StringUtils.isEmpty(password)) {
                WalletOperationUtil.walletlock(rpcClient);
            }
            MessageResult result = new MessageResult(0, "success");
            result.setData(txid);
            return result;
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
