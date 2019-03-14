package com.example.laiyang.nest.test;

public class Test10 {
    public static void main(String[] args) {
        String input = "abcdefg红外数据:23,34,45,55;车头方向:B6,从车车库:D";

        String[] strArrary = input.split(",");
        for (int i = 0; i < strArrary.length; i++) {
            System.out.println(i + "-" + strArrary[i]);
        }
        int M03_1 = Integer.parseInt(strArrary[0].substring(12,14));
        int M03_2 = Integer.parseInt(strArrary[1].substring(0,2));
        int M03_3 = Integer.parseInt(strArrary[2].substring(0,2));
        int M03_4 = Integer.parseInt(strArrary[3].substring(0,2));

        String M04 = strArrary[3].substring(8,10);
        int M05 = Integer.parseInt(strArrary[4].substring(5,6));

        System.out.println(M03_1 +"-"+ M03_2+ "-" + M03_3 +"-"+ M03_4 +"-"+ M04 +"-"+ M05 );


    }
}
