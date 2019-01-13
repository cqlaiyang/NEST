package com.example.laiyang.nest.threadPool;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by laiyang on 2018/11/3 17:22
 */
public class ThreadPoolProxy {
    ThreadPoolExecutor mExecutor;
    private int mCorePoolSize;
    private int mMaximumPoolSize;

    /**
     *
     * @param mCorePoolSize 核心池的大小
     * @param mMaximumPoolSize 最大线程数
     */
    public ThreadPoolProxy(int mCorePoolSize, int mMaximumPoolSize) {
        this.mCorePoolSize = mCorePoolSize;
        this.mMaximumPoolSize = mMaximumPoolSize;
    }

    /**
     * 初始化ThreadPoolExecutor
     * 双重检查加锁,只有在第一次实例化的时候才启用同步机制，提高性能
     * 在Chrome书签里的Java enum类型里面有解释；
     */
    private void initThreadPoolExecutor() {
        if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
            synchronized (ThreadPoolProxy.class) {
                if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
                    long keepAliveTime = 3000;
                    TimeUnit unit = TimeUnit.MILLISECONDS;
                    BlockingQueue workQueue = new LinkedBlockingDeque<>();
                    ThreadFactory threadFactory = Executors.defaultThreadFactory();
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();

                    mExecutor = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, keepAliveTime, unit, workQueue,
                            threadFactory, handler);
                }
            }
        }
    }

    /**
     * 执行任务和提交任务的区别？
     * 1.有无返回值
     * execute->有没有返回值
     * submit--有没有返回值
     * 2.Future的具体作用？
     * 1.有方法可以接收一个任务执行完成之后的结果，其实就是get方法，get方法就是一个阻塞方法
     * 2.get方法的签名抛出了异常==>可以处理任务执行过程中可能遇到的异常
     */

    /**
     * 执行任务
     */

    public void excute(Runnable task){
        initThreadPoolExecutor();

        mExecutor.execute(task);
    }

    /**
     * 提交任务
     */

    public Future submit(Runnable task){
        initThreadPoolExecutor();
        return mExecutor.submit(task);
    }

    /**
     * 移除任务
     */
    public void remove(Runnable task){
        initThreadPoolExecutor();
        mExecutor.remove(task);
    }
}
