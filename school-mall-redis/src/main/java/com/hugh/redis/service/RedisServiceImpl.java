package com.hugh.redis.service;

import com.hugh.common.rpc.RedisService;
import com.hugh.rpc.server.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author 52123
 * @since 2019/6/23 15:47
 */
@Slf4j
@RpcService
@Service
public class RedisServiceImpl implements RedisService {

    private RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> opsValue;

    @Autowired
    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.opsValue = redisTemplate.opsForValue();
    }

    @Override
    public void setValue(String key, Object value) {
        opsValue.set(key, value);
    }

    @Override
    public void setValue(String key, Object value, long timeout, TimeUnit timeUnit) {
        opsValue.set(key, value, timeout, timeUnit);
    }

    @Override
    public boolean setIfAbsent(String key, Object value, long timeout, TimeUnit timeUnit) {
        Boolean flag = opsValue.setIfAbsent(key, value, timeout, timeUnit);
        if(flag == null){
            return false;
        }else{
            return flag;
        }
    }

    @Override
    public Object getValue(String key) {
        return opsValue.get(key);
    }

    @Override
    public boolean delete(String key) {
        Boolean flag = redisTemplate.delete(key);
        if(flag == null){
            return false;
        }else{
            return flag;
        }
    }

    @Override
    public boolean hasKey(String key) {
        Boolean flag = redisTemplate.hasKey(key);
        if(flag == null){
            return false;
        }else{
            return flag;
        }
    }

    @Override
    public Object existKey(String key) {
        return hasKey(key) ? getValue(key) : null;
    }

    @Override
    public Long decrement(String key) {
        return opsValue.decrement(key);
    }
}
