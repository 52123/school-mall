package com.hugh.seckill.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author 52123
 * @since 2019/06/17 12:57
 */
@Data
public class MallSeckill {
    private Long id;

    /**
     * 商品id
     */
    private Long itemId;

    /**
     * 商品名称
     */
    private String itemName;

    /**
     * 秒杀数量
     */
    private Integer seckillNum;

    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date createTime;
}