package com.hugh.common.uniquenessid;

/**
 * @author 52123
 * @since 2019/6/17 9:05
 */
public class SnowFlakeFactory {

    private volatile static SnowFlake snowFlake = null;

    private SnowFlakeFactory(){}

    /**
     * 使用双重检测机制生成单例
     * @param workId 机器ID
     * @param dataCenterId 数据中心ID
     * @return SnowFlake实例
     */
    public static SnowFlake getInstance(Long workId, Long dataCenterId){
        if(snowFlake == null){
            synchronized ("SnowFlakeInstance"){
                if(snowFlake == null){
                    snowFlake = new SnowFlake(workId, dataCenterId);
                }
            }
        }
        return snowFlake;
    }
}
