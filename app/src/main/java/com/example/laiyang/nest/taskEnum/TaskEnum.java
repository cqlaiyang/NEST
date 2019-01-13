package com.example.laiyang.nest.taskEnum;

/**
 * @author 赖杨2018/10/26
 * @class 是一个枚举类，用于枚举每个任务，每个枚举实例都实现一个接口；
 */

import android.graphics.Bitmap;
import android.util.Log;

import com.example.laiyang.nest.camera.veer.VeerCamera;
import com.example.laiyang.nest.connect.Connect_transport;
import com.example.laiyang.nest.taskEnum.carPlate.latest.LatestPlate;
import com.example.laiyang.nest.taskEnum.carPlate.oldPlate.Plate;
import com.example.laiyang.nest.taskEnum.landMark.LandMark;
import com.example.laiyang.nest.activity.queue.SendQueue;
import com.example.laiyang.nest.taskEnum.qrCode.MixRecong;
import com.example.laiyang.nest.taskEnum.shape.Shape;
import com.example.laiyang.nest.taskEnum.shape.ShapeStatistics;
import com.example.laiyang.nest.taskEnum.trafficLight.TrafficLight;
import com.example.laiyang.nest.taskManager.Mission;
import com.example.laiyang.nest.taskManager.MissionQueue;
import com.example.laiyang.nest.taskManager.MissionQueueFactory;
import com.example.laiyang.nest.threadPool.ThreadPoolProxyFactory;
import com.example.laiyang.nest.utils.GetPicture;
import com.example.laiyang.nest.utils.Logger;

import org.opencv.core.Rect;

import java.util.HashMap;
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
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
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
                    Map<String, Integer> shapeResult = new HashMap<String, Integer>();


                    // 得到当前图片
                    String result = "";

                    Pic = GetPicture.getPicture();

                    try {
                        result = Plate.PlateRecognition(Pic);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result.length() > 1) {
                            Connect_transport.send("NO_TARGET");
                        } else {
                            // 图形识别
                            shapeResult = Shape.ShapeRecognition(Pic);

                            for (Map.Entry<String,Integer> entry : shapeResult.entrySet()) {
                                Log.d("laiyang666","Key = " + entry.getKey() + ",Value" + entry.getValue());
                            }

                            // 统计
                            String Numbers = ShapeStatistics.Statistics(shapeResult);

                            Log.d("laiyang666", "" + Numbers);
                            // 发送回去
                            SendQueue ShapeMessage = new SendQueue(Numbers);
                            missionQueue.add(ShapeMessage);
                        }
                  

                }
            });
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
                }
            });
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
    },

    FRONT("FRONT") {
        @Override
        public void execute() {
            if (VeerCamera.count2 == 0) {
                VeerCamera.Reset();
            }

            // 计算屏幕中心位置
            Bitmap bitmap = Bitmap.createBitmap(GetPicture.getPicture());
            Rect rect = VeerCamera.correction(bitmap);

            // 判断是否找到屏幕区域；没有找到就转动摄像头
            if (rect.height == bitmap.getHeight() && rect.width == bitmap.getWidth() && VeerCamera.count2 < 2) {
                VeerCamera.CarMistake();

                TaskEnum.RIGHT.execute();
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
    },;

    // fix at 2018/12/9
    private static MissionQueue missionQueue = MissionQueueFactory.getMissionQueue();
    private String CMD;
    public Bitmap Pic;

    TaskEnum(String CMD) {
        this.CMD = CMD;
        Connect_transport connect_transport = new Connect_transport();
    }
}
