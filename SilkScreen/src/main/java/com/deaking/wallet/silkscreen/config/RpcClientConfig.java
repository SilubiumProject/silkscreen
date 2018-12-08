package com.deaking.wallet.silkscreen.config;


import com.deaking.wallet.core.rpcclient.BitcoinRPCClient;
import com.deaking.wallet.core.util.TimeOutFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 初始化RPC客户端
 */
@Configuration
public class RpcClientConfig {
    private Logger logger = LoggerFactory.getLogger(RpcClientConfig.class);

    @Bean
    public JsonrpcClient setClient(@Value("${coin.rpc}") String uri){
        try {
            logger.info("uri={}",uri);
            URL url = new URL(uri);
            JsonrpcClient client =  new JsonrpcClient(url);
            client.setConnectionTimeoutMillis(TimeOutFinal.MILS);
            client.setReadTimeoutMillis(TimeOutFinal.MILS);
            return client;
        } catch (MalformedURLException e) {
            logger.info("init wallet failed");
            e.printStackTrace();
            return null;
        }
    }

    @Bean
    public BitcoinRPCClient bitcoinRpcClient(@Value("${coin.rpc}") String uri){
        try {
            logger.info("uri={}",uri);
            URL url = new URL(uri);
            BitcoinRPCClient client =  new BitcoinRPCClient(url);
            return client;
        } catch (MalformedURLException e) {
            logger.info("init wallet failed");
            e.printStackTrace();
            return null;
        }
    }
}
