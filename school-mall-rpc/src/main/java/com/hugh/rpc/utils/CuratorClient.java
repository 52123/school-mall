package com.hugh.rpc.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author 52123
 * @since 2019/6/19 19:55
 * 服务注册--在ZooKeeper上注册服务地址
 */
@Slf4j
public class CuratorClient {

    /**
     * 会话超时时间(ms)，超过这个时间仍未连接上
     * ZooKeeper，则这个session视为过期
     */
    private int sessionTimeout;

    /**
     * 最大重试次数
     */
    private int maxRetries;

    /**
     * 时间时隔，随着重试次数的增大而增大
     */
    private int baseSleepTimeMS;

    /**
     * ZooKeeper地址
     */
    private String zooKeeperServer;

    public CuratorClient(int sessionTimeout, int maxRetries, int baseSleepTimeMS, String zooKeeperServer) {
        this.sessionTimeout = sessionTimeout;
        this.maxRetries = maxRetries;
        this.baseSleepTimeMS = baseSleepTimeMS;
        this.zooKeeperServer = zooKeeperServer;
    }

    public CuratorClient(String zooKeeperServer) {
        this(5000, 3, 1000, zooKeeperServer);
    }

    public CuratorFramework client() {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMS, maxRetries);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zooKeeperServer).sessionTimeoutMs(sessionTimeout)
                .retryPolicy(retryPolicy).build();
        client.start();
        return client;
    }
}
