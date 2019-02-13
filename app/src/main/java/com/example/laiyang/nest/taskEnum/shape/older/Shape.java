package com.example.laiyang.nest.taskEnum.shape.older;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.laiyang.nest.taskEnum.shape.ShapeUtils;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shape {
    // 最后的结果统计
    private static Map<String, Integer> shapeResult = new HashMap<String, Integer>();

    public static Map ShapeRecognition(Bitmap shapePic) {

        Bitmap DebugBitMap;

        Mat Srcmat = new Mat();
        Utils.bitmapToMat(shapePic, Srcmat);

        // 裁剪图片得到ROI区域；
        Mat ROIMat = new Mat();
        try {
            ROIMat = ShapeUtils.GetROI(Srcmat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("laiyang666", "你是猪吗？");
        // 对ROI区域进行轮廓提取与筛选得到的是一个只含内轮廓的链表
        List<MatOfPoint> ShapeContours = new ArrayList<MatOfPoint>();

        // TODO：警告：Unchecked assignment: 'java.util.List' to 'java.util.List<org.opencv.core.MatOfPoint>
        ShapeContours = ShapeUtils.GetShapeContours(ROIMat);

        // 转换为Lab色彩空间；应为Lab色彩空间是一个三维的色彩空间
        // L为亮度(0 ~ 100)，a为绿色到洋红(-127 ~ +127),表示蓝色到黄色(-127 ~ +127);
        // 在这个色彩空间里面计算欧式距离更明显效果也更好；
        Mat labImg = ROIMat.clone();
        Log.d("laiyang666", "Mat" + ROIMat.channels());
        Imgproc.cvtColor(ROIMat, labImg, Imgproc.COLOR_RGB2Lab);
        ShapeDector shapeDector = new ShapeDector();
        init();

        for (int i = 0; i < ShapeContours.size(); i++) {

            String shape;
            String color;

            MatOfPoint2f matOfPoint2f = new MatOfPoint2f(ShapeContours.get(i).toArray());
            shape = shapeDector.contourDetect(ShapeContours.get(i), matOfPoint2f);
            color = shapeDector.colorDetect(labImg, ShapeContours, i);
            Log.d("laiyang666", "???" + shape + "-" + color + Imgproc.contourArea(ShapeContours.get(i), false));

            // 我这样写只是便于debug；
            // 完全可以这样写
            // shapeResult.put(shape + color, shapeResult.get(shape + color) + 1);

            switch (shape) {
                case "triangle": {
                    switch (color) {
                        case "white":{
                            shapeResult.put("whiteTriangle", shapeResult.get("whiteTriangle") + 1);
                            break;
                        }
                        case "red":{
                            shapeResult.put("redTriangle", shapeResult.get("redTriangle") + 1);
                            break;
                        }
                        case "green":{
                            shapeResult.put("greenTriangle", shapeResult.get("greenTriangle") + 1);
                            break;
                        }
                        case "blue":{
                            shapeResult.put("blueTriangle", shapeResult.get("blueTriangle") + 1);
                            break;
                        }
                        case "yellow":{
                            shapeResult.put("yellowTriangle", shapeResult.get("yellowTriangle") + 1);
                            break;
                        }
                        case "pink":{
                            shapeResult.put("pinkTriangle", shapeResult.get("pinkTriangle") + 1);
                            break;
                        }
                        case "cyan":{
                            shapeResult.put("cyanTriangle", shapeResult.get("cyanTriangle") + 1);
                            break;
                        }
                        case "black":{
                            shapeResult.put("blackTriangle", shapeResult.get("blackTriangle") + 1);
                            break;
                        }
                        default:{
                            break;
                        }
                    }
                    break;

                }
                case "square": {
                    switch (color) {
                        case "white": {
                            shapeResult.put("whiteSquare", shapeResult.get("whiteSquare") + 1);
                            break;
                        }
                        case "red": {
                            shapeResult.put("redSquare", shapeResult.get("redSquare") + 1);
                            break;
                        }
                        case "green": {
                            shapeResult.put("greenSquare", shapeResult.get("greenSquare") + 1);
                            break;
                        }
                        case "blue": {
                            shapeResult.put("blueSquare", shapeResult.get("blueSquare") + 1);
                            break;
                        }
                        case "yellow": {
                            shapeResult.put("yellowSquare", shapeResult.get("yellowSquare") + 1);
                            break;
                        }
                        case "pink": {
                            shapeResult.put("pinkSquare", shapeResult.get("pinkSquare") + 1);
                            break;
                        }
                        case "cyan": {
                            shapeResult.put("cyanSquare", shapeResult.get("cyanSquare") + 1);
                            break;
                        }
                        case "black": {
                            shapeResult.put("blackSquare", shapeResult.get("blackSquare") + 1);
                            break;
                        }
                        default: {
                            // throw new Exception("错误！");
                        }
                    }
                    break;
                }
                case "rectangle": {
                    switch (color) {
                        case "white": {
                            shapeResult.put("whiteRectangle", shapeResult.get("whiteRectangle") + 1);
                            break;
                        }
                        case "red": {
                            shapeResult.put("redRectangle", shapeResult.get("redRectangle") + 1);
                            break;
                        }
                        case "green": {
                            shapeResult.put("greenRectangle", shapeResult.get("greenRectangle") + 1);
                            break;
                        }
                        case "blue": {
                            shapeResult.put("blueRectangle", shapeResult.get("blueRectangle") + 1);
                            break;
                        }
                        case "yellow": {
                            shapeResult.put("yellowRectangle", shapeResult.get("yellowRectangle") + 1);
                            break;
                        }
                        case "pink": {
                            shapeResult.put("pinkRectangle", shapeResult.get("pinkRectangle") + 1);
                            break;
                        }
                        case "cyan": {
                            shapeResult.put("cyanRectangle", shapeResult.get("cyanRectangle") + 1);
                            break;
                        }
                        case "black": {
                            shapeResult.put("blackRectangle", shapeResult.get("blackRectangle") + 1);
                            break;
                        }
                        default: {
                            // throw new Exception("错误！");
                        }
                    }
                    break;
                }
                case "rhombus": {
                    switch (color) {
                        case "white": {
                            shapeResult.put("whiteRhombus", shapeResult.get("whiteRhombus") + 1);
                            break;
                        }
                        case "red": {
                            shapeResult.put("redRhombus", shapeResult.get("redRhombus") + 1);
                            break;
                        }
                        case "green": {
                            shapeResult.put("greenRhombus", shapeResult.get("greenRhombus") + 1);
                            break;
                        }
                        case "blue": {
                            shapeResult.put("blueRhombus", shapeResult.get("blueRhombus") + 1);
                            break;
                        }
                        case "yellow": {
                            shapeResult.put("yellowRhombus", shapeResult.get("yellowRhombus") + 1);
                            break;
                        }
                        case "pink": {
                            shapeResult.put("pinkRhombus", shapeResult.get("pinkRhombus") + 1);
                            break;
                        }
                        case "cyan": {
                            shapeResult.put("cyanRhombus", shapeResult.get("cyanRhombus") + 1);
                            break;
                        }
                        case "black": {
                            shapeResult.put("blackRhombus", shapeResult.get("blackRhombus") + 1);
                            break;
                        }
                        default: {
                            // throw new Exception("错误！");
                        }
                    }
                    break;
                }
                case "star": {
                    switch (color) {
                        case "white": {
                            shapeResult.put("whiteStar", shapeResult.get("whiteStar") + 1);
                            break;
                        }
                        case "red": {
                            shapeResult.put("redStar", shapeResult.get("redStar") + 1);
                            break;
                        }
                        case "green": {
                            shapeResult.put("greenStar", shapeResult.get("greenStar") + 1);
                            break;
                        }
                        case "blue": {
                            shapeResult.put("blueStar", shapeResult.get("blueStar") + 1);
                            break;
                        }
                        case "yellow": {
                            shapeResult.put("yellowStar", shapeResult.get("yellowStar") + 1);
                            break;
                        }
                        case "pink": {
                            shapeResult.put("pinkStar", shapeResult.get("pinkStar") + 1);
                            break;
                        }
                        case "cyan": {
                            shapeResult.put("cyanStar", shapeResult.get("cyanStar") + 1);
                            break;
                        }
                        case "black": {
                            shapeResult.put("blackStar", shapeResult.get("blackStar") + 1);
                            break;
                        }
                        default: {
                            //  throw new Exception("错误！");
                        }
                    }
                    break;
                }
                case "circle": {
                    switch (color) {
                        case "white": {
                            shapeResult.put("whiteCircle", shapeResult.get("whiteCircle") + 1);
                            break;
                        }
                        case "red": {
                            shapeResult.put("redCircle", shapeResult.get("redCircle") + 1);
                            break;
                        }
                        case "green": {
                            shapeResult.put("greenCircle", shapeResult.get("greenCircle") + 1);
                            break;
                        }
                        case "blue": {
                            shapeResult.put("blueCircle", shapeResult.get("blueCircle") + 1);
                            break;
                        }
                        case "yellow": {
                            shapeResult.put("yellowCircle", shapeResult.get("yellowCircle") + 1);
                            break;
                        }
                        case "pink": {
                            shapeResult.put("pinkCircle", shapeResult.get("pinkCircle") + 1);
                            break;
                        }
                        case "cyan": {
                            shapeResult.put("cyanCircle", shapeResult.get("cyanCircle") + 1);
                            break;
                        }
                        case "black": {
                            shapeResult.put("blackCircle", shapeResult.get("blackCircle") + 1);
                            break;
                        }
                        default: {
                            //   throw new Exception("错误！");
                        }
                    }
                    break;
                }
                default: {
                    Log.d("laiyang666", "???" + shape + "-" + color);
                    //throw new Exception("错误！");
                }
            }

        }
        return shapeResult;
    }

    private static void init() {

        // 三角形
        shapeResult.put("whiteTriangle", 0);
        shapeResult.put("redTriangle", 0);
        shapeResult.put("greenTriangle", 0);
        shapeResult.put("yellowTriangle", 0);
        shapeResult.put("pinkTriangle", 0);
        shapeResult.put("cyanTriangle", 0);
        shapeResult.put("blueTriangle", 0);
        shapeResult.put("blackTriangle", 0);
        // 正方形
        shapeResult.put("whiteSquare", 0);
        shapeResult.put("redSquare", 0);
        shapeResult.put("greenSquare", 0);
        shapeResult.put("blueSquare", 0);
        shapeResult.put("yellowSquare", 0);
        shapeResult.put("pinkSquare", 0);
        shapeResult.put("cyanSquare", 0);
        shapeResult.put("blackSquare", 0);

        // 矩形
        shapeResult.put("whiteRectangle", 0);
        shapeResult.put("redRectangle", 0);
        shapeResult.put("greenRectangle", 0);
        shapeResult.put("blueRectangle", 0);
        shapeResult.put("yellowRectangle", 0);
        shapeResult.put("pinkRectangle", 0);
        shapeResult.put("cyanRectangle", 0);
        shapeResult.put("blackRectangle", 0);

        // 菱形
        shapeResult.put("whiteRhombus", 0);
        shapeResult.put("redRhombus", 0);
        shapeResult.put("greenRhombus", 0);
        shapeResult.put("blueRhombus", 0);
        shapeResult.put("yellowRhombus", 0);
        shapeResult.put("pinkRhombus", 0);
        shapeResult.put("cyanRhombus", 0);
        shapeResult.put("blackRhombus", 0);

        // 五角星
        shapeResult.put("whiteStar", 0);
        shapeResult.put("redStar", 0);
        shapeResult.put("greenStar", 0);
        shapeResult.put("blueStar", 0);
        shapeResult.put("yellowStar", 0);
        shapeResult.put("pinkStar", 0);
        shapeResult.put("cyanStar", 0);
        shapeResult.put("blackStar", 0);

        // 圆
        shapeResult.put("whiteCircle", 0);
        shapeResult.put("redCircle", 0);
        shapeResult.put("greenCircle", 0);
        shapeResult.put("blueCircle", 0);
        shapeResult.put("yellowCircle", 0);
        shapeResult.put("pinkCircle", 0);
        shapeResult.put("cyanCircle", 0);
        shapeResult.put("blackCircle", 0);


    }
}
