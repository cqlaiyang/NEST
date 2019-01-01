package com.example.laiyang.nest.taskEnum.shape;

import android.util.Log;

import java.util.Map;

public class ShapeStatistics {
    public static String Statistics(Map<String, Integer> map) {

        int triangleNum = map.get("whiteTriangle") + map.get("redTriangle") +
                map.get("greenTriangle") + map.get("blueTriangle") +
                map.get("yellowTriangle") + map.get("pinkTriangle") +
                map.get("cyanTriangle") + map.get("blackTriangle");

        int squareNum = map.get("whiteSquare") + map.get("redSquare") +
                map.get("greenSquare") + map.get("blueSquare") +
                map.get("yellowSquare") + map.get("pinkSquare") +
                map.get("cyanSquare") + map.get("blackSquare");

        int rhombusNum = map.get("whiteRhombus") + map.get("redRhombus") +
                map.get("greenRhombus") + map.get("blueRhombus") +
                map.get("yellowRhombus") + map.get("pinkRhombus") +
                map.get("cyanRhombus") + map.get("blackRhombus");

        int rectNum = map.get("whiteRectangle") + map.get("redRectangle") +
                map.get("greenRectangle") + map.get("blueRectangle") +
                map.get("yellowRectangle") + map.get("pinkRectangle") +
                map.get("cyanRectangle") + map.get("blackRectangle");

        int circle = map.get("whiteCircle") + map.get("redCircle") +
                map.get("greenCircle") + map.get("blueCircle") +
                map.get("yellowCircle") + map.get("pinkCircle") +
                map.get("cyanCircle") + map.get("blackCircle");

        int Star = map.get("whiteStar") + map.get("redStar") +
                map.get("greenStar") + map.get("blueStar") +
                map.get("yellowStar") + map.get("pinkStar") +
                map.get("cyanStar") + map.get("blackStar");
        Log.d("laiyang666", "" + rectNum + "" + rhombusNum +"" + squareNum);
        return   rectNum + rhombusNum + squareNum + ""  + circle + "" + triangleNum + "" + Star;
    }
}
