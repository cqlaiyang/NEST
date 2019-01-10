package com.example.laiyang.nest.test;


import com.example.laiyang.nest.camera.utils.SystemValue;

public class Test {
    public static void main(String[] args){
        String s = "<a,V.|Set=4|>";

        s = s.substring(10,11);
        System.out.print("" + s);
    }

    /**
     * 将byte数组化为十六进制串
     */
    public static StringBuilder byte2hex(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data) {
            stringBuilder.append(String.format("%02X ", byteChar).trim());
            stringBuilder.append(" ");
        }
        return stringBuilder;
    }
}
