package com.example.laiyang.nest.taskEnum.shape.latest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.laiyang.nest.taskEnum.shape.ShapeUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shape {
    static List<Map<String,Integer>> mapList;
    public static List<Map<String,Integer>> ShapeRecognition(Bitmap shapePic) {

        Mat Srcmat = new Mat();
        Utils.bitmapToMat(shapePic, Srcmat);
        // 裁剪图片得到ROI区域；
        Mat ROIMat = new Mat();
        try {
            ROIMat = ShapeUtils.GetROI(Srcmat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Imgproc.cvtColor(ROIMat,ROIMat,Imgproc.COLOR_RGBA2RGB);
        Mat HSVdst = new Mat();

        // 中值滤波(不一定采用中值滤波方式)
        Imgproc.medianBlur(ROIMat,HSVdst,7);

        // 转化为HSV色彩空间
        Imgproc.cvtColor(HSVdst,HSVdst,Imgproc.COLOR_RGB2HSV);


        // 首先列出颜色
        Scalar whiteMax = new Scalar(180,110,255);
        Scalar whiteMin = new Scalar(0,0,80);

        Scalar redMax = new Scalar(180,255,256);
        Scalar redMin = new Scalar(156,43,0);

        Scalar greenMax = new Scalar(64,255,255);
        Scalar greenMin = new Scalar(44,50,50);

        Scalar blueMax = new Scalar(120,256,255);
        Scalar blueMin = new Scalar(106,43,66);

        Scalar yellowMax = new Scalar(43,255,255);
        Scalar yellowMin = new Scalar(25,35,46);

        Scalar pinkMax = new Scalar(155,255,255);
        Scalar pinkMin = new Scalar(125,90,46);

        Scalar cyanMax = new Scalar(100,255,255);
        Scalar cyanMin = new Scalar(80,150,200);

        Scalar blackMax = new Scalar(180,255,80);
        Scalar blackMin = new Scalar(0,0,0);

        // 放置颜色大小的容器
        List<Scalar> ColorValue = new ArrayList<Scalar>();
        ColorValue.add(whiteMin);ColorValue.add(whiteMax);
        ColorValue.add(redMin);ColorValue.add(redMax);
        ColorValue.add(greenMin);ColorValue.add(greenMax);
        ColorValue.add(blueMin);ColorValue.add(blueMax);
        ColorValue.add(yellowMin);ColorValue.add(yellowMax);
        ColorValue.add(pinkMin);ColorValue.add(pinkMax);
        ColorValue.add(cyanMin);ColorValue.add(cyanMax);
        ColorValue.add(blackMin);ColorValue.add(blackMax);


        // 列出通道数，长宽信息
        int []info = new int[]{HSVdst.channels(),HSVdst.width(),HSVdst.height()};
        Log.d("laiyang666","channels" + info[0] + "width" + info[1] + "height" + info[2] );

        // 颜色筛选后的容器
        Mat whiteMat = new Mat();
        whiteMat.create(ROIMat.size(),ROIMat.type());
        Mat redMat = new Mat();
        redMat.create(ROIMat.size(),ROIMat.type());
        Mat greenMat = new Mat();
        greenMat.create(ROIMat.size(),ROIMat.type());
        Mat blueMat = new Mat();
        blueMat.create(ROIMat.size(),ROIMat.type());
        Mat yellowMat = new Mat();
        yellowMat.create(ROIMat.size(),ROIMat.type());
        Mat pinkMat = new Mat();
        pinkMat.create(ROIMat.size(),ROIMat.type());
        Mat cyanMat = new Mat();
        cyanMat.create(ROIMat.size(),ROIMat.type());
        Mat blackMat = new Mat();
        blackMat.create(ROIMat.size(),ROIMat.type());

        // 找到在范围内的颜色轮廓
        Core.inRange(HSVdst,ColorValue.get(0),ColorValue.get(1),whiteMat);
        Core.inRange(HSVdst,ColorValue.get(2),ColorValue.get(3),redMat);
        Core.inRange(HSVdst,ColorValue.get(4),ColorValue.get(5),greenMat);
        Core.inRange(HSVdst,ColorValue.get(6),ColorValue.get(7),blueMat);
        Core.inRange(HSVdst,ColorValue.get(8),ColorValue.get(9),yellowMat);
        Core.inRange(HSVdst,ColorValue.get(10),ColorValue.get(11),pinkMat);
        Core.inRange(HSVdst,ColorValue.get(12),ColorValue.get(13),cyanMat);
        Core.inRange(HSVdst,ColorValue.get(14),ColorValue.get(15),blackMat);

        // 定义算子
        Mat kMin = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(6,6),new Point(3,3));
        Mat kMax = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(12,12),new Point(6,6));

        // 形态学开操作(部分进行)
        Imgproc.morphologyEx(whiteMat,whiteMat,Imgproc.MORPH_OPEN,kMin);
        Imgproc.morphologyEx(redMat,redMat,Imgproc.MORPH_OPEN,kMin);
        Imgproc.morphologyEx(greenMat,greenMat,Imgproc.MORPH_OPEN,kMin);
        Imgproc.morphologyEx(blueMat,blueMat,Imgproc.MORPH_OPEN,kMin);
        Imgproc.morphologyEx(yellowMat,yellowMat,Imgproc.MORPH_OPEN,kMin);
        Imgproc.morphologyEx(pinkMat,pinkMat,Imgproc.MORPH_OPEN,kMin);
        Imgproc.morphologyEx(cyanMat,cyanMat,Imgproc.MORPH_OPEN,kMin);
        Imgproc.morphologyEx(blackMat,blackMat,Imgproc.MORPH_OPEN,kMin);

        Rect rect = new Rect(30,30,blackMat.width() - 60 ,blackMat.height() -60);
       // blackMat = blackMat.submat(rect);

        // 定义容器
        List<Mat> binaryMat = new ArrayList<Mat>();

        Map<String,Integer> white = new HashMap<>();
        white.put("triangle",0);
        white.put("square",0);
        white.put("rectangle",0);
        white.put("rhombus",0);
        white.put("stars",0);
        white.put("circle",0);
        Map<String,Integer> red = new HashMap<>();
        red.put("triangle",0);
        red.put("square",0);
        red.put("rectangle",0);
        red.put("rhombus",0);
        red.put("stars",0);
        red.put("circle",0);
        Map<String,Integer> green = new HashMap<>();
        green.put("triangle",0);
        green.put("square",0);
        green.put("rectangle",0);
        green.put("rhombus",0);
        green.put("stars",0);
        green.put("circle",0);
        Map<String,Integer> blue = new HashMap<>();
        blue.put("triangle",0);
        blue.put("square",0);
        blue.put("rectangle",0);
        blue.put("rhombus",0);
        blue.put("stars",0);
        blue.put("circle",0);
        Map<String,Integer> yellow = new HashMap<>();
        yellow.put("triangle",0);
        yellow.put("square",0);
        yellow.put("rectangle",0);
        yellow.put("rhombus",0);
        yellow.put("stars",0);
        yellow.put("circle",0);
        Map<String,Integer> pink = new HashMap<>();
        pink.put("triangle",0);
        pink.put("square",0);
        pink.put("rectangle",0);
        pink.put("rhombus",0);
        pink.put("stars",0);
        pink.put("circle",0);
        Map<String,Integer> cyan= new HashMap<>();
        cyan.put("triangle",0);
        cyan.put("square",0);
        cyan.put("rectangle",0);
        cyan.put("rhombus",0);
        cyan.put("stars",0);
        cyan.put("circle",0);
        Map<String,Integer> black= new HashMap<>();
        black.put("triangle",0);
        black.put("square",0);
        black.put("rectangle",0);
        black.put("rhombus",0);
        black.put("stars",0);
        black.put("circle",0);

        mapList = new ArrayList<>();
        //  mapList.add(white);
        mapList.add(red);
        mapList.add(green);
        mapList.add(blue);
        mapList.add(yellow);
        mapList.add(pink);
        mapList.add(cyan);
        mapList.add(black);

        // 反向，用于接下来的轮廓提取
        // Imgproc.threshold(whiteMat,whiteMat,127,255,Imgproc.THRESH_BINARY_INV|Imgproc.THRESH_OTSU); binaryMat.add(whiteMat);
        Imgproc.threshold(redMat,redMat,127,255,Imgproc.THRESH_BINARY_INV|Imgproc.THRESH_OTSU);binaryMat.add(redMat);
        Imgproc.threshold(greenMat,greenMat,127,255,Imgproc.THRESH_BINARY_INV|Imgproc.THRESH_OTSU);binaryMat.add(greenMat);
        Imgproc.threshold(blueMat,blueMat,127,255,Imgproc.THRESH_BINARY_INV|Imgproc.THRESH_OTSU);binaryMat.add(blueMat);
        Imgproc.threshold(yellowMat,yellowMat,127,255,Imgproc.THRESH_BINARY_INV|Imgproc.THRESH_OTSU);binaryMat.add(yellowMat);
        Imgproc.threshold(pinkMat,pinkMat,127,255,Imgproc.THRESH_BINARY_INV|Imgproc.THRESH_OTSU);binaryMat.add(pinkMat);
        Imgproc.threshold(cyanMat,cyanMat,127,255,Imgproc.THRESH_BINARY_INV|Imgproc.THRESH_OTSU);binaryMat.add(cyanMat);
        Imgproc.threshold(blackMat,blackMat,127,255,Imgproc.THRESH_BINARY_INV|Imgproc.THRESH_OTSU);binaryMat.add(blackMat);

        Bitmap dbit1 = Bitmap.createBitmap(ROIMat.width(),ROIMat.height(),Bitmap.Config.RGB_565);
        Bitmap dbit2 = Bitmap.createBitmap(ROIMat.width(),ROIMat.height(),Bitmap.Config.RGB_565);
        Bitmap dbit3 = Bitmap.createBitmap(ROIMat.width(),ROIMat.height(),Bitmap.Config.RGB_565);
        Bitmap dbit4 = Bitmap.createBitmap(ROIMat.width(),ROIMat.height(),Bitmap.Config.RGB_565);
        Bitmap dbit5 = Bitmap.createBitmap(ROIMat.width(),ROIMat.height(),Bitmap.Config.RGB_565);
        Bitmap dbit6 = Bitmap.createBitmap(ROIMat.width(),ROIMat.height(),Bitmap.Config.RGB_565);
        Bitmap dbit7 = Bitmap.createBitmap(blackMat.width(),blackMat.height(),Bitmap.Config.RGB_565);

        Utils.matToBitmap(redMat,dbit1);
        Utils.matToBitmap(greenMat,dbit2);
        Utils.matToBitmap(blueMat,dbit3);
        Utils.matToBitmap(yellowMat,dbit4);
        Utils.matToBitmap(pinkMat,dbit5);
        Utils.matToBitmap(cyanMat,dbit6);
        Utils.matToBitmap(blackMat,dbit7);

        // 轮廓查找
        // 定义容器，所有轮廓的集合
        List<MatOfPoint> Whole = new ArrayList<MatOfPoint>();

        // Top
        Mat top = new Mat();

        // 循环，查找binaryMat中每个分色抠图的轮廓形状
        // 底色为白色，所以没有白色
        for (int i = 0; i < binaryMat.size(); i++) {
            Whole.clear();
            // 找到该颜色轮廓的点集
            Imgproc.findContours(binaryMat.get(i),Whole,top,Imgproc.RETR_TREE,
                    Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));
            // 该颜色的点集剔除其它干扰识别的点集
            for (int cnt = 0; cnt < Whole.size();cnt++){
                MatOfPoint point = Whole.get(cnt);
                double lenth = Imgproc.arcLength(new MatOfPoint2f(point.toArray()),false);
                double area = Imgproc.contourArea(point,false);
                boolean is = Imgproc.isContourConvex(point);
                // 打印该点集的长度和面积信息
                Log.d("laiyang666","" + lenth + "-" + area + "-" + is + "-" + Imgproc.contourArea(point));
                // 移除多余的
                if (area > 50000){
                    Whole.remove(cnt);
                    cnt = -1;
                }else
                if (area < 800){
                    Whole.remove(cnt);
                    // -1+1 = 0！
                    cnt = -1;
                }else if (lenth > 1200){
                    Whole.remove(cnt);
                    cnt  = -1;
                }
            }

            // 形状判断
            ShapeDector shapeDector = new ShapeDector();
            for (int cont = 0; cont < Whole.size(); cont ++) {
                MatOfPoint point = Whole.get(cont);
                MatOfPoint2f matOfPoint2f = new MatOfPoint2f(point.toArray());

                String s = shapeDector.contourDetect(point,matOfPoint2f);
                if (s.isEmpty()){
                }else {
                    Log.d("laiyang666","Shape:" + s + "-" + i);
                    switch (i){
                        case 0:{
                            mapList.get(i).put(s,mapList.get(i).get(s) + 1);
                            break;
                        }
                        case 1:{
                            mapList.get(i).put(s,mapList.get(i).get(s) + 1);
                            break;
                        }
                        case 2:{
                            mapList.get(i).put(s,mapList.get(i).get(s) + 1);
                            break;
                        }
                        case 3:{
                            mapList.get(i).put(s,mapList.get(i).get(s) + 1);
                            break;
                        }
                        case 4:{
                            mapList.get(i).put(s,mapList.get(i).get(s) + 1);
                            break;
                        }
                        case 5:{
                            mapList.get(i).put(s,mapList.get(i).get(s) + 1);
                            break;
                        }
                        case 6:{
                            mapList.get(i).put(s,mapList.get(i).get(s) + 1);
                            break;
                        }
                        case 7:{
                            mapList.get(i).put(s,mapList.get(i).get(s) + 1);
                            break;
                        }

                    }
                }

            }

        }
        return mapList;
    }
}
