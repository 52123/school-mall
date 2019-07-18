package com.hugh.rpc.utils;


import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author 52123
 * @since 2019/6/20 22:00
 */
public class ThreadPoolFactory {

    private static int threadNum = Runtime.getRuntime().availableProcessors() * 2;
    private static int coreSize = threadNum + 1;
    private static int maxPoolSize = threadNum + 1;
    private static long keepAlive = 2000L;
    private static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(100000);

    private ThreadPoolFactory(){}

    public static ThreadPoolExecutor getInstance(String nameFormat){
        return new ThreadPoolExecutor(coreSize, maxPoolSize, keepAlive, TimeUnit.MILLISECONDS,
                queue, new ThreadFactoryBuilder().setNameFormat(nameFormat).build());
    }
}
