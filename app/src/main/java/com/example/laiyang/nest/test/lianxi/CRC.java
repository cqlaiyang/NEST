package com.example.laiyang.nest.test.lianxi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CRC {
    public static void main(String[] args) {
        String input = "";
        Scanner scanner = new Scanner(System.in);
        input = scanner.nextLine();

        // 制作CRC码表
        Map<String,Byte> table = new HashMap<>();
        table.put("A", (byte) 0x00);table.put("B", (byte) 0x01);table.put("C", (byte) 0x02);table.put("D", (byte) 0x03);table.put("E", (byte) 0x04);table.put("U", (byte) 0x05);
        table.put("J", (byte) 0x10);table.put("I", (byte) 0x11);table.put("H", (byte) 0x12);table.put("G", (byte) 0x13);table.put("F", (byte) 0x14);table.put("V", (byte) 0x15);
        table.put("K", (byte) 0x20);table.put("L", (byte) 0x21);table.put("M", (byte) 0x22);table.put("N", (byte) 0x23);table.put("O", (byte) 0x24);table.put("W", (byte) 0x25);
        table.put("T", (byte) 0x30);table.put("S", (byte) 0x31);table.put("R", (byte) 0x32);table.put("Q", (byte) 0x33);table.put("P", (byte) 0x34);table.put("X", (byte) 0x35);

        byte[] bytes = new byte[input.length()];

        for (int i = 0; i < input.length();i++){
            bytes[i] = table.get(input.substring(i,i+1));
        }

        byte[] result = new byte[2];

        result = Crc16(bytes);

    }

    public static byte[] Crc16(byte[] input){
        int CRC = 0xFFFF;

        for (int i = 0; i < input.length; i++){
            CRC = CRC & 0xFF00 | CRC & 0xFF & input[i];

            int LSB = CRC & 0x01;

            // 不考虑符号位右移一位
            CRC = CRC>>>1;
            if (LSB == 0) {
                CRC = CRC >>> 1;
            }else {

            }

        }
        return null;
    }
}
