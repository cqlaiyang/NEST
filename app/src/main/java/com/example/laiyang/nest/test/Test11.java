package com.example.laiyang.nest.test;

public class Test11 {
    public static void main(String[] args) {
        String result = "挡位信息1:1;#12,23,34,45;挡位信息2:3";
        String[] strArrary = result.split(",");

        for (int i = 0; i < strArrary.length; i++){
            System.out.println(strArrary[i]);
        }
        int M02 = Integer.parseInt(strArrary[0].substring(4,5));
        int M04 = Integer.parseInt(strArrary[3].substring(9,10));
        int M03_1 = Integer.parseInt(strArrary[0].substring(9,11));
        int M03_2 = Integer.parseInt(strArrary[1].substring(0,2));
        int M03_3 = Integer.parseInt(strArrary[2].substring(0,2));
        int M03_4 = Integer.parseInt(strArrary[3].substring(0,2));


    }
}
