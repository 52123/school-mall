package com.hugh.rpc.client;

import com.hugh.rpc.protocol.RpcRequest;
import com.hugh.rpc.protocol.RpcResponse;
import com.hugh.rpc.registry.ServiceDiscover;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 52123
 * @since 2019/6/20 17:57
 * 用户创建RPC服务代理
 */
@Slf4j
@Component
public class RpcProxy {

    @Resource
    private ServiceDiscover discover;

    private static final AtomicInteger ATOMIC_INT = new AtomicInteger(1);

    @SuppressWarnings("unchecked")
    public <T> T create(String serviceName, Class<?> clazz) {
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

                    /*
                     * 获取服务的地址(随机)
                     */
                    String serviceAddress = discover.getServiceByName(serviceName);

                    /*
                     * 创建基于Netty实现的RpcClient连接服务端并发送请求
                     */
                    RpcResponse response = new RpcClient().init(serviceAddress).send(request).get();
                    if (response.isError()) {
                        log.error("PRC接收返回消息失败:", response.getMsg());
                        throw new Exception(response.getMsg());
                    }
                    return response.getResult();
                });

    }
}
