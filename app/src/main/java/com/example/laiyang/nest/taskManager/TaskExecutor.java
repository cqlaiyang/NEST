package com.example.laiyang.nest.taskManager;

import java.util.concurrent.BlockingQueue;

/**
 * 这是一个任务的执行类，
 */
public class TaskExecutor extends Thread{

    //排队的任务，里面是排队的任务
    private BlockingQueue<Mission> taskQueue;
    //这个任务处理是否开启
    private boolean isRunning = true;


    //构造器用于构造一个队列
    public TaskExecutor(BlockingQueue<Mission> taskQueue) {
        this.taskQueue = taskQueue;
    }

    //退出执行任务，任务执行窗口关闭
    public void quit(){
        isRunning = false;
        interrupt();
    }


    @Override
    public void run() {

        while (isRunning){//如果是开启执行就继续
            Mission mission;
            try {
                mission = taskQueue.take();//这里拿到一个任务
            } catch (InterruptedException e) {
                if (!isRunning) {
                    //发生意外了，下班状态的话就窗口关闭。
                    interrupt();
                    break;
                }
                continue;
            }
            mission.execute();
        }
    }
}
