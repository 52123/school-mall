package com.hugh.rpc.protocol;

import lombok.Data;

/**
 * @author 52123
 * @since 2019/6/20 17:04
 * RPC消息响应传输对象
 */
@Data
public class RpcResponse {

    private Integer requestId;

    private String msg;

    private Object result;

    public boolean isError(){
        return !"OK".equals(msg);
    }
}
