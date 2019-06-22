package com.hugh.rpc.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author 52123
 * @since 2019/6/19 19:55
 *  服务注册--在ZooKeeper上注册服务地址
 */
@Slf4j
@Component
public class CuratorClient {

    /**
     * 会话超时时间(ms)，超过这个时间仍未连接上
     * ZooKeeper，则这个session视为过期
     */
    @Value("${zookeeper.sessionTimeout}")
    private int sessionTimeout;

    /**
     *  最大重试次数
     */
    @Value("${zookeeper.maxRetries}")
    private int maxRetries = 3;

    /**
     *  时间时隔，随着重试次数的增大而增大
     */
    @Value("${zookeeper.baseSleepTimeMS}")
    private int baseSleepTimeMS = 1000;

    /**
     * ZooKeeper地址
     */
    @Value("${zookeeper.zooKeeperServer}")
    private String zooKeeperServer;

    @Bean
    public CuratorFramework client() {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMS, maxRetries);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zooKeeperServer).sessionTimeoutMs(sessionTimeout)
                .retryPolicy(retryPolicy).build();
        client.start();
        return client;
    }
}
