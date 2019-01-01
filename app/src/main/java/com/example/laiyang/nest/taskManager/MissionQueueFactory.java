package com.example.laiyang.nest.taskManager;

public class MissionQueueFactory {
    public static MissionQueue mMissionQueue;

    public static MissionQueue getMissionQueue(){
        if (mMissionQueue == null){
            synchronized (MissionQueueFactory.class){
                if (mMissionQueue == null) {
                    mMissionQueue = new MissionQueue(1);
                }
            }
        }
        return mMissionQueue;
    }

}
