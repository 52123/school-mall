package com.hugh.rpc.client;

import com.hugh.rpc.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author 52123
 * @since 2019/6/21 0:38
 * 用于RPC异步回调，采用了生产者-消费者模式解决假死问题
 */
@Slf4j
public class RpcFuture implements Future<RpcResponse> {

    private RpcResponse response;
    private long startTime;
    private boolean done = false;
    private final Object lock = new Object();

    RpcFuture() {
        this.startTime = System.currentTimeMillis();
    }

    void setResponse(RpcResponse response) {
        this.response = response;
        synchronized (lock) {
            done = true;
            lock.notifyAll();
        }
        long responseTime = System.currentTimeMillis() - startTime;
        long responseTimeThreshold = 5000;
        if (responseTime > responseTimeThreshold) {
            log.warn("Service response time is too slow. Request id = " + response.getRequestId() + ". Response Time = " + responseTime + "ms");
        }
    }

    @Override
    public RpcResponse get() throws InterruptedException {
        /*
         * 万一传输途中服务器挂了，就完了，一直阻塞
         */
        synchronized (lock) {
            if (!done) {
                lock.wait();
            }
        }
        return response;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) {
        return null;
    }

}
