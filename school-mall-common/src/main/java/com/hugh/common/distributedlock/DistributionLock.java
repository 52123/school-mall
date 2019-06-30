package com.hugh.common.distributedlock;

/**
 * @author 52123
 * @since 2019/6/30 18:22
 * 策略模式
 */
public interface DistributionLock {

    /**
     * 加锁操作
     * @return 是否成功
     */
    boolean lock();

    /**
     * 释放锁操作
     */
    void releaseLock();
}
