/*
 * File Name: ThreadManager.java 
 * History:
 * Created by Lxh on 2014-4-4
 */
package utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 一个简易的线程池管理类，提供三个线程池
 */
public class ThreadManager {
    private static ThreadPoolProxy threadPoolProxy;

    private ThreadManager() {
    }

    /**
     * 获取代理类对象
     *
     * @return
     */
    public static ThreadPoolProxy getInstance() {
        if (threadPoolProxy == null) {
            threadPoolProxy = new ThreadPoolProxy(3, 5, 5);
        }
        return threadPoolProxy;
    }

    public static class ThreadPoolProxy {
        private int                corePoolSize;
        private int                maximumPoolSize;
        private int                keepAliveTime;
        private ThreadPoolExecutor threadPoolExecutor;

        public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, int keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        /**
         * 执行任务方法
         *
         * @param runnable
         */
        public void execute(Runnable runnable) {

            if (threadPoolExecutor == null) {
                threadPoolExecutor = new ThreadPoolExecutor(
                        corePoolSize,//核心线程数   3
                        maximumPoolSize,//最大线程数 5
                        keepAliveTime, //线程空闲存活时间
                        TimeUnit.MILLISECONDS,    //存活时间的单位
                        new LinkedBlockingDeque<Runnable>(), //任务队列
                        Executors.defaultThreadFactory());
                //线程池执行任务
                threadPoolExecutor.execute(runnable);
            } else {
                threadPoolExecutor.execute(runnable);
            }

        }

        /**
         * 从线程池中移除一个任务
         *
         * @param downloadTask
         */
        public void cancelDownload(Runnable downloadTask) {
            threadPoolExecutor.getQueue().remove(downloadTask);
        }
    }
}
