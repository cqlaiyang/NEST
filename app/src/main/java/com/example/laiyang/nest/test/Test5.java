package com.example.laiyang.nest.test;

import com.example.laiyang.nest.utils.Logger;

public class Test5 {
    public static void main(String[] args) {
        String result = "挡位信息:1;#12,23,34,45; /车库:D";

        result = result.substring(1,result.length());
        System.out.print(result);
        /*int M02 = Integer.decode(result.substring(5, 6));
        int M03_1 = Integer.decode(result.substring(8, 10));
        int M03_2 = Integer.decode(result.substring(11, 13));
        int M03_3 = Integer.decode(result.substring(14, 16));
        int M03_4 = Integer.decode(result.substring(17, 19));
        String M04 = result.substring(25, 26);


        int zhuche = 0;
        if (M04.equals("A")){
            zhuche = 0;
        }else if (M04.equals("B")){
            zhuche = 1;
        }else if (M04.equals("C")){
            zhuche = 2;
        }else if (M04.equals("D")){
            zhuche = 3;
        }else if (M04.equals("E")){
            zhuche = 4;
        }else if (M04.equals("F")){
            zhuche = 5;
        }else if (M04.equals("G")){
            zhuche = 6;
        }

        byte[] bytes = new byte[]{(byte) M02, (byte) M03_1, (byte) M03_2, (byte) M03_3, (byte) M03_4, (byte) zhuche};*/
    }
}
