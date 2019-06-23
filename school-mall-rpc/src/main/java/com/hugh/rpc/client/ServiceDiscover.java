package com.hugh.rpc.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 52123
 * @since 2019/6/19 23:59
 * 服务发现-- 用于RPC客户端寻找RPC服务端
 */
@Slf4j
public class ServiceDiscover {

    private final CuratorFramework client;

    private String rootPath;

    private String serviceName;

    private final AtomicInteger atomicInteger = new AtomicInteger(1);

    public ServiceDiscover(CuratorFramework client, String rootPath, String serviceName) {
        this.client = client;
        this.rootPath = rootPath;
        this.serviceName = serviceName;
    }

    public String getServiceAddress() throws Exception {
        List<String> list = client.getChildren().forPath(rootPath + "/" + serviceName);
        int listSize = list.size();
        if (listSize == 0) {
            throw new Exception("未能找到指定服务");
        }

        /*
         * 若服务有多个地址，实现负载均衡
         * 假如listSize等于1时，减一之后进行异或永远为另一个数(atomicInteger)
         * 所以要加个判断处理
         */
        return list.get(listSize == 1 ? 0 : atomicInteger.getAndAdd(1) ^ (listSize - 1));
    }
}
