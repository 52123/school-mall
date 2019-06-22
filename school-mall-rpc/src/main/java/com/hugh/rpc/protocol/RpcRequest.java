package com.hugh.rpc.protocol;

import lombok.Data;

/**
 * @author 52123
 * @since 2019/6/20 17:03
 * RPC传输请求对象
 */
@Data
public class RpcRequest {

    /**
     * 标识唯一请求ID
     */
    private Integer requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;
}
