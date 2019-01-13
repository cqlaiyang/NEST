package com.example.laiyang.nest.taskEnum.qrCode;


import android.util.Log;

import com.example.laiyang.nest.activity.PlayActivity;
import com.example.laiyang.nest.activity.queue.SendQueue;
import com.example.laiyang.nest.camera.veer.VeerCamera;
import com.example.laiyang.nest.taskManager.MissionQueueFactory;

/**
 * Created by laiyang at
 * 2018/12/08
 * 混合识别 Zxing加上Zbar；
 */
public class MixRecong {
    public static boolean Break = false;

    public static void Recong() {
        // 判断当前任务是否为算法

        PlayActivity.instance.RecogQrCode(new QrCallBack() {
            @Override
            public void callbleBack(String result) {

                Log.d("laiyang666", result + "二维码回调");


                // 回调如果Zxing和Zbar混合识别都没有结果
                // 第一次没有结果转动摄像头
                if (!Break) {

                    if (result == null) {

                        // 转动摄像头的三阶方法
                        VeerCamera.staticCarMistake();
                        // 休眠等在转向完成
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        if (VeerCamera.count > 3) {
                            VeerCamera.count = 0;

                            // 任务失败:发送失败Not Find
                            MissionQueueFactory.getMissionQueue().add(new SendQueue("FAILUIE"));
                            Break = true;
                        }else{
                            Recong();
                        }

                    } else {
                        VeerCamera.count = 0;
                        VeerCamera.Reset();
                        result = result.substring(10,11);
                        MissionQueueFactory.getMissionQueue().add(new SendQueue(result));
                        // 这里进行字符串截取与算法操作
                    }


                }

            }
        });
    }
}
