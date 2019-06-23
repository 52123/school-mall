package com.hugh.rpc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 52123
 * @since 2019/6/23 21:36
 */
@Data
@ConfigurationProperties(prefix = "rpc.service")
public class RpcServiceProperties {

    private boolean enabled;

    private String serviceName;

    private String serviceAddress;

    private int servicePort = 8888;

    private String zooKeeperAddress;

    private String rootPath;
}
