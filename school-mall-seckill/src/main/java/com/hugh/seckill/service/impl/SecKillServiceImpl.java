package com.hugh.seckill.service.impl;

import com.hugh.common.distributedlock.redis.RedisLockUtil;
import com.hugh.common.model.BaseResp;
import com.hugh.common.rpc.RedisService;
import com.hugh.seckill.dto.SecKillReq;
import com.hugh.seckill.entity.MallOrder;
import com.hugh.seckill.mapper.MallOrderMapper;
import com.hugh.seckill.mapper.MallSecKillMapper;
import com.hugh.seckill.service.OrderService;
import com.hugh.seckill.service.SecKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author 52123
 * @since 2019/6/17 8:32
 */
@Service
public class SecKillServiceImpl implements SecKillService {

    @Resource
    private OrderService orderService;

    @Resource
    private MallOrderMapper orderMapper;

    @Resource
    private MallSecKillMapper secKillMapper;

    private RedisService redisService;

    private RedisLockUtil redisLock;

    @Autowired
    public SecKillServiceImpl(RedisService redisService){
        this.redisService = redisService;
        this.redisLock = new RedisLockUtil("redisLock",redisService);
    }

    /**
     * 使用Redis分布式锁生成全局唯一订单ID
     * @param req 秒杀dto
     */
    @Override
    public BaseResp startRedisSecondKill(SecKillReq req) {
        // 短时间(30秒)内同一用户对同一商品发起多次请求
        if(redisLock.lock()) {
            System.out.println(Thread.currentThread().getName() + "  getLock");
            String redisKey = req.userId + ":" + req.secondKillId;
            Long orderId = orderService.checkRepeat(redisKey);
            if (orderId != null) {
                System.out.println(Thread.currentThread().getName() + "  releaseLock");
                redisLock.releaseLock();
                return BaseResp.fail("0002", "请勿重复下单");
            }
            if (checkSecKillCount(req.secondKillId) > 0) {
                orderId = orderService.createOrderId(req);
                System.out.println(orderId);
                MallOrder mallOrder = new MallOrder();
                mallOrder.setId(orderId);
                mallOrder.setUserId(req.userId);
                mallOrder.setCreateTime(new Date(System.currentTimeMillis()));
                orderMapper.insert(mallOrder);
                reduceCount(req.secondKillId);
                System.out.println(Thread.currentThread().getName() + "  releaseLock");
                redisLock.releaseLock();
                return BaseResp.success("抢购成功");
            } else {
                System.out.println(Thread.currentThread().getName() + "  releaseLock");
                redisLock.releaseLock();
                return BaseResp.success("秒杀已结束");
            }
        }else{
            return BaseResp.fail("0002","换个姿势试试再吧");
        }
    }

    private int checkSecKillCount(Long secondKillId) {
        Integer count = (Integer) redisService.existKey("secKill:" + secondKillId);
        if(count == null){
            count = secKillMapper.selectSecKillCount(secondKillId);
            redisService.setValue("secKill:" + secondKillId, count);
        }
        return count;
    }

    private void reduceCount(Long secKillId){
        redisService.decrement("secKill:" + secKillId);
    }
}
