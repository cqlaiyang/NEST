package com.example.laiyang.nest.taskEnum.qrCode;


import android.nfc.NfcAdapter;
import android.util.Log;

import com.example.laiyang.nest.activity.PlayActivity;
import com.example.laiyang.nest.activity.queue.SendQueue;
import com.example.laiyang.nest.algorithm.Crc;
import com.example.laiyang.nest.camera.veer.VeerCamera;
import com.example.laiyang.nest.taskManager.MissionQueue;
import com.example.laiyang.nest.taskManager.MissionQueueFactory;
import com.example.laiyang.nest.utils.Logger;
import com.example.laiyang.nest.utils.Turn;

import org.opencv.android.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingFormatArgumentException;

/**
 * Created by laiyang at
 * 2018/12/08
 * 混合识别 Zxing加上Zbar；
 */
public class MixRecong {
    public static boolean Break = false;
    public static int count = 0;
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
                        }else{
                            Recong();
                        }

                    } else {

                        // 摄像头
                        VeerCamera.count = 0;
                        VeerCamera.Reset();

                        Logger.d("laiyang666" ,result);
                        if (count == 0){
                            // 算法
                            Map<String,Byte> table = new HashMap<String, Byte>();
                            byte[] input = new byte[4];
                            table.put("A",(byte)0x00);table.put("B", (byte) 0x01);table.put("C",(byte)0x02);table.put("D",(byte)0x03);table.put("E",(byte)0x04);table.put("U",(byte)0x05);
                            table.put("J",(byte)0x10);table.put("I",(byte)0x11);table.put("H",(byte)0x12);table.put("G",(byte)0x13);table.put("F",(byte)0x14);table.put("V",(byte)0x15);
                            table.put("K",(byte)0x20);table.put("L",(byte)0x21);table.put("M",(byte)0x22);table.put("N",(byte)0x23);table.put("O",(byte)0x24);table.put("W",(byte)0x25);
                            table.put("T",(byte)0x30);table.put("S",(byte)0x31);table.put("R",(byte)0x32);table.put("Q",(byte)0x33);table.put("P",(byte)0x34);table.put("X",(byte)0x35);
                            for (int i = 0; i <  4;i++) {
                                if (i == 0){
                                    input[0] = table.get(result .substring(11,12));
                                }else if (i == 1){
                                    input[1] = table.get(result.substring(13,14));
                                }else if (i == 2){
                                    input[2] = table.get(result.substring(15,16));
                                }else {
                                    input[3] = table.get(result.substring(17,18));
                                }
                            }
                            Logger.d("laiyang666",Turn.byte2hex(input)+"-" + Turn.byte2hex(Crc.toCrc(input)));

                            MissionQueueFactory.getMissionQueue().add(new SendQueue(new byte[]{(byte) 0x03,(byte) 0x05,(byte) 0x14,(byte) 0x45,(byte) 0xDE,(byte) 0x92}));

                            count ++;
                        }else
                        if (count == 1){
                            // 光档
                            result = result.substring(22,23);
                            MissionQueueFactory.getMissionQueue().add(new SendQueue(result));
                            count++;
                        }else
                        if (count == 2){
                            // 车库
                            count = 0;
                            result = result.substring(16,17);
                            MissionQueueFactory.getMissionQueue().add(new SendQueue(result));

                        }
                        /*
                        result = result.substring(10,11);
                        MissionQueueFactory.getMissionQueue().add(new SendQueue(result));
                        // 这里进行字符串截取与算法操作*/
                    }


                }

            }
        });
    }
}
