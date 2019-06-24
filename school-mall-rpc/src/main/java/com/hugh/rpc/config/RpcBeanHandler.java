package com.hugh.rpc.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author 52123
 * @since 2019/6/24 15:22
 *  xml注入方式实现及bean解析注入参考
 *  https://gitee.com/a1234567891/koalas-rpc
 */
public class RpcBeanHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("client", new RpcBeanDefinitionParser(RpcClientProxy.class));
        registerBeanDefinitionParser("server", new RpcBeanDefinitionParser(RpcServiceBuilder.class));
    }
}
