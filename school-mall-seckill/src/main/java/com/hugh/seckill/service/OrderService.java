package com.hugh.seckill.service;

import com.hugh.seckill.dto.SecKillReq;

/**
 * @author 52123
 * @since 2019/6/17 8:31
 */
public interface OrderService {

    /**
     * 从缓存服务器检查是否有相同key的orderId
     *
     * @param redisKey 缓存键
     * @return 订单ID
     */
    Long checkRepeat(String redisKey);

    /**
     * 生成全局唯一ID
     *
     * @param req 秒杀dto，缓存订单ID所需
     * @return 订单ID
     */
    Long createOrderId(SecKillReq req);


}
