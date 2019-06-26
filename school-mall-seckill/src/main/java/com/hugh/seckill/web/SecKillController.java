package com.hugh.seckill.web;

import com.hugh.common.model.BaseResp;
import com.hugh.rpc.utils.ThreadPoolFactory;
import com.hugh.seckill.dto.SecKillReq;
import com.hugh.seckill.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 52123
 * @since 2019/6/17 8:30
 */
@Slf4j
@RestController
public class SecKillController {

    @Resource
    private SecKillService seckillService;

    private ThreadPoolExecutor executor =
            ThreadPoolFactory.getInstance("SecKill-%d");

    @PostMapping("/start-redis-lock")
    public BaseResp startSecondKill(@RequestBody SecKillReq req){
        try {
            return executor.submit(() -> seckillService.startRedisSecondKill(req)).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(Thread.currentThread().getName() + "run error", e);
        }
        return BaseResp.fail("0002","秒杀失败，换个姿势再试吧");
    }
}
