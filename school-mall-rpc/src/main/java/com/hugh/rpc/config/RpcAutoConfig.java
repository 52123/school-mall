package com.hugh.rpc.config;

import com.hugh.rpc.client.RpcClient;
import com.hugh.rpc.client.RpcProxy;
import com.hugh.rpc.client.ServiceDiscover;
import com.hugh.rpc.server.NettyServer;
import com.hugh.rpc.server.ServiceRegistry;
import com.hugh.rpc.utils.CuratorClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 52123
 * @since 2019/6/23 21:59
 */
@Configuration
@ConditionalOnClass(value = {NettyServer.class, RpcClient.class})
@EnableConfigurationProperties({RpcServiceProperties.class, RpcClientProperties.class})
public class RpcAutoConfig {

    private final RpcServiceProperties serviceProperties;

    private final RpcClientProperties clientProperties;

    @Autowired
    public RpcAutoConfig(RpcServiceProperties serviceProperties, RpcClientProperties clientProperties) {
        this.serviceProperties = serviceProperties;
        this.clientProperties = clientProperties;
    }

    @Bean
    public NettyServer nettyServer() {
        if(!serviceProperties.isEnabled()){
            return null;
        }
        String serviceAddress = serviceProperties.getServiceAddress();
        int servicePort = serviceProperties.getServicePort();
        String serviceName = serviceProperties.getServiceName();
        String zooKeeperAddress = serviceProperties.getZooKeeperAddress();
        String rootPath = serviceProperties.getRootPath();

        if(StringUtils.isBlank(zooKeeperAddress) || StringUtils.isBlank(serviceAddress)
                || StringUtils.isBlank(serviceName) || StringUtils.isBlank(rootPath)){
            throw new NullPointerException("Incomplete parameters: must have zooKeeperAddress, " +
                    "serviceAddress, serviceName and rootPath");
        }
        CuratorFramework curator = new CuratorClient(zooKeeperAddress).client();

       ServiceRegistry serviceRegistry = new ServiceRegistry(curator, rootPath, serviceName,
               serviceAddress, servicePort);
       return new NettyServer(serviceRegistry);
    }

    @Bean
    public RpcProxy rpcProxy(){
        if(!clientProperties.isEnabled()){
            return null;
        }
        String serviceName = clientProperties.getServiceName();
        String zooKeeperAddress = clientProperties.getZooKeeperAddress();
        String rootPath = clientProperties.getRootPath();
        if(StringUtils.isBlank(zooKeeperAddress) || StringUtils.isBlank(serviceName)
            || StringUtils.isBlank(rootPath)){
            throw new NullPointerException("Incomplete parameters: must have " +
                    "zooKeeperAddress, rootPath and serviceName");
        }
        CuratorFramework curator = new CuratorClient(zooKeeperAddress).client();
        ServiceDiscover discover = new ServiceDiscover(curator, rootPath, serviceName);
        return new RpcProxy(discover);
    }
}
