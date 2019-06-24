package com.hugh.common.rpc;

import java.util.concurrent.TimeUnit;

/**
 * @author 52123
 * @since 2019/6/23 15:11
 */
public interface RedisService {

    /**
     * 单纯地写入键值
     * @param key 键
     * @param value 值
     */
    void setValue(String key, Object value);

    /**
     * 写入键并设置过期时间
     * @param key 键
     * @param value 值
     * @param timeout 时长
     * @param timeUnit 时长类型
     */
    void setValue(String key, Object value, long timeout, TimeUnit timeUnit);

    /**
     * 实现分布式锁，必须传入过期时间防止死锁
     * @param key 键
     * @param value 值
     * @param timeout 过期时长
     * @param timeUnit 时间类型
     * @return 成功与否
     */
    boolean setIfAbsent(String key, Object value, long timeout, TimeUnit timeUnit);

    /**
     * 根据键取出值
     * @param key 键
     * @return 值
     */
    Object getValue(String key);

    /**
     * 删除指定键值
     * @param key 键
     * @return 是否成功
     */
    boolean delete(String key);

    /**
     * 判断缓存中是否有此键
     * @param key 键
     * @return 是否存在
     */
    boolean hasKey(String key);

}
