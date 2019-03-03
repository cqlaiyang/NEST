package com.example.laiyang.nest.taskEnum;

/**
 * @author 赖杨2018/10/26
 * @class 是一个枚举类，用于枚举每个任务，每个枚举实例都实现一个接口；
 */

import android.graphics.Bitmap;
import android.util.Log;

import com.example.laiyang.nest.algorithm.Crc;
import com.example.laiyang.nest.camera.veer.VeerCamera;
import com.example.laiyang.nest.connect.Connect_transport;
import com.example.laiyang.nest.taskEnum.carPlate.latest.LatestPlate;
import com.example.laiyang.nest.taskEnum.carPlate.oldPlate.Plate;
import com.example.laiyang.nest.taskEnum.landMark.LandMark;
import com.example.laiyang.nest.activity.queue.SendQueue;
import com.example.laiyang.nest.taskEnum.qrCode.MixRecong;
import com.example.laiyang.nest.taskEnum.shape.latest.Shape;
import com.example.laiyang.nest.taskEnum.shape.older.ShapeStatistics;
import com.example.laiyang.nest.taskEnum.trafficLight.TrafficLight;
import com.example.laiyang.nest.taskManager.Mission;
import com.example.laiyang.nest.taskManager.MissionQueue;
import com.example.laiyang.nest.taskManager.MissionQueueFactory;
import com.example.laiyang.nest.threadPool.ThreadPoolProxyFactory;
import com.example.laiyang.nest.utils.GetPicture;
import com.example.laiyang.nest.utils.Logger;
import com.example.laiyang.nest.utils.Turn;

import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public enum TaskEnum implements Mission {
    //二维码识别
    QR("CMD_QR_CODE:") {
        @Override
        public void execute() {
            //任务开始，打开线程
            missionQueue.start();
            ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {

                @Override
                public void run() {
                    MixRecong.Recong();
                }
            });
        }

        @Override
        public void execute(String s) {

        }
    },
    //车牌识别
    CAR_PLATE("CMD_PLATE:") {
        @Override
        public void execute() {
            missionQueue.start();
            ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
                @Override
                public void run() {
                    // 得到图片
                    Pic = GetPicture.getPicture();
                    String result = "";

                    // 处理的到返回值
                    try {

                        result = LatestPlate.latestPlate(Pic);
                        if (result.length() < 1 || result.equals("233333")) {


                            Connect_transport.send("NO_TARGET");

                        } else {
                            SendQueue sendQueue = new SendQueue(result);
                            missionQueue.add(sendQueue);

                            //摄像头复位
                            VeerCamera.Reset();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
        }

        @Override
        public void execute(String s) {

        }
    },
    // 形状识别
    // 2018/12/12 目前写完与调试完图形；
    // 它的能力远大于比赛
    // 1.形状识别，能识别比赛所有形状
    // 2.每个图形颜色判断（8种颜色），并且算法比较精炼；使用mask和欧式距离计算，系统鲁棒性大大提高！
    SHAPE("CMD_SHAPE:") {
        @Override
        public void execute() {
            missionQueue.start();
            ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
                @Override
                public void run() {

                    // 定义一个Map用于存取映射数据
                 //   Map<String, Integer> shapeResult = new HashMap<String, Integer>();

                    List<Map<String,Integer>> shapeResult = new ArrayList<>();

                    // 得到当前图片
                    String result = "";

                    Pic = GetPicture.getPicture();

                    try {
                        result = Plate.PlateRecognition(Pic);
                    } catch (Exception e) {
                        Logger.e("laiyang666", e + "");
                        e.printStackTrace();
                    }
                    if (result.length() > 1) {
                        Connect_transport.send("NO_TARGET");
                    } else {

                        // 图形识别
                        shapeResult = Shape.ShapeRecognition(Pic);

                   /*     for (Map.Entry<String, Integer> entry : shapeResult.entrySet()) {
                            Log.d("laiyang666", "Key = " + entry.getKey() + ",Value" + entry.getValue());
                        }*/

                        // 统计
                        String Numbers = com.example.laiyang.nest.taskEnum.shape.latest.ShapeStatistics.first(shapeResult);

                        Log.d("laiyang666", "" + Numbers);
                        // 发送回去
                        SendQueue ShapeMessage = new SendQueue(Numbers);
                        missionQueue.add(ShapeMessage);

                        //摄像头复位
                        VeerCamera.Reset();
                    }


                }
            });
        }

        @Override
        public void execute(String s) {

        }
    },
    //交通灯判断
    TRAFFIC_LIGHT("CMD_TRAFFIC:") {
        @Override
        public void execute() {
            missionQueue.start();
            ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
                @Override
                public void run() {
                    //得到图片
                    Pic = GetPicture.getPicture();
                    //处理得到返回值
                    String Color = TrafficLight.Detection(Pic);

                    SendQueue sendQueue = new SendQueue(Color);
                    missionQueue.add(sendQueue);
                    //摄像头复位
                    VeerCamera.Reset();
                }
            });
        }

        @Override
        public void execute(String s) {

        }
    },
    //立体显示标志物
    //判断是否显示
    IPS("CMD_STEMAK_IPS") {
        @Override
        public void execute() {
            missionQueue.start();
            // 摄像头向下一丢丢
            try {
                VeerCamera.LandMarkPosition();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
                @Override
                public void run() {
                    //得到图片
                    Pic = GetPicture.getPicture();
                    int pixl = LandMark.landMark(Pic);

                    Log.i("info", "立体标志物的像素点个数：" + pixl);
                    Logger.i("info", "立体标志物的像素点个数：" + pixl);
                    String reslut = "";
                    //判断像素点个数
                    if (pixl > 800) {
                        reslut = "SUCCESS";
                    } else {
                        reslut = "FAILURE";
                    }
                    SendQueue sendQueue = new SendQueue(reslut);
                    //回传数据
                    missionQueue.add(sendQueue);
                    //摄像头复位
                    VeerCamera.Reset();
                }
            });
        }

        @Override
        public void execute(String s) {

        }
    },

    SQRCODE("CMD_SQR_CODE") {
        @Override
        public void execute() {

        }

        @Override
        public void execute(String result) {
            missionQueue.start();

            if (result == null) {
                if (count == 0) {
                    count++;
                    missionQueue.add(new SendQueue(new byte[]{(byte) 0x03, (byte) 0x05, (byte) 0x14, (byte) 0x45, (byte) 0xDE, (byte) 0x92}));
                } else {
                    count = 0;
                    missionQueue.add(new SendQueue("3"));
                }

            } else if (count == 0) {
                count++;
                // 算法
                Map<String, Byte> table = new HashMap<String, Byte>();
                byte[] input = new byte[4];
                table.put("A", (byte) 0x00);
                table.put("B", (byte) 0x01);
                table.put("C", (byte) 0x02);
                table.put("D", (byte) 0x03);
                table.put("E", (byte) 0x04);
                table.put("U", (byte) 0x05);
                table.put("J", (byte) 0x10);
                table.put("I", (byte) 0x11);
                table.put("H", (byte) 0x12);
                table.put("G", (byte) 0x13);
                table.put("F", (byte) 0x14);
                table.put("V", (byte) 0x15);
                table.put("K", (byte) 0x20);
                table.put("L", (byte) 0x21);
                table.put("M", (byte) 0x22);
                table.put("N", (byte) 0x23);
                table.put("O", (byte) 0x24);
                table.put("W", (byte) 0x25);
                table.put("T", (byte) 0x30);
                table.put("S", (byte) 0x31);
                table.put("R", (byte) 0x32);
                table.put("Q", (byte) 0x33);
                table.put("P", (byte) 0x34);
                table.put("X", (byte) 0x35);
                for (int i = 0; i < 4; i++) {
                    if (i == 0) {
                        input[0] = table.get(result.substring(11, 12));
                    } else if (i == 1) {
                        input[1] = table.get(result.substring(13, 14));
                    } else if (i == 2) {
                        input[2] = table.get(result.substring(15, 16));
                    } else {
                        input[3] = table.get(result.substring(17, 18));
                    }
                }
                Logger.d("laiyang666", Turn.byte2hex(input) + "-" + Turn.byte2hex(Crc.toCrc(input)));
                missionQueue.add(new SendQueue(new byte[]{(byte) 0x03, (byte) 0x05, (byte) 0x14, (byte) 0x45, (byte) 0xDE, (byte) 0x92}));
            } else {
                count = 0;
                // 光档
                result = result.substring(22, 23);
                missionQueue.add(new SendQueue(result));
            }


        }
    },

    FRONT("FRONT") {
        @Override
        public void execute() {

            // 计算屏幕中心位置
            Bitmap bitmap = GetPicture.getPicture();

            Rect rect = VeerCamera.correction(bitmap);

            // 判断是否找到屏幕区域；没有找到就转动摄像头
            if (rect.height == bitmap.getHeight() && rect.width == bitmap.getWidth() && VeerCamera.count2 < 2) {
                VeerCamera.CarMistake();


                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                TaskEnum.FRONT.execute();
            } else {
                VeerCamera.count2 = 0;
                // 计算与标准点的偏差
                int x = (int) (rect.x + (rect.size().width / 2)) - 640;
                int y = (int) (rect.y + (rect.size().height / 2)) - 360;
                Log.d("laiyang666", x + "-" + y);
                try {
                    VeerCamera.doCorrection(x, y);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void execute(String s) {

        }
    },
    RFID("RFID") {
        @Override
        public void execute() {

        }

        @Override
        public void execute(String s) {

        }
    },
    LIFT("LIFT") {
        @Override
        public void execute() {
            if (VeerCamera.count2 == 0) {
                VeerCamera.staticLift();
            }

            // 计算屏幕中心位置
            Bitmap bitmap = Bitmap.createBitmap(GetPicture.getPicture());
            Rect rect = VeerCamera.correction(bitmap);

            // 判断是否找到屏幕区域；没有找到就转动摄像头
            if (rect.height == bitmap.getHeight() && rect.width == bitmap.getWidth() && VeerCamera.count2 < 2) {
                VeerCamera.CarMistake();

                TaskEnum.LIFT.execute();
            } else {

                // 计算与标准点的偏差
                int x = (int) (rect.x + (rect.size().width / 2)) - 640;
                int y = (int) (rect.y + (rect.size().height / 2)) - 360;
                Log.d("laiyang666", x + "-" + y);
                try {
                    VeerCamera.doCorrection(x, y);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void execute(String s) {

        }
    },
    RIGHT("RIGHT") {
        @Override
        public void execute() {
            if (VeerCamera.count2 == 0) {
                VeerCamera.staticRight();
            }

            // 计算屏幕中心位置
            Bitmap bitmap = Bitmap.createBitmap(GetPicture.getPicture());
            Rect rect = VeerCamera.correction(bitmap);

            // 判断是否找到屏幕区域；没有找到就转动摄像头
            if (rect.height == bitmap.getHeight() && rect.width == bitmap.getWidth() && VeerCamera.count2 < 2) {
                VeerCamera.CarMistake();

                TaskEnum.RIGHT.execute();
            } else {

                // 计算与标准点的偏差
                int x = (int) (rect.x + (rect.size().width / 2)) - 640;
                int y = (int) (rect.y + (rect.size().height / 2)) - 360;
                Log.d("laiyang666", x + "-" + y);
                try {
                    VeerCamera.doCorrection(x, y);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void execute(String s) {

        }
    },;

    // fix at 2018/12/9
    private static MissionQueue missionQueue = MissionQueueFactory.getMissionQueue();
    private String CMD;
    public Bitmap Pic;
    public static int count = 0;

    TaskEnum(String CMD) {
        this.CMD = CMD;
        Connect_transport connect_transport = new Connect_transport();
    }
}
