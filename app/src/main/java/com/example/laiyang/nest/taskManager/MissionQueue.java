package com.example.laiyang.nest.taskManager;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//任务队列
public class MissionQueue {
    //任务队列里的任务，队里面是待处理的Mission
    private BlockingQueue<Mission> missionQueue;

    //多任务执行

    private TaskExecutor[] mTaskExcutors;

    //在开发者new队列的时候，要指定多任务执行的个数就是开多少个线程！

    public MissionQueue(int Size) {
        missionQueue = new LinkedBlockingQueue<>();
        mTaskExcutors = new TaskExecutor[Size];
    }

    //打开每个任务窗口，开始执行任务

    public void start(){
        stop();
        //打开所有任务处理窗口,窗口大小已经都在构造函数里面构造好了！
        for (int i = 0; i < mTaskExcutors.length; i++){
            mTaskExcutors[i] = new TaskExecutor(missionQueue);
            mTaskExcutors[i].start();
        }
    }

    //关闭所有的窗口
    public void stop() {
        if (mTaskExcutors != null){
            for (TaskExecutor taskExecutor : mTaskExcutors){
                if (taskExecutor != null){
                    taskExecutor.quit();
                }
            }
        }
    }


    public <T extends Mission> int add (T mission){
        //判断是否包含这个任务 没有添加！
        if (!missionQueue.contains(mission)){
            missionQueue.add(mission);
        }
        //返回排的队的人数，公开透明，让外面的人看看有多少人在等待任务执行！
        return missionQueue.size();
    }
}
