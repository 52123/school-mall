package com.hugh.rpc.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author 52123
 * @since 2019/6/24 15:24
 */
@Slf4j
public class RpcBeanDefinitionParser implements BeanDefinitionParser {

    private static final String CLIENT_NAME = "RpcClientProxy";
    private static final String SERVICE_INTERFACE = "serviceInterface";
    private static final String SERVICE_INTERFACE_NAME = "serviceInterfaceName";
    private static final String SERVICE_NAME = "serviceName";
    private static final String ROOT_PATH = "rootPath";
    private static final String ZOO_KEEPER_ADDRESS = "zooKeeperAddress";
    private static final String SERVICE_ADDRESS = "serviceAddress";
    private static final String SERVICE_PORT = "servicePort";
    private static final String ROOT_PATH_START = "/";

    private Class<?> clazz;
    RpcBeanDefinitionParser(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(clazz);
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);

        String rootPath = element.getAttribute(ROOT_PATH);
        if (StringUtils.isNotBlank(rootPath) && rootPath.startsWith(ROOT_PATH_START)) {
            beanDefinition.getPropertyValues().addPropertyValue(ROOT_PATH, rootPath);
        }

        String zooKeeperAddress = element.getAttribute(ZOO_KEEPER_ADDRESS);
        if (StringUtils.isNotBlank(zooKeeperAddress)) {
            beanDefinition.getPropertyValues().addPropertyValue(ZOO_KEEPER_ADDRESS, zooKeeperAddress);
        }

        String serviceName = element.getAttribute(SERVICE_NAME);
        if (StringUtils.isNotBlank(serviceName)) {
            beanDefinition.getPropertyValues().addPropertyValue(SERVICE_NAME, serviceName);
        }


        return CLIENT_NAME.equals(clazz.getSimpleName())
                ? parseClient(element,parserContext, beanDefinition) : parseServer(element, parserContext,beanDefinition);
    }

    private BeanDefinition parseServer(Element element, ParserContext context, RootBeanDefinition beanDefinition) {

        String serviceAddress = element.getAttribute(SERVICE_ADDRESS);
        if (StringUtils.isNotBlank(serviceAddress)) {
            beanDefinition.getPropertyValues().addPropertyValue(SERVICE_ADDRESS, serviceAddress);
        }

        beanDefinition.getPropertyValues().addPropertyValue(SERVICE_PORT, element.getAttribute(SERVICE_PORT));
        context.getRegistry().registerBeanDefinition(element.getAttribute("id"),beanDefinition);
        return beanDefinition;
    }

    private BeanDefinition parseClient(Element element,ParserContext context, RootBeanDefinition beanDefinition) {

        String serviceInterface = element.getAttribute(SERVICE_INTERFACE);
        if (StringUtils.isNotBlank(serviceInterface)) {
            try {
                beanDefinition.getPropertyValues().
                        addPropertyValue("serviceInterface", Class.forName(serviceInterface));
            } catch (ClassNotFoundException e) {
                log.error("ClassNotFoundException: can not find class :{}", serviceInterface);
            }
        }

        beanDefinition.getPropertyValues().addPropertyValue(SERVICE_INTERFACE_NAME, serviceInterface);
        context.getRegistry().registerBeanDefinition(element.getAttribute("id"),beanDefinition);
        return beanDefinition;
    }


}
