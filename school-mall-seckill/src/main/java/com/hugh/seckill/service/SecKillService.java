package com.hugh.seckill.service;

import com.hugh.common.model.BaseResp;
import com.hugh.seckill.dto.SecKillReq;

/**
 * @author 52123
 * @since 2019/6/17 8:31
 */
public interface SecKillService {

    /**
     *  开始秒杀，两种实现-- Redis分布式锁和ZooKeeper分布式锁
     *  只负责实现生成订单和减库存
     * @param req 秒杀dto
     * @return 成功与否
     */
    BaseResp startRedisSecondKill(SecKillReq req);
}
