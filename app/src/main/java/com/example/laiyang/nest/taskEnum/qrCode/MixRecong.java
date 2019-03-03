package com.example.laiyang.nest.taskEnum.qrCode;

import android.util.Log;

import com.example.laiyang.nest.activity.PlayActivity;
import com.example.laiyang.nest.activity.queue.SendQueue;
import com.example.laiyang.nest.camera.veer.VeerCamera;
import com.example.laiyang.nest.taskManager.MissionQueueFactory;
import com.example.laiyang.nest.utils.Logger;

/**
 * Created by laiyang at
 * 2018/12/08
 * 混合识别 Zxing加上Zbar；
 */
public class MixRecong {
    private static boolean Break = false;


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
                        } else {
                            Recong();
                        }

                     } else {// 确保回传队列开启
                        MissionQueueFactory.getMissionQueue().start();

                        result = result.trim();

                        if ((result.charAt(0) & 0xffff)== (char) 0xfeff){
                            result = result.substring(1);
                        }

                        // 摄像头
                        VeerCamera.count = 0;
                        VeerCamera.Reset();
                            int M02 = Integer.decode(result.substring(5, 6));
                            int M03_1 = Integer.decode(result.substring(8, 10));
                            int M03_2 = Integer.decode(result.substring(11, 13));
                            int M03_3 = Integer.decode(result.substring(14, 16));
                            int M03_4 = Integer.decode(result.substring(17, 19));
                            String M04 = result.substring(25, 26);

                            int zhuche = 0;
                            if (M04.equals("A")) {
                                zhuche = 0;
                            } else if (M04.equals("B")) {
                                zhuche = 1;
                            } else if (M04.equals("C")) {
                                zhuche = 2;
                            } else if (M04.equals("D")) {
                                zhuche = 3;
                            } else if (M04.equals("E")) {
                                zhuche = 4;
                            } else if (M04.equals("F")) {
                                zhuche = 5;
                            } else if (M04.equals("G")) {
                                zhuche = 6;
                            } else {
                                Logger.d("laiyang666", "" + M04);
                            }

                            byte[] bytes = new byte[]{(byte) M02, (byte) M03_1, (byte) M03_2, (byte) M03_3, (byte) M03_4, (byte) zhuche};
                            MissionQueueFactory.getMissionQueue().add(new SendQueue(bytes));
                    }


                }

            }
        });
    }
}
