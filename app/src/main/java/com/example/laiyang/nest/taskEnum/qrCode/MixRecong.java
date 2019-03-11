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
                        // 解决Zbar的一个Bug
                        if ((result.charAt(0) & 0xffff)== (char) 0xfeff){
                            result = result.substring(1);
                        }
                        //----------------------------------------------------------------------------------------------------------------------------------------------
                        String[] strArrary = result.split(",");
                        for (int i = 0; i < strArrary.length;i++){
                            System.out.println(i + "-" + strArrary[i]);
                        }

                        int M02_1 = Integer.parseInt(strArrary[0].substring(12,14)) - 10;
                        int M02_2 = Integer.parseInt(strArrary[1].substring(0,2)) - 10;
                        int M02_3 = Integer.parseInt(strArrary[2].substring(0,2)) - 10;
                        int M02_4 = Integer.parseInt(strArrary[3].substring(0,2)) - 10;

                        byte[] M03 = strArrary[3].substring(8,10).getBytes();
                        byte[] M04 = strArrary[3].substring(16,18).getBytes();
                        byte[] M05 = strArrary[4].substring(5,6).getBytes();

                        byte[] bytes = new byte[]{(byte)0x03,0x05,0x14,0x45, (byte) 0xDE,(byte) 0x92,M03[0],M03[1],M04[0],M04[1],M05[0]};

                        MissionQueueFactory.getMissionQueue().add(new SendQueue(bytes));
                    }


                }//(byte)0x03,0x05,0x14,0x45, (byte) 0xDE,(byte) 0x92

            }
        });
    }
}
