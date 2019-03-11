package com.example.laiyang.nest.test;

import android.util.Log;

import com.example.laiyang.nest.utils.Logger;

import org.opencv.core.Mat;

public class Test8 {
    public static void main(String[] args) {
        String reslut= "红外数据:23,34,45,55;从车位置D6;车头方向/A6";
        reslut = reslut.trim();

        String[] strArrary = reslut.split(",");
        for (int i = 0; i < strArrary.length; i++) {
            System.out.println(strArrary[i]);
        }

        String M02_1 = strArrary[0].substring(5,7);
        String M02_2 = strArrary[1].substring(0,2);
        String M02_3 = strArrary[2].substring(0,2);
        String M02_4 = strArrary[3].substring(0,2);

        String M03 = strArrary[3].substring(7,9);
        String M04 = strArrary[3].substring(15,17);

        System.out.println(M03 + M04);


    }
}
