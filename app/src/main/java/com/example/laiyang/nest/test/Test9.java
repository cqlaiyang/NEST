package com.example.laiyang.nest.test;

public class Test9 {
    public static void main(String[] args) {
        String result = "abcdrfgfhf;B6-B4;abcdf23,34,45,56,fgfhgghgh#BAbff";
        String[] strArrarys = result.split(";");
        String M02_1 = strArrarys[1].substring(0,2);
        String M02_2 = strArrarys[1].substring(3,5);

        int M11_1 = Integer.parseInt(strArrarys[2].substring(5,7));
        int M11_2 = Integer.parseInt(strArrarys[2].substring(8,10));
        int M11_3 = Integer.parseInt(strArrarys[2].substring(11,13));
        int M11_4 = Integer.parseInt(strArrarys[2].substring(14,16));

        System.out.println(M02_1 + M02_2 + M11_1+M11_2 + M11_3 + M11_4);
    }
}
