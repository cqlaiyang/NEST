package com.example.laiyang.nest.test;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

public class Run {
    public static void main(String[] args){
        CRC32 crc32 = new CRC32();

        Map<String,Byte> table = new HashMap<String, Byte>();

        String input = "BHNP";
        table.put("A",(byte)0x00);table.put("B", (byte) 0x01);table.put("C",(byte)0x02);table.put("D",(byte)0x03);table.put("E",(byte)0x04);table.put("U",(byte)0x05);
        table.put("J",(byte)0x10);table.put("I",(byte)0x11);table.put("H",(byte)0x12);table.put("G",(byte)0x13);table.put("F",(byte)0x14);table.put("V",(byte)0x15);
        table.put("K",(byte)0x20);table.put("L",(byte)0x21);table.put("M",(byte)0x22);table.put("N",(byte)0x23);table.put("O",(byte)0x24);table.put("W",(byte)0x25);
        table.put("T",(byte)0x30);table.put("S",(byte)0x31);table.put("R",(byte)0x32);table.put("Q",(byte)0x33);table.put("P",(byte)0x34);table.put("X",(byte)0x35);

        byte[] inputBytes = new byte[input.length()];
        byte[] outBytes;
        for (int i = 0 ; i  < input.length(); i ++) {
            inputBytes[i] = table.get(input.substring(i, i + 1));

            System.out.println( " " + input.substring(i, i + 1) + "-" +byte2hex(inputBytes));

        }

        outBytes = toCrc(inputBytes);
        System.out.println("" +byte2hex(outBytes) );

    }
    public static byte[] toCrc(byte[] arr_buff) {
        int len = arr_buff.length;

        // CRC寄存器。
        int crc = 0xFFFF;
        int i, j;
        for (i = 0; i < len; i++) {

            // 把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器
            crc = (crc ^ (arr_buff[i] & 0xFF));

            crc = crc >>> 1;
            for (j = 1; j < 8; j++) {

                // 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位
                if ((crc & 0x0001) > 0) {

                    // 如果移出位为 1, CRC寄存器与多项式A001进行异或

                    crc = crc ^ 0xA001;
                    crc = crc >>> 1;
                } else

                    // 如果移出位为 0,再次右移一位 >>> 不考虑符号影响
                    crc = crc >>> 1;
            }
        }
        return intToBytes(crc);
    }

    private static byte[] intToBytes(int value)  {
        byte[] src = new byte[2];
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }
    public static StringBuilder byte2hex(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data) {
            stringBuilder.append(String.format("%02X ", byteChar).trim());
            stringBuilder.append(" ");
        }
        return stringBuilder;
    }
}
