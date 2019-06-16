package com.hugh.order.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author 52123
 * @since 2019/06/14 04:01
 */
@Data
public class MallOrder {
    /**
     * 全局唯一键
     */
    private Long id;

    /**
     * 实付金额
     */
    private BigDecimal pay;

    /**
     * 状态，0-未付款，1-已付款
     */
    private Byte status;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户昵称
     */
    private String userName;
}