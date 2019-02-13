package com.example.laiyang.nest.algorithm;

public class Crc {

    public static byte[] toCrc(byte[] arr_buff) {
        int len = arr_buff.length;

        // CRC寄存器。
        int crc = 0xFFFF;
        int i, j;
        for (i = 0; i < len; i++) {

            // 把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (arr_buff[i] & 0xFF));
            for (j = 0; j < 8; j++) {

                // 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位
                if ((crc & 0x0001) > 0) {

                    // 如果移出位为 1, CRC寄存器与多项式A001进行异或
                    crc = crc >> 1;
                    crc = crc ^ 0xA001;
                } else

                    // 如果移出位为 0,再次右移一位 >>> 不考虑符号影响
                    crc = crc >>> 1;
            }
        }
        return intToBytes(crc);
    }

    private static byte[] intToBytes(int value)  {
        byte[] src = new byte[2];
        src[0] =  (byte) ((value>>8) & 0xFF);
        src[1] =  (byte) (value & 0xFF);
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
