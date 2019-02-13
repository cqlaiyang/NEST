package com.example.laiyang.nest.taskEnum.shape.older;

import android.util.Log;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class ShapeDector {

    /**
     *
     * @param ShapeMatofPoint 图形轮廓，
     * @param ShapeMatof2Point 图形轮廓转Matof2Point；
     * @return
     */

    public String contourDetect(MatOfPoint ShapeMatofPoint, MatOfPoint2f ShapeMatof2Point){
        // 识别结果
        String shape = "";
        // 轮廓逼近程度，越小逼近越厉害
        double epsilon = 0.04D * Imgproc.arcLength(ShapeMatof2Point,true);
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        // 轮廓逼近
        Imgproc.approxPolyDP(ShapeMatof2Point,approxCurve,epsilon,true);
        // 角点检测
        int shapeLen = approxCurve.toArray().length;

        Log.d("laiyang666","" + shapeLen);
        switch (shapeLen){
            case 3:{
                shape = "triangle";
                break;
            }
            // 角点为4个，还细分为菱形，正方形，矩形
            case 4:{
                // 首先判断为矩形还是正方形或者菱形
                RotatedRect rect = Imgproc.minAreaRect(ShapeMatof2Point);
                float width = (float)rect.size.width;
                float heigt = (float)rect.size.height;

                // 长宽比值，也有可能为宽比长
                // 这里用得是最小外接矩形；不能用buningRect;
                float specific = width/heigt;
                double RectArea = rect.size.area();
                double ShapeArea = Imgproc.contourArea(ShapeMatofPoint,false);
                if ((double) specific >= 0.8D && (double) specific <= 1.2D) {
                    shape = "square";
                }else if (RectArea >= 0.9D * ShapeArea && RectArea <= 1.1D * ShapeArea){
                    shape = "rectangle";
                }else {
                    shape = "rhombus";
                }
                break;
            }
            case 10:{
                shape = "star";
                break;
            }
            default:{
                shape = "circle";
                break;
            }
        }
        return shape;
    }

    public String colorDetect(Mat ROImat,List contours,int Index){
        // 原图长宽
        int width = ROImat.cols();
        int hight = ROImat.rows();

        // 识别结果
        String color = "unKnow";

        // 首先我们要建立颜色参考表；
        // 写死颜色数据；
        double[]White = new double[]{255 * 0.85 , 127 -17 , 127 - 16};
        double[]Red = new double[]{255 * 0.29 , 127 + 52 , 127 + 32};
        double[]Green = new double[]{255 * 88 , 255 - 75 , 127 + 68};
        double[]Blue = new double[]{255 * 0.30 , 127 + 54 , 127 - 99 };
        double[]Yellow = new double[]{255 * 0.95 , 255 - 33 , 127 + 67};
        // 品色 但是看起来像粉红，国赛看起来像紫色
        double[]Pink = new double[]{255 * 0.38 , 127 + 69 ,127 - 87 };
        double[]Cyan = new double[]{255 * 0.87 , 127 - 43 , 127 - 21};
        double[]Black = new double[]{255 * 0.05, 127 - 8 , 127 + 3};

        // 现在建立一个mask用于指明求平均值区域；
        // 并且初始化为全零矩阵
        Mat mask = Mat.zeros(ROImat.size(),CvType.CV_8UC1);

        // 其中第一个mask为目标图像；
        // contours为轮廓集合
        // Index画出那个轮廓
        // Scalar颜色
        // -1表示为填充该轮廓为该颜色；也可以使用CV_FILLED
        Imgproc.drawContours(mask,contours,Index,new Scalar(255,0,0),-1);

        // 接下来计算平均值；输入2个Mat对象，
        // 一个代求平均值的Mat，一个指明范围(255，0，0)区域才进行求平均值；
        Scalar meanScalar = Core.mean(ROImat,mask);

        // 将颜色空间转化为数组进行比较；
        double[] scalarArray = new double[]{
                meanScalar.val[0],
                meanScalar.val[1],
                meanScalar.val[2]
        };

        // 计算欧氏距离,因为他在一个类似XYZ轴的色彩空间中；
        // 可以通过计算欧式距离来验证他们与参考色的相关性；
        EuclideanDistance euclideanDistance = new EuclideanDistance();
        double WhiteDistance = euclideanDistance.compute(scalarArray,White);
        double RedDistance = euclideanDistance.compute(scalarArray,Red);
        double GreenDistance = euclideanDistance.compute(scalarArray,Green);
        double BlueDistance = euclideanDistance.compute(scalarArray,Blue);
        double YellowDistance = euclideanDistance.compute(scalarArray,Yellow);
        double PinkDistance = euclideanDistance.compute(scalarArray,Pink);
        double CyanDistance = euclideanDistance.compute(scalarArray,Cyan);
        double BlackDistance = euclideanDistance.compute(scalarArray,Black);

        Log.d("laiyang666","White:"+ WhiteDistance +"Red" +
                RedDistance + "Green" + GreenDistance +
                "Blue" + BlueDistance);
        Log.d("laiyang666", "Yellow" + YellowDistance +
                "pink" + PinkDistance + "Cyan" + CyanDistance+
                "Black" + BlackDistance);
        // 得出结论，标准差越小，相关性越大
        if (WhiteDistance < RedDistance && WhiteDistance < GreenDistance &&
                WhiteDistance < BlueDistance && WhiteDistance < YellowDistance &&
                WhiteDistance < PinkDistance && WhiteDistance < CyanDistance &&
                WhiteDistance < BlackDistance){
            color = "white";
        }else if (RedDistance < GreenDistance && RedDistance < BlueDistance &&
                RedDistance < YellowDistance && RedDistance < PinkDistance &&
                RedDistance < CyanDistance && RedDistance < CyanDistance &&
                RedDistance < BlackDistance){
            color = "red";
        } else if (GreenDistance < BlueDistance && GreenDistance < YellowDistance &&
                GreenDistance < PinkDistance && GreenDistance < CyanDistance &&
                GreenDistance < BlackDistance) {
            color = "green";
        } else if (BlueDistance < YellowDistance && BlueDistance < PinkDistance &&
                BlueDistance < PinkDistance && BlueDistance < CyanDistance &&
                BlueDistance < BlackDistance) {
            color = "blue";
        }else if (YellowDistance < PinkDistance && YellowDistance < CyanDistance &&
                YellowDistance < BlackDistance){
            color  = "yellow";

        }else if (PinkDistance < CyanDistance && PinkDistance < BlackDistance){
            color = "pink";
        }else if (CyanDistance < BlackDistance){
            color = "cyan";
        }else {
            color = "black";
        }
        return color;
    }
}
