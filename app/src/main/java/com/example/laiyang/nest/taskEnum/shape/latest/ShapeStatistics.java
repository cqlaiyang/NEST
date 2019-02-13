package com.example.laiyang.nest.taskEnum.shape.latest;


import android.util.Log;

import java.util.List;
import java.util.Map;

public class ShapeStatistics {
    public static String first(List<Map<String,Integer>> matList){
        int shibian = 0;
        int circle = 0;
        int triangle = 0;
        int stars = 0;
        for (int i = 0; i < matList.size(); i++){
            for (String shape : matList.get(i).keySet()){
                int count = matList.get(i).get(shape);
                if (count > 0){
                    Log.d("laiyang66",shape +"-"+ count);
                    if (shape.equals("square") || shape.equals("rectangle")|| shape.equals("rhombus")){
                        shibian +=  count;
                    }else if (shape.equals("circle")){
                        circle  += count ;
                    }else if (shape.equals("triangle")){
                        triangle += count;
                    }else if (shape.equals("stars")){
                        stars += count;
                    }
                }
            }
        }

        return "" + shibian +"" + circle + "" + triangle + "" + stars;
    }
}
