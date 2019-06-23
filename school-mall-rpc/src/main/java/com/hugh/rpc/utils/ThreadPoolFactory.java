package com.hugh.rpc.utils;


import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 52123
 * @since 2019/6/20 22:00
 */
public class ThreadPoolFactory {

    private static int cpuNum = Runtime.getRuntime().availableProcessors();
    private static int coreSize = cpuNum + 1;
    private static int maxPoolSize = cpuNum + 1;
    private static long keepAlive = 2000;
    private static BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(1000);

    private ThreadPoolFactory(){}

    public static ThreadPoolExecutor getInstance(String nameFormat){
        return new ThreadPoolExecutor(coreSize, maxPoolSize, keepAlive, TimeUnit.MILLISECONDS,
                queue, new ThreadFactoryBuilder().setNameFormat(nameFormat).build());
    }
}
