package com.example.laiyang.nest.algorithm;

import android.util.Log;

import com.example.laiyang.nest.utils.Turn;

public class NewCrc {
    public static void main(String[] args) {
        // 截取不到1但是gx最后一位肯定为一
        String input = "<Cc12x16,Fg,5tx15/x2+\\1/hgGg>";


        System.out.println(Turn.byte2hex(newCrc(input)));

    }

    public static byte[] newCrc(String input) {

        String uesful = input.substring(1, 3) + input.substring(input.length() - 3, input.length() - 1);
        // 截取不到1但是gx最后一位肯定为\1或者\0
        int gx1 = Integer.decode(input.substring(6, 8));
        int gx2 = Integer.decode(input.substring(15, 17));
        int gx3 = Integer.decode(input.substring(19, 20));

        // 通过equals判断是否为\0
        boolean gx4 = input.substring(22, 23).equals("1");

        long bite1 = 0b1L;
        bite1 <<= gx1;

        long bite2 = 0b1L;
        bite2 <<= gx2;

        long bite3 = 0b1L;
        bite3 <<= gx3;

        long bite4 = 0;
        // 通过之前的判断来决定是否有位数
        if (gx4) {
            bite4 = 0b1L;
        } else {
            bite4 = 0b0L;
        }


        long yuan = (bite1 | bite2 | bite3 | bite4) & 0xffff;
        int gx = 0;

        // 逆序
        for (int n = 16; n > 0; n--) {
            int bite = (int) (yuan & 1);
            gx  <<= 1;
            gx |= bite;
            yuan >>= 1;
        }

        byte[] bytes = uesful.getBytes();

        return toCrc(bytes,gx);
    }

    public static byte[] toCrc(byte[] arr_buff,int gx) {
        int len = arr_buff.length;

        // CRC寄存器。
        int crc = 0xFFFF;
        int i, j;
        for (i = 0; i < len; i++) {

            // 把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器
            crc = ((crc & 0xFF00) | ((crc & 0x00FF) ^ (arr_buff[i] & 0xFF)));
            for (j = 0; j < 8; j++) {

                // 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位
                if ((crc & 1) > 0) {

                    // 如果移出位为 1, CRC寄存器与多项式gx进行异或
                    crc = crc >> 1;
                    crc = crc ^ gx;
                } else{
                    // 如果移出位为 0,再次右移一位 >>> 不考虑符号影响
                    crc = crc >>> 1;
                }


            }
        }
        return intToBytes(crc,arr_buff);
    }

    private static byte[] intToBytes(int value,byte[] arr)  {
        byte[] src = new byte[6];

        // 高八位
        src[0] =  (byte) ((value>>>8) & 0xFF);
        // 低八位
        src[5] =  (byte) (value & 0xFF);

        src[1] = arr[0];
        src[2] = arr[1];
        src[3] = arr[2];
        src[4] = arr[3];
        return src;
    }

}
