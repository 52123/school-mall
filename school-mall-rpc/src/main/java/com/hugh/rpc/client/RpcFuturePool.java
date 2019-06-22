package com.hugh.rpc.client;


import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 52123
 * @since 2019/6/21 2:53
 *  用传输唯一标识ID，记录response对应哪个request
 */
class RpcFuturePool {

    private static final ConcurrentHashMap<Integer, RpcFuture> FUTURE_RESPONSE_POOL = new ConcurrentHashMap<>();

    private RpcFuturePool() {
    }

    static ConcurrentHashMap<Integer, RpcFuture> getInstance() {
        return FUTURE_RESPONSE_POOL;
    }
}
