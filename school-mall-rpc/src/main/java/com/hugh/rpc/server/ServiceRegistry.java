package com.hugh.rpc.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import org.apache.zookeeper.CreateMode;


/**
 * @author 52123
 * @since 2019/6/19 22:31
 * RPC服务往ZooKeeper上注册，/rootPath/service/RPCServiceName/IP:PORT
 * RPC客户端以RPCServerName在ZooKeeper上找到对应的服务端地址
 */
@Slf4j
public class ServiceRegistry {

    private final CuratorFramework client;

    private String rootPath;

    private String serviceAddress;

    private int servicePort;

    private String serviceName;

    public ServiceRegistry(CuratorFramework client, String rootPath, String serviceName,
                           String serviceAddress, int servicePort) {
        this.client = client;
        this.rootPath = rootPath;
        this.serviceName = serviceName;
        this.serviceAddress = serviceAddress;
        this.servicePort = servicePort;
    }

    /**
     * PERSISTENT--持久型节点
     * 该节点永久保存，只有delete的时候才能删除节点
     * <p>
     * PERSISTENT_SEQUENTIAL--持久顺序型节点
     * 按注册顺序编号排列的永久节点
     * <p>
     * EPHEMERAL--临时型节点  这里采用的是临时节点
     * 与ZooKeeper断开连接后自动删除
     * <p>
     * EPHEMERAL_SEQUENTIAL--临时顺序型节点
     * 按注册顺序编号的临时节点，可用于实现分布式锁
     * <p>
     * serviceName 服务端在ZooKeeper上注册的服务名，这里还在后面加上了地址
     */
    public void registerService() {
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                    .forPath(rootPath + "/" + serviceName + "/" + serviceAddress + ":" + servicePort);
        } catch (Exception e) {
            log.error("注册失败", e);
        }
    }

    public String getRootPath() {
        return rootPath;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public int getServicePort() {
        return servicePort;
    }

    public String getServiceName() {
        return serviceName;
    }
}
