package com.hugh.common.distributedlock.redis;

import com.hugh.common.rpc.RedisService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author 52123
 * @since 2019/6/26 19:25
 */

@Slf4j
public class RedisLockUtil {

    private RedisService redisService;

    private String lockKey;

    public RedisLockUtil(String lockKey, RedisService redisService) {
        this.lockKey = lockKey;
        this.redisService = redisService;
    }

    public boolean lock() {
        return this.lock(3, 10, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 上锁，并设置键的过期时间防止死锁
     * 失败则进行重试
     *
     * @param maxRetries    重试次数
     * @param baseSleepTime 重试间隔时间
     * @param expireTime    键的过期时间
     * @return 是否成功获得锁
     */
    public boolean lock(int maxRetries, int baseSleepTime, long expireTime, TimeUnit timeUnit) {
        if (maxRetries < 0 || baseSleepTime < 0 || expireTime < 0) {
            return false;
        }
        boolean isLock = redisService.setIfAbsent(lockKey, "1", expireTime, timeUnit);
        System.out.println(Thread.currentThread().getName() + " " + isLock);
        if (isLock) {
            return true;
        } else {
            while (maxRetries-- > 0) {
                try {
                    Thread.sleep(baseSleepTime);
                } catch (InterruptedException e) {
                    log.error(Thread.currentThread().getName() + "is interrupted", e);
                }
            }
        }
        return false;
    }

    /**
     * 释放锁
     */
    public void releaseLock() {
        redisService.delete(lockKey);
    }
}
