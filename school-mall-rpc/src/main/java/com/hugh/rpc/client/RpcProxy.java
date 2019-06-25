package com.hugh.rpc.client;

import com.hugh.rpc.protocol.RpcRequest;
import com.hugh.rpc.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 52123
 * @since 2019/6/20 17:57
 * 用户创建RPC服务代理
 */
@Slf4j
public class RpcProxy {

    private ServiceDiscover discover;

    private static final AtomicInteger ATOMIC_INT = new AtomicInteger(1);

    private RpcClient client;

    private String serviceAddress;

    private String address;

    private int port;

    private ReentrantLock lock = new ReentrantLock();

    public RpcProxy(ServiceDiscover discover){
        this.discover = discover;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[]{clazz}, (proxy, method, args) -> {
                    /*
                     * 封装被代理类的属性
                     */
                    RpcRequest request = new RpcRequest();
                    request.setRequestId(ATOMIC_INT.getAndIncrement());
                    request.setClassName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setParameters(args);
                    request.setParameterTypes(method.getParameterTypes());

                    lock.lock();
                    try {
                        /*
                         * 获取服务的地址(随机)
                         * 当地址不同的时候才重新初始化Netty客户端
                         */

                        String serviceAddress = discover.getServiceAddress();
                        if (StringUtils.isNotBlank(serviceAddress) && !serviceAddress.equals(this.serviceAddress)) {
                            this.address = serviceAddress.split(":")[0];
                            this.port = Integer.valueOf(serviceAddress.split(":")[1]);
                            this.serviceAddress = serviceAddress;
                            this.client = new RpcClient().init(address, port);
                        }
                    } finally {
                        lock.unlock();
                    }
                    /*
                     * 创建基于Netty实现的RpcClient连接服务端并发送请求
                     */
                    RpcResponse response = client.send(request).get();
                    if (response.isError()) {
                        log.error("PRC接收返回消息失败:", response.getMsg());
                        throw new Exception(response.getMsg());
                    }
                    return response.getResult();
                });

    }
}
