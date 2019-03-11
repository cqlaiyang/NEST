package com.example.laiyang.nest.taskEnum.shape.latest;


import android.util.Log;

import java.util.List;
import java.util.Map;

public class ShapeStatistics {
    // List索引0.red 1.green,2blue,3,yellow,4,pink,5.cyan,6.black
    public static String first(List<Map<String, Integer>> matList) {
        int rectangle = 0;
        int circle = 0;
        int triangle = 0;
        int BlueTriangle = 0;
        int YellowTriangle = 0;
        int rhombus = 0;
        int stars = 0;
        for (int i = 0; i < matList.size(); i++) {
            for (String shape : matList.get(i).keySet()) {
                int count = matList.get(i).get(shape);
                if (count > 0) {
                    Log.d("laiyang66", shape + "-" + count);
                    if (shape.equals("square") || shape.equals("rectangle")) {
                         rectangle+= count;
                    } else if (shape.equals("circle")) {
                        circle += count;
                    } else if (shape.equals("triangle")) {
                        triangle += count;
                        if (i == 1){
                            BlueTriangle += count;
                        }else if (i == 2){
                            YellowTriangle += count;
                        }

                    } else if (shape.equals("stars")) {
                        stars += count;
                    } else if (shape.equals("rhombus")) {
                        rhombus += count;
                    }
                }
            }
        }
        return "" + rectangle +"" +  "" + triangle + "" + "" + stars + "" + (BlueTriangle + YellowTriangle);
    }

    public static String getColor(List<Map<String, Integer>> mapList) {
        int white = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        int yellow = 0;
        int pink = 0;
        int cyan = 0;
        int black = 0;
        for (int i = 0; i < mapList.size(); i++) {
            for (String shape : mapList.get(i).keySet()) {
                int num = mapList.get(i).get(shape);
                if (num > 0) {
                    if (i == 0) { //红色
                        red += num;
                    } else if (i == 1) { // 绿色
                        green += num;
                    } else if (i == 2) { //蓝色
                        blue += num;
                    } else if (i == 3) { //黄色
                        yellow += num;
                    } else if (i == 4) { //粉色
                        pink += num;
                    } else if (i == 5) { // 青色
                        cyan += num;
                    } else if (i == 6) { //黑色
                        black +=0;
                    } else if (i == 7) {

                    }
                }
            }
        }

        return red + "" + green +"" + blue +"" + yellow + "" +pink + "" +cyan + "" + black;
    }


}
