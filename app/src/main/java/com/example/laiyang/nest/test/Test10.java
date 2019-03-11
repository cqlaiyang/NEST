package com.example.laiyang.nest.test;

public class Test10 {
    public static void main(String[] args) {
        String input = "abcdefg红外数据:23,34,45,55;从车位置:D6;车头方向:B6,从车车库:D";

        String[] strArrary = input.split(",");
        for (int i = 0; i < strArrary.length;i++){
            System.out.println(i + "-" + strArrary[i]);
        }

        int M02_1 = Integer.parseInt(strArrary[0].substring(12,14));
        int M02_2 = Integer.parseInt(strArrary[1].substring(0,2));
        int M02_3 = Integer.parseInt(strArrary[2].substring(0,2));
        int M02_4 = Integer.parseInt(strArrary[3].substring(0,2));

        int M03 = Integer.parseInt(strArrary[3].substring(8,10),16);
        int M04 = Integer.parseInt(strArrary[3].substring(16,18),16);
        int M05 = Integer.parseInt(strArrary[4].substring(5,6),16);

    }
}
