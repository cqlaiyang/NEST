package com.example.laiyang.nest.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args){

        // 输入数据
        String input = "BHNP";

        // CRC寄存器0xFFFF 16位
        int CRC = 0xFFFF;

        Map<String,Byte>table = new HashMap<String, Byte>();
        table.put("A",(byte)0x00);table.put("B", (byte) 0x01);table.put("C",(byte)0x02);table.put("D",(byte)0x03);table.put("E",(byte)0x04);table.put("U",(byte)0x05);
        table.put("J",(byte)0x10);table.put("I",(byte)0x11);table.put("H",(byte)0x12);table.put("G",(byte)0x13);table.put("F",(byte)0x14);table.put("V",(byte)0x15);
        table.put("K",(byte)0x20);table.put("L",(byte)0x21);table.put("M",(byte)0x22);table.put("N",(byte)0x23);table.put("O",(byte)0x24);table.put("W",(byte)0x25);
        table.put("T",(byte)0x30);table.put("S",(byte)0x31);table.put("R",(byte)0x32);table.put("Q",(byte)0x33);table.put("P",(byte)0x34);table.put("X",(byte)0x35);
        // 数据有几段就做几次
        for (int i = 0 ; i < input.length(); i++) {
            // 截取最后一位转化为byte型
            byte  data = table.get(input.substring(i,i + 1));

            // data与CRC低8位取异或存入CRC
            CRC =(byte) ((data & 0xFF) ^ (CRC & 0x00FF | CRC & 0xFF00));

            // 八次处理
            for (int j = 0;  j < 8; j++){

                // 判断LSB是否为1
                if ((byte)(CRC & 0x0001) > 0) {

                    CRC = (byte) (CRC >>> 1);
                    CRC = CRC & 0xA001;
                } else {

                    // 向右移位 高位补零，不考虑符号位；
                    CRC = (byte) (CRC >>> 1);
                }
            }


            System.out.println("" + byte2hex(intToBytes(CRC)));
        }


    }

    private static byte[] intToBytes(int value)  {
        byte[] src = new byte[2];
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
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
