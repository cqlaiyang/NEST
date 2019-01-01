package com.example.laiyang.nest.camera.veer;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.laiyang.nest.camera.utils.ContentCommon;
import com.example.laiyang.nest.camera.utils.SystemValue;
import com.example.laiyang.nest.threadPool.ThreadPoolProxyFactory;
import com.example.laiyang.nest.utils.Logger;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import vstc2.nativecaller.NativeCaller;

/**
 * Created by laiyang 2018/11/9
 * 这是一个控制摄像头转动的类；
 */

public class VeerCamera {


    public static void right(int time){
        NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_RIGHT);
        try {
            Thread.sleep(1000 * time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        NativeCaller.PPPPPTZControl(SystemValue.deviceId,ContentCommon.CMD_PTZ_RIGHT_STOP);
    }

    public static void lift(int time){
        NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_LEFT);
        try {
            Thread.sleep(1000 * time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        NativeCaller.PPPPPTZControl(SystemValue.deviceId,ContentCommon.CMD_PTZ_LEFT_STOP);
    }

    /**
     * @pama 摄像头SDK有16个预设位，
     * 设置好以后就可以快速的转到特定的位置；
     * 这个正前方，也是摄像头初始化以后的位置；
     */
    public static void Reset() {
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
            @Override
            public void run() {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, 31); // 第一个预设位我设置为复位
            }
        });
    }

    /**
     * 摄像头向下转45度因为只有立体标志物使用我就把他叫LandMark了
     */
    public static void LandMarkPosition() throws InterruptedException {
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
            @Override
            public void run() {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, 33); // 转动到正前方向下45度
            }
        });

        Thread.sleep(1000);
    }

    /**
     * 摄像头左边90度
     */
    public static void staticLift() {
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
            @Override
            public void run() {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, 35);
            }
        });


    }


    /**
     * 摄像头右边90度
     */
    public static void staticRight() {
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
            @Override
            public void run() {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, 39);
            }
        });
    }

    public static void staticMinLift() {
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
            @Override
            public void run() {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, 41);
            }
        });
    }

    public static void staticMinRight() {
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
            @Override
            public void run() {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, 43);
            }
        });
    }

    public static void MinDown() {
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
            @Override
            public void run() {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, 45);
            }
        });
    }

    public static int count = 0;
    public static int count2 = 0;
    /**
     * 第一次调用向下转
     * 第二次调用向左转
     * 第三次调用向右转
     * 第四次调用复位，该任务失败；
     */
    public static void staticCarMistake() {
        if (count == 0) {
            MinDown();
            count++;
        } else if (count == 1) {
            staticMinLift();
            count++;
        } else if (count == 2) {
            staticMinRight();
            count++;
        } else if (count == 3) {
            Reset();
            count++;
        }
    }

    public static void CarMistake() {
        if (count2 == 0) {
            lift(1);
            count2++;
        } else if (count2 == 1) {
            right(2);
            count2++;
        } else if (count2 == 2) {
            count2++;
        }
    }

    // 得到待识别框；可以简化代码提高性能
    // Todo:2018/12/2

    public static Rect correction(Bitmap bitmap) {

        Mat Srcmat = new Mat();

        Utils.bitmapToMat(bitmap, Srcmat);

        // 转为单通道灰度图；
        Mat GrayMat = new Mat();
        Imgproc.cvtColor(Srcmat, GrayMat, Imgproc.COLOR_RGB2GRAY);
        // 计算均值,方差
        MatOfDouble means = new MatOfDouble();
        MatOfDouble stddevs = new MatOfDouble();
        // 计算每个个通道的均值与方差
        Core.meanStdDev(GrayMat, means, stddevs);

        double[] mean = means.toArray();
        // 方差越大，图片的视觉效果越好；
        double[] stddev = stddevs.toArray();
        // 平均值
        int average = (int) mean[0];
        // 宽 / 高 / 通道数
        int width = GrayMat.cols();
        int hight = GrayMat.rows();
        int channels = GrayMat.channels();
        // 原灰度图数组

        byte[] Pixl = new byte[width * hight * channels];

        // 得到像素点；
        GrayMat.get(0, 0, Pixl);

        int pv = 0;
        // 二值化在平均值的提出上提高80单位；使低光区几乎没有细节；
        for (int i = 0; i < Pixl.length; i++) {
            pv = Pixl[i] & 0xff;
            // 二值化阀值设高80大概就能屏蔽很多暗色区域；

            if (pv > average + 80) {
                Pixl[i] = (byte) 255;
            } else {
                Pixl[i] = (byte) 0;
            }
        }
        GrayMat.put(0, 0, Pixl);

        // 接下来进行腐蚀操作形态学操作
        // 首先定义一个模糊算子
        Mat k = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(6, 6), new Point(3, 3));
        // 形态学开操作
        Imgproc.morphologyEx(GrayMat, GrayMat, Imgproc.MORPH_OPEN, k);

        // 计算X方向的梯度值；
        Mat gradx = new Mat();
        Imgproc.Sobel(GrayMat, gradx, CvType.CV_32F, 1, 0);
        Core.convertScaleAbs(gradx, gradx);
        // 计算Y方向的梯度值；
        Mat grady = new Mat();
        Imgproc.Sobel(GrayMat, grady, CvType.CV_32F, 0, 1);
        Core.convertScaleAbs(grady, grady);

        // X梯度与Y梯度合并得到新的边缘Mat

        Mat contour = new Mat();
        Core.addWeighted(gradx, 0.5, grady, 0.5, 0, contour);

        // 进行找轮廓操作，目前的图片已经足够简单；
        // 定义一个轮廓的集合；用于存取找到的轮廓；
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        // 定义一个Mat 用于 Top参数；
        // 不知道有什么用
        Mat Top = new Mat();

        // 使用findContours()这个函数找到所以的轮廓
        // contour资源图片，
        // contours轮廓集合
        // Top拓扑信息（不知道干什么用得）
        // Imgproc.RETR_EXTERNAL返回最外层轮廓
        // Imgproc.CHAIN_APPROX_SIMPLE链式编码方式

        Imgproc.findContours(contour, contours, Top, Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        // 感兴趣区域Rect
        Rect ROIArea = null;

        for (int i = 0; i < contours.size(); i++) {
            // 给每个轮廓绑定最小外接矩形
            Rect rect = Imgproc.boundingRect(contours.get(i));
            if (rect.width / 2 > 0.8 * rect.height &&
                    rect.width / 2 < 1.2 * rect.height &&
                    rect.width > hight / 3) {
                ROIArea = rect;
            }
        }

        // 如果一个都没有筛选到，这种情况几乎不可能发生；
        // 但是为了防止出现空指针异常写了如下：
        if (ROIArea == null) {
            ROIArea = new Rect(new Point(0, 0), GrayMat.size());
            Logger.e("laiyang666","图像修正Bunding失败！");
        }
        // 目前还存在问题就是截取的图像
        // 会存在截取过多或者过少的情况
        // 因为不可能拍到的屏幕完完全全是一个规则矩形
        // 得到感兴趣区域

        // 释放内存
        GrayMat.release();
        contour.release();
        Top.release();
        k.release();
        gradx.release();
        grady.release();

        return ROIArea;
    }

    /**
     * 用于修正的方法
     *
     * @param x,y 与标准的距离 上下不需要修正，x可以忽略；
     */
    public static void doCorrection(int x, int y) throws InterruptedException {
        // 这个方法写的不好 -- 车与屏幕的距离是动态的
        x = x / 300;
        y = y / 200;

        Log.d("laiyang666", x + "" + y + "");

        if (x >= 0 && y >= 0) {// 右上角 && 右边
            for (int i = 0; i < x; i++) {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_RIGHT);
                Thread.sleep(1000);
            }

            NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_RIGHT_STOP);
            Thread.sleep(1000);

            for (int i = 0; i < y; i++) {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_UP);
                Thread.sleep(1000);
            }

            NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_UP_STOP);
        } else if (x >= 0 && y <= 0) { // 右下角 && 右边
            for (int i = 0; i < x; i++) {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_RIGHT);
                Thread.sleep(1000);
            }
            NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_RIGHT_STOP);
            Thread.sleep(1000);
            for (int i = 0; i < -y; i++) {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_DOWN);
                Thread.sleep(1000);
            }

            NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_DOWN_STOP);
        }


        if (x <= 0 && y <= 0) { // 左下角 && 左边
            for (int i = 0; i < -x; i++) {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_LEFT);
                Thread.sleep(1000);
            }
            NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_LEFT_STOP);
            Thread.sleep(1000);
            for (int i = 0; i < -y; i++) {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_DOWN);
                Thread.sleep(1000);
            }

            NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_DOWN_STOP);
        } else if (x <= 0 && y >= 0) { // 左上角 && 左边
            for (int i = 0; i < -x; i++) {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_LEFT);
                Thread.sleep(1000);
            }

            NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_LEFT_STOP);
            Thread.sleep(1000);

            for (int i = 0; i < y; i++) {
                NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_UP);
                Thread.sleep(1000);
            }
            NativeCaller.PPPPPTZControl(SystemValue.deviceId, ContentCommon.CMD_PTZ_UP_STOP);
        }

    }

}
