package com.hugh.rpc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 52123
 * @since 2019/6/23 21:55
 */
@Data
@ConfigurationProperties(prefix = "rpc.client")
public class RpcClientProperties {

    private boolean enabled;

    private String serviceName;

    private String zooKeeperAddress;

    private String rootPath;

}
