package com.hugh.rpc.config;

import com.hugh.rpc.client.RpcProxy;
import com.hugh.rpc.client.ServiceDiscover;
import com.hugh.rpc.utils.CuratorClient;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author 52123
 * @since 2019/6/23 21:55
 */
@Data
public class RpcClientProxy implements FactoryBean<Object>, InitializingBean {

    private String serviceName;

    private String zooKeeperAddress;

    private String rootPath;

    private Class<?> serviceInterface;

    private String serviceInterfaceName;

    private Object service;

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Object getObject(){
        return this.service;
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    /**
     * 所有属性初始化完成后启动
     * 在init方法前完成
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if(StringUtils.isBlank(zooKeeperAddress) || StringUtils.isBlank(serviceName)
                || StringUtils.isBlank(rootPath)){
            throw new NullPointerException("Incomplete parameters or illegal argument: please check " +
                    "zooKeeperAddress, rootPath and serviceName");
        }

        if(serviceInterface == null){
            throw new ClassNotFoundException("ClassNotFoundException: can not find class :" + serviceInterfaceName);
        }

        CuratorFramework curator = new CuratorClient(zooKeeperAddress).client();
        ServiceDiscover discover = new ServiceDiscover(curator, rootPath, serviceName);
        this.service = new RpcProxy(discover).create(serviceInterface);
    }

}
