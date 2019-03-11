package com.example.laiyang.nest.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test7 {
    public static void main(String[] args) {
        Pattern p = Pattern.compile("\\d+");
        String[] strings = p.split("挡位信息:1;#12,23,34,45; /车库:D");
        for (int i = 0; i < strings.length; i++) {
            System.out.println(strings[i]);
        }


        p = Pattern.compile("\\d+");
        Matcher m = p.matcher("22bb23");
        System.out.println(m.matches());

    }
}
