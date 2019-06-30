package com.hugh.common.distributedlock.zookeeper;

import com.hugh.common.distributedlock.DistributionLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * @author 52123
 * @since 2019/6/25 23:14
 */
@Slf4j
public class ZkLock implements DistributionLock {

    private CuratorFramework client;

    private final InterProcessMutex mutex;

    public ZkLock(String zkAddress, String rootPath){
        this.client = CuratorFrameworkFactory.builder()
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .connectString(zkAddress).build();
        client.start();
        /*
         * 分布式可重入排他锁
         */
        this.mutex = new InterProcessMutex(client, rootPath);
    }

    @Override
    public boolean lock() {
        return this.lock(100, TimeUnit.MILLISECONDS);
    }

    public boolean lock(long time, TimeUnit timeUnit){
        try {
            return mutex.acquire(time, timeUnit);
        } catch (Exception e) {
            log.error(Thread.currentThread().getName() + "获取锁失败",e);
            return false;
        }
    }

    @Override
    public void releaseLock() {
        try {
            mutex.release();
        } catch (Exception e) {
            log.error(Thread.currentThread().getName() + "释放锁失败",e);
        }
    }
}
