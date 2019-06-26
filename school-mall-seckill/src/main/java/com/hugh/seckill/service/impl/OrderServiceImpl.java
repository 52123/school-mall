package com.hugh.seckill.service.impl;

import com.hugh.common.annotation.ObjectKeyCache;
import com.hugh.common.rpc.RedisService;
import com.hugh.common.uniquenessid.SnowFlakeFactory;
import com.hugh.seckill.dto.SecKillReq;
import com.hugh.seckill.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 52123
 * @since 2019/6/17 8:32
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Value("${workId}")
    private Long workId;

    @Value("${dataCenterId}")
    private Long dataCenterId;

    @Resource
    private RedisService redisService;


    @Override
    public Long checkRepeat(String key) {
        return (Long)redisService.existKey(key);
    }

    @Override
    @ObjectKeyCache(fields = {"userId","secondKillId"})
    public Long createOrderId(SecKillReq req) {
        return SnowFlakeFactory.getInstance(workId, dataCenterId).nextId();
    }


}
