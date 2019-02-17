package com.example.laiyang.nest.taskEnum.qrCode;


import android.util.Log;

import com.example.laiyang.nest.activity.PlayActivity;
import com.example.laiyang.nest.activity.queue.SendQueue;
import com.example.laiyang.nest.algorithm.Crc;
import com.example.laiyang.nest.camera.veer.VeerCamera;
import com.example.laiyang.nest.taskManager.MissionQueueFactory;
import com.example.laiyang.nest.utils.Logger;
import com.example.laiyang.nest.utils.Turn;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laiyang at
 * 2018/12/08
 * 混合识别 Zxing加上Zbar；
 */
public class MixRecong {
    public static boolean Break = false;

    public static int counter = 0;
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

                        // 摄像头
                        VeerCamera.count = 0;
                        VeerCamera.Reset();

                        switch (counter){
                            case 0 :{
/*                                String[]re = new String[]{result.substring(11,12) ,result.substring(13,14) , result.substring(15,16) , result.substring(17,18)};
                                Map<String,Byte> table= new HashMap<String,Byte>();
                                table.put("A", (byte) 0x00);table.put("B", (byte) 0x01);table.put("C", (byte) 0x02);table.put("D", (byte) 0x03);table.put("E", (byte) 0x04);table.put("U", (byte) 0x05);
                                table.put("J", (byte) 0x10);table.put("I", (byte) 0x11);table.put("H", (byte) 0x12);table.put("G", (byte) 0x13);table.put("F", (byte) 0x14);table.put("V", (byte) 0x15);
                                table.put("K", (byte) 0x20);table.put("L", (byte) 0x21);table.put("M", (byte) 0x22);table.put("N", (byte) 0x23);table.put("O", (byte) 0x24);table.put("W", (byte) 0x25);
                                table.put("T", (byte) 0x30);table.put("S", (byte) 0x31);table.put("R", (byte) 0x32);table.put("Q", (byte) 0x33);table.put("P", (byte) 0x34);table.put("X", (byte) 0x35);

                                byte[]stbuf = new byte[4];

                                stbuf[0] = table.get(re[0]);
                                stbuf[1] = table.get(re[1]);
                                stbuf[2] = table.get(re[2]);
                                stbuf[3] = table.get(re[3]);
                                // 算法
                                byte[] bytes = Crc.toCrc(stbuf);
                                Logger.d("laiyang666","" +Turn.byte2hex(bytes));
                                byte[] bytes1 = new byte[]{(byte)0x03,(byte)0x05,(byte)0x14,(byte)0x45, (byte) 0xDE, (byte) 0x92};*/

                                String i = result.substring(5,6);
                                String a = result.substring(12,13);
                                String b = result.substring(20,21);
                                String c = result.substring(58,59);
                                int zhuche = 0;
                                if (i.equals("A")){
                                    zhuche = 0;
                                }else if (i.equals("B")){
                                    zhuche = 1;
                                }else if (i.equals("C")){
                                    zhuche = 2;
                                }else if (i.equals("D")){
                                    zhuche = 3;
                                }else if (i.equals("E")){
                                    zhuche = 4;
                                }else if (i.equals("F")){
                                    zhuche = 5;
                                }else if (i.equals("G")){
                                    zhuche = 6;
                                }

                                MissionQueueFactory.getMissionQueue().add(new SendQueue(zhuche + "" + a + "" + b +"" + c));
                                counter ++;
                                break;
                            }
                            case 1:{
                                // 首先得到参数和密码子
                                int[] parm = new int[]{Integer.decode(result.substring(24,25)),
                                        Integer.parseInt(result.substring(26,27)),
                                        Integer.parseInt(result.substring(28,29)),
                                        Integer.parseInt(result.substring(30,31)),
                                        Integer.parseInt(result.substring(32,33)),
                                        Integer.parseInt(result.substring(34,35)),
                                        Integer.parseInt(result.substring(36,38))};
                                int[] key = new int[]{Integer.parseInt(result.substring(41,42)),
                                        Integer.parseInt(result.substring(43,44)),
                                        Integer.parseInt(result.substring(45,46)),
                                        Integer.parseInt(result.substring(47,48)),
                                        Integer.parseInt(result.substring(49,50)),
                                        Integer.parseInt(result.substring(51,52))};

                                // 进行排序
                                Arrays.sort(parm);
                                Arrays.sort(key);
                                int p = 0;
                                int q = 0;
                                // 得到最大质数
                                for (int i = parm.length -1 ; i > -1; i --){
                                    int count = 0;
                                    for (int j = 2; j <= parm[i] / 2;j ++){
                                        if (parm[i] % j == 0) {
                                            count ++;
                                        }
                                    }
                                    if (p == 0){
                                        if (count == 0){
                                            p = parm[i];
                                        }
                                    }else if (count == 0){
                                        q = parm[i];
                                        break;
                                    }

                                }

                                int n = p * q;
                                int ola = (p - 1)*(q - 1);

                                int e = 0;
                                // 找出e
                                for (int i = 1; i < ola; i ++) {
                                    if (ola % i != 0){
                                        int count = 0;
                                        for (int j = 2; j < i/2; j ++){
                                            if (i % j == 0){
                                                count ++;
                                            }
                                        }
                                        if (count == 0){
                                            e  = i;
                                            break;
                                        }
                                    }
                                }

                                int d = 0;
                                // 找出d
                                for (int i = 1; e * i < 65536; i++){
                                    if (e * i % ola == 1){
                                        d = i;
                                        break;
                                    }
                                }

                                byte[] reslut = new byte[6];

                                for (int i = 0; i < key.length; i ++) {
                                    reslut[i] = (byte) (((int)(Math.pow(key[i],d) % n)) & 0xff);
                                }

                                MissionQueueFactory.getMissionQueue().add(new SendQueue(reslut));
                                counter ++;
                                break;
                            }
                 /*           case 2:{
                                result = result.substring(16,17);
                                MissionQueueFactory.getMissionQueue().add(new SendQueue(result));
                                break;
                            }*/
                        }

                    }


                }

            }
        });
    }
}
