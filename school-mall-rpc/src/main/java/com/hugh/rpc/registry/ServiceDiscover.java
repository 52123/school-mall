package com.hugh.rpc.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 52123
 * @since 2019/6/19 23:59
 * 服务发现-- 用于RPC客户端寻找RPC服务端
 */
@Slf4j
@Component
public class ServiceDiscover {

    private final CuratorFramework client;

    @Value("${zookeeper.rootPath}")
    private String rootPath;

    private final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Autowired
    public ServiceDiscover(CuratorFramework client) {
        this.client = client;
    }

    public String getServiceByName(String name) throws Exception {
        List<String> list = client.getChildren().forPath(rootPath + "/" + name);
        int listSize = list.size();
        if (listSize == 0) {
            throw new Exception("未能找到指定服务");
        }
        /*
         * 若服务有多个地址，实现负载均衡
         */
        return list.get(atomicInteger.getAndAdd(1) ^ (listSize - 1));

    }
}
