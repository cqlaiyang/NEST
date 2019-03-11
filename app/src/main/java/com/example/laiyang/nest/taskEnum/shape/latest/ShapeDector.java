package com.example.laiyang.nest.taskEnum.shape.latest;

import android.util.Log;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

public class ShapeDector {

    /**
     * @param ShapeMatofPoint  图形轮廓，
     * @param ShapeMatof2Point 图形轮廓转Matof2Point；
     * @return
     */

    public String contourDetect(MatOfPoint ShapeMatofPoint, MatOfPoint2f ShapeMatof2Point) {
        // 识别结果
        String shape = "";
        // 轮廓逼近程度，越小逼近越厉害
        double epsilon = 0.04D * Imgproc.arcLength(ShapeMatof2Point, true);
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        // 轮廓逼近
        Imgproc.approxPolyDP(ShapeMatof2Point, approxCurve, epsilon, true);
        // 角点检测
        int shapeLen = approxCurve.toArray().length;

        Log.d("laiyang666", "" + shapeLen);
        switch (shapeLen) {
            case 2: {
                // 不完整边缘干扰
                break;
            }
            case 3: {
                // 三个角点也有可能是菱形
                RotatedRect rect = Imgproc.minAreaRect(ShapeMatof2Point);

                // 长宽比值，也有可能为宽比长
                // 这里用得是最小外接矩形；不能用buningRect;
                double RectArea = rect.size.area();
                double ShapeArea = Imgproc.contourArea(ShapeMatofPoint, false);

                if (RectArea >= 0.9D * ShapeArea && RectArea <= 1.1D * ShapeArea) {
                    shape = "rhombus";
                } else {
                    shape = "triangle";
                }
                break;
            }
            // 角点为4个，还细分为菱形，正方形，矩形
            case 4: {
                // 首先判断为矩形还是正方形或者菱形
                RotatedRect rect = Imgproc.minAreaRect(ShapeMatof2Point);
                float width = (float) rect.size.width;
                float heigt = (float) rect.size.height;

                // 长宽比值，也有可能为宽比长
                // 这里用得是最小外接矩形；不能用buningRect;
                float specific = width / heigt;
                double RectArea = rect.size.area();
                double ShapeArea = Imgproc.contourArea(ShapeMatofPoint, false);
                if ((double) specific >= 0.8D && (double) specific <= 1.2D) {
                    shape = "square";
                } else if (RectArea >= 0.9D * ShapeArea && RectArea <= 1.1D * ShapeArea) {
                    shape = "rectangle";
                } else {
                    shape = "rhombus";
                }
                break;
            }
            case 5: {
                //  bund 最小外接矩形
                RotatedRect rect = Imgproc.minAreaRect(ShapeMatof2Point);

                // 这里用得是最小外接矩形；不能用buningRect;
                double RectArea = rect.size.area();
                double ShapeArea = Imgproc.contourArea(ShapeMatofPoint, false);
                if (ShapeArea / RectArea < 0.75) {
                    shape = "stars";
                } else {
                    shape = "circle";
                }
                break;
            }
            case 8: {
                //  bund 最小外接矩形
                RotatedRect rect = Imgproc.minAreaRect(ShapeMatof2Point);

                // 这里用得是最小外接矩形；不能用buningRect;
                double RectArea = rect.size.area();
                double ShapeArea = Imgproc.contourArea(ShapeMatofPoint, false);
                if (ShapeArea / RectArea < 0.75) {
                    shape = "stars";
                } else {
                    shape = "circle";
                }
                break;
            }
            case 9: {
                //  bund 最小外接矩形
                RotatedRect rect = Imgproc.minAreaRect(ShapeMatof2Point);

                // 这里用得是最小外接矩形；不能用buningRect;
                double RectArea = rect.size.area();
                double ShapeArea = Imgproc.contourArea(ShapeMatofPoint, false);
                if (ShapeArea / RectArea < 0.75) {
                    shape = "stars";
                } else {
                    shape = "circle";
                }
                break;
            }
            case 10: {
                // 大五角星
                shape = "stars";
                break;
            }
            default: {
                shape = "circle";
                break;
            }
        }
        return shape;
    }


}
