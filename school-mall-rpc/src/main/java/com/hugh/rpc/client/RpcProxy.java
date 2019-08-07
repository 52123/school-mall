package com.hugh.rpc.client;

import com.hugh.rpc.protocol.RpcRequest;
import com.hugh.rpc.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 52123
 * @since 2019/6/20 17:57
 * 用户创建RPC服务代理
 */
@Slf4j
public class RpcProxy {

    private ServiceDiscover discover;

    private static final AtomicInteger ATOMIC_INT = new AtomicInteger(1);

    public RpcProxy(ServiceDiscover discover) {
        this.discover = discover;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[]{clazz}, (proxy, method, args) -> {

                    RpcRequest request = new RpcRequest();
                    request.setRequestId(ATOMIC_INT.getAndIncrement());
                    request.setClassName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setParameters(args);
                    request.setParameterTypes(method.getParameterTypes());

                    /*
                     * 创建基于Netty实现的RpcClient连接服务端并发送请求
                     */
                    RpcResponse response = discover.getRpcClient().send(request).get();
                    if (response.isError()) {
                        log.error("PRC接收返回消息失败:", response.getMsg());
                        throw new Exception(response.getMsg());
                    }
                    return response.getResult();
                });
    }
}
