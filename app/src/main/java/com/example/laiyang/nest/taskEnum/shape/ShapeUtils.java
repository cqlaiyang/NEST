package com.example.laiyang.nest.taskEnum.shape;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.laiyang.nest.camera.utils.SystemValue;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ShapeUtils {

    /**
     * @param Srcmat 传入带TFT标志物的图片片
     * @return 返回一个TFT显示屏幕区域
     */
    public static Mat GetROI(Mat Srcmat) throws Exception {
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
            Log.e("laiyang666", "没有找到图像");
            throw new Exception("没有找到屏幕区域！");
        }
        // 目前还存在问题就是截取的图像
        // 会存在截取过多或者过少的情况
        // 因为不可能拍到的屏幕完完全全是一个规则矩形
        // 得到感兴趣区域
        Mat ROIMat = Srcmat.submat(ROIArea);

        // 释放内存
        GrayMat.release();
        contour.release();
        Top.release();
        k.release();
        gradx.release();
        grady.release();

        return ROIMat;
    }

    /**
     * @param ROIMat 传入TFT显示标志物屏幕区域的Mat
     * @return 返回一个图像的链表链表
     */
    public static List GetShapeContours(Mat ROIMat) {

        Mat GrayMat = new Mat();

        // 转为灰度图
        Imgproc.cvtColor(ROIMat, GrayMat, Imgproc.COLOR_RGB2GRAY);

        // 计算X方向的梯度值
        Mat gradx = new Mat();
        Imgproc.Sobel(GrayMat, gradx, CvType.CV_32F, 1, 0);
        Core.convertScaleAbs(gradx, gradx);

        // 计算Y方向的梯度值
        Mat grady = new Mat();
        Imgproc.Sobel(GrayMat, grady, CvType.CV_32F, 0, 1);
        Core.convertScaleAbs(grady, grady);

        // 将X方向与Y方向的值合成
        Mat ROIContours = new Mat();
        Core.addWeighted(gradx, 0.5, grady, 0.5, 0, ROIContours);

        // 现在的轮廓颜色比较暗淡
        // 需要进行提亮炒作；
        byte[] Pixl = new byte[ROIContours.channels() * ROIContours.cols() * ROIContours.rows()];
        ROIContours.get(0, 0, Pixl);

        int pv = 0;
        for (int i = 0; i < Pixl.length; i++) {
            pv = Pixl[i] & 0xff;
            if (pv > 100) {
                Pixl[i] = (byte) 0;
            } else {
                Pixl[i] = (byte) 255;
            }
        }
        ROIContours.put(0, 0, Pixl);

        // 现在得到一个明显的轮廓图
        // 但是它包含了TFT边框杂七杂八的轮廓；所以我们要进行：“筛选”，“剔除”；
        // 得到外边框和全部      边框，然后挨个把外边款剔除，还有比较小的小轮廓

        // 这个是完整的轮廓链表
        List<MatOfPoint> Whole = new ArrayList<MatOfPoint>();

        // 这个是外边框轮廓链表
        List<MatOfPoint> margin = new ArrayList<MatOfPoint>();

        // 拓扑参数
        Mat Top = new Mat();

        // 找到所有轮廓
        Imgproc.findContours(ROIContours, Whole, Top, Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        // 外边框轮廓
        Imgproc.findContours(ROIContours, margin, Top, Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        // 剔除外边框干扰判断的轮廓(通过判断轮廓长度)
        for (int cnt = 0; cnt < margin.size(); cnt++) {
            MatOfPoint marginPoint = margin.get(cnt);
            double marginLenth = Imgproc.arcLength(new MatOfPoint2f(marginPoint.toArray()), false);

            for (int cont = 0; cont < Whole.size(); cont++) {
                MatOfPoint wholePoint = Whole.get(cont);
                double wholeLenth = Imgproc.arcLength(new MatOfPoint2f(wholePoint.toArray()), false);
                double Area = Imgproc.contourArea(wholePoint,false);
                if (wholeLenth == marginLenth) {
                    Whole.remove(cont);
                } else {
                    if (Area < 2000){
                        Whole.remove(cont);
                        cont = -1;
                    }

                    // 这一个过滤是过滤外边框中闭合的轮廓
                    // 面积也和待识别的基本接近
                    // 但是他的轮廓是围着边框一圈
                    if (wholeLenth > 1000) {
                        Whole.remove(cont);
                        cont = -1; // = -1 ?  (-1 ++) = 0;
                    }
                }

            }

        }


        Bitmap DebugBitmap = Bitmap.createBitmap(ROIMat.cols(),ROIMat.rows(),Bitmap.Config.RGB_565);
        Mat dst = new Mat();
        dst.create(ROIMat.size(),ROIMat.type());
        dst.setTo(new Scalar(255,255,255));

        for (int i = 0; i < Whole.size();i++) {
            Imgproc.drawContours(dst,Whole,i,new Scalar(0,0,255),1);
            Utils.matToBitmap(dst,DebugBitmap);
        }

        for (int i = 0; i < Whole.size(); i++) {
            MatOfPoint wholePoint = Whole.get(i);
            double Area = Imgproc.contourArea(wholePoint,false);
            double Lenth = Imgproc.arcLength(new MatOfPoint2f(wholePoint.toArray()),false);
            Log.d("laiyang666","" + Area + "----" + Lenth);
        }
        // 现在还存在内轮廓与外轮廓
        // 我选择剔除外轮廓，
        // 因为外内轮廓会让颜色识别不会被边缘色彩不正的干扰；
        // 而且我选择反向剔除，他能保证剔除的顺序（在还存在干扰轮廓，或者干扰轮廓没有剔除完全的情况下）
        // 内外轮廓的顺序是：外 ：内 ：外 ：内 ：外 ：内

        Log.d("laiyang666", "Whole Size:" + Whole.size());
        for (int cnt = Whole.size() - 2; cnt >= 0; cnt = cnt- 2) {
            Whole.remove(cnt);
        }
        return Whole;
    }
}
