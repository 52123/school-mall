package com.hugh.rpc.config;

import com.hugh.rpc.server.NettyServer;
import com.hugh.rpc.server.ServiceRegistry;
import com.hugh.rpc.utils.CuratorClient;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author 52123
 * @since 2019/6/23 21:36
 */
@Data
public class RpcServiceBuilder implements FactoryBean<Object>, ApplicationContextAware {

    private String serviceName;

    private String serviceAddress;

    private int servicePort;

    private String zooKeeperAddress;

    private String rootPath;

    private Class<?> serverType;

    private NettyServer nettyServer;

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public Object getObject(){
        return nettyServer;
    }

    @Override
    public Class<?> getObjectType() {
        return serverType;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (StringUtils.isBlank(zooKeeperAddress) || StringUtils.isBlank(serviceAddress)
                || StringUtils.isBlank(serviceName) || StringUtils.isBlank(rootPath)) {
            throw new NullPointerException("Incomplete parameters or illegal argument: please check zooKeeperAddress, " +
                    "serviceAddress, serviceName and rootPath");
        }

        CuratorFramework curator = new CuratorClient(zooKeeperAddress).client();
        ServiceRegistry serviceRegistry = new ServiceRegistry(curator, rootPath, serviceName,
                serviceAddress, servicePort);
        NettyServer nettyServer = new NettyServer(serviceRegistry);
        nettyServer.initService(applicationContext);
        nettyServer.startUpService();
        this.nettyServer = nettyServer;
        this.serverType = nettyServer.getClass();
    }
}
