package com.hugh.common.uniquenessid;


import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author 52123
 * @since 2019/6/16 2:39
 */
@Slf4j
public class Snowflake {

    /**
     * 工作机器ID
     */
    private long workerId;

    /**
     *  数据中心ID
     */
    private long dataCenterId;

    /**
     * 序列号
     */
    private long sequence;

    /**
     * 初始时间戳
     */
    private long initTimeStamp = LocalDateTime.parse("2019-06-16 23:53")
                                    .atZone(ZoneId.systemDefault()).toEpochSecond();

    /**
     * 工作机器所占位数
     */
    private long workerIdBits = 5L;

    /**
     * 数据中心所占位数
     */
    private long dataCenterIdBits = 5L;

    /**
     * 序列号所占位数
     */
    private long sequenceBits = 12L;

    /**
     * 支持的最大机器id数 -- 31
     *
     */
    private long maxWorkerId = ~(-1L << workerIdBits);

    /**
     * 支持的最大数据中心id数 -- 31
     */
    private long maxDataCenterId = ~(-1L << dataCenterIdBits);

    /**
     *  序列号可以表示的最大正整数 -- 4095
     */
    private long sequenceMask = ~(-1L << sequenceBits);

    /**
     *  SnowFlake 的结构为 1个符号位 - 41位时间戳 - 5位数据中心ID 、5位工作机器ID - 12位序列号
     *  对应数据所需左移的位数
     */
    private long workerIdShift = sequenceBits;
    private long dataCenterIdShift = sequenceBits + workerIdBits;
    private long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;


    private long lastTimestamp = -1L;

    public Snowflake(long workerId, long dataCenterId, long sequence){

        // 合法性检验
        if (workerId > maxWorkerId || workerId < 0) {
            log.error(String.format("Illegal worker Id, it can't be greater than %d or less than 0",maxWorkerId));
            throw new IllegalArgumentException(String.format("Illegal worker Id, it can't be greater than %d or less than 0",maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            log.error(String.format("Illegal worker Id, it can't be greater than %d or less than 0", maxDataCenterId));
            throw new IllegalArgumentException(String.format("Illegal worker Id, it can't be greater than %d or less than 0", maxDataCenterId));
        }

        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        this.sequence = sequence;
    }

    /**
     *  生成唯一键
     *  逻辑：1.先判断当前时间是否大于上次生成时间
     *       2.若同一毫秒内有多个线程一起生成，则增加序列号
     *       3.将得到的所有数据移位到指定位
     * @return
     */
    public synchronized long nextId() {

        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            log.error("clock is moving backwards. Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            // 保证序列号不会溢出
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 获取大于上次生成的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return ((timestamp - initTimeStamp) << timestampLeftShift) |
                (dataCenterId << dataCenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }


    public long getWorkerId(){
        return workerId;
    }

    public long getDataCenterId(){
        return dataCenterId;
    }

    public long getTimestamp(){
        return System.currentTimeMillis();
    }
}
