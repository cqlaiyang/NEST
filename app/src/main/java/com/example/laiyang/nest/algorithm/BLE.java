package com.example.laiyang.nest.algorithm;

import com.example.laiyang.nest.utils.Turn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BLE {
    public static void main(String[] args) {
        String qrMessages = "342ABC123";
        qrMessages = qrMessages.substring(3, qrMessages.length());
        System.out.println("" + qrMessages);

        byte[] bytes = new byte[2];
        bytes = BLE(qrMessages);

        System.out.println("" + Turn.byte2hex(bytes));
    }

    private static byte[] BLE(String s) {
        byte[] byteString = s.getBytes();
        long BLE = (0b1000000000000000000000000000000000000000000L);

        for (int i = 0; i < byteString.length; i++) {
            long input = (byte) (byteString[i] & 0b1111111L);

            if (byteString.length - (i + 1) >= 0) {

                // 向左移动
                input <<= (7*(byteString.length - (i + 1)));

                // 连接
                BLE = input | BLE;
            } else {
                // 无需移位，直接处理
                BLE = input | BLE;
            }
        }
        List<Integer> block= new ArrayList<>();
         long nuclear = 0b100000000000000000000000000000000000000000L;
         int count = 0;
         long flag = 0;
         while (nuclear > 0){
             // 判断当前位是否为1为一进入
             if ((nuclear & BLE) > 0){

                 // 设置标志位
                 flag = nuclear & BLE;
                 nuclear >>>= 1;
                 block.add(0);
                 count ++;
             }else {
                 // 进行移位操作。现在已经知道当前位为0，移位，用于判断下一位
                 nuclear >>>=1;
                 block.add(0);
                 // 判断上一位是否为1
                 if (flag > 0){

                     // 如果前一位为1并且下一位也为1  == 符合（但是当前位为0）
                     if ((nuclear & BLE) > 0){
                         // 并且当前位与与下一位都进行了判断，所以count = count+2
                         count = count + 2;

                         // 为下一次判断当前位做准备
                         nuclear >>>= 1;
                         block.add(0);
                     }else { // 上一位为1，但是当前位和下一位都为0，本次判断结束，找到区块
                         block.set(block.size() - count, count);
                         // count ，flag清空
                         count = 0;
                         flag = 0;
                     }
                 }

             }

         }
         double[] cast = new double[block.size()];
         for (double i = 0; i < block.size();i++){
             cast[(int) i] = block.get((int) i) + i / 100;
         }
         // 快速排序
        Arrays.sort(cast);

         int Nf = (int) ((cast[cast.length - 1] * 100) % 100);
         int Lf = (int) cast[cast.length - 1];

         int Ns = (int) ((cast[cast.length - 2] * 100) % 100);
         int Ls  = (int)cast[cast.length - 2];

         int midf = 0;
         int mids = 0;

         // F逆序
        for (int n = 8; n > 0; n--) {
            int bite = Lf & 1;
            midf  <<= 1;
            midf |= bite;
            Lf >>= 1;
        }

        // S逆序
        for (int n = 8; n > 0; n--) {
            int bite = Ls & 1;
            mids  <<= 1;
            mids |= bite;
            Ls >>= 1;
        }

        byte F = (byte) (Nf ^ midf);
        byte S = (byte)(Ns ^ mids);

        byte[] re  =  new byte[2];

        re[0] = F;
        re[1] = S;

        return re;
    }
}
