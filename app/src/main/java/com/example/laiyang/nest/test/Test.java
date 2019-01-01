package com.example.laiyang.nest.test;



public class Test {
    public static void main(String[] args){
        byte[] bytes = {(byte) 0x03,(byte) 0x05,(byte) 0x14,(byte) 0x45,(byte) 0xDE,(byte) 0x92};

        StringBuilder stringBuilder = byte2hex(bytes);

        System.out.println("" + stringBuilder);

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
