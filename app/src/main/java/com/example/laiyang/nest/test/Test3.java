package com.example.laiyang.nest.test;

import java.util.Arrays;

public class Test3 {
    public static void main(String[] args){
        String input = "入库信息：E，立体车库，2层；挡位信息/3；(<3,4,5,7,8,9,11>/<1,2,3,4,5,6>)/挡位：2";
        // 首先得到参数和密码子
        int[] parm = new int[]{Integer.decode(input.substring(24,25)),
                Integer.parseInt(input.substring(26,27)),
                Integer.parseInt(input.substring(28,29)),
                Integer.parseInt(input.substring(30,31)),
                Integer.parseInt(input.substring(32,33)),
                Integer.parseInt(input.substring(34,35)),
                Integer.parseInt(input.substring(36,38))};
        int[] key = new int[]{Integer.parseInt(input.substring(41,42)),
                Integer.parseInt(input.substring(43,44)),
                Integer.parseInt(input.substring(45,46)),
                Integer.parseInt(input.substring(47,48)),
                Integer.parseInt(input.substring(49,50)),
                Integer.parseInt(input.substring(51,52))};

        // 进行排序
        Arrays.sort(parm);
        Arrays.sort(key);
        int p = 0;
        int q = 0;
        // 得到最大质数
        for (int i = parm.length -1 ; i > -1; i --){
            int count = 0;
            for (int j = 2; j <= parm[i] / 2;j ++){
                if (parm[i] % j == 0) {
                    count ++;
                }
            }
            if (p == 0){
                if (count == 0){
                    p = parm[i];
                }
            }else if (count == 0){
                q = parm[i];
                break;
            }

        }

        int n = p * q;
        int ola = (p - 1)*(q - 1);

        int e = 0;
        // 找出e
        for (int i = 1; i < ola; i ++) {
            if (ola % i != 0){
                int count = 0;
                for (int j = 2; j < i/2; j ++){
                    if (i % j == 0){
                        count ++;
                    }
                }
                if (count == 0){
                    e  = i;
                    break;
                }
            }
        }

        int d = 0;
        // 找出d
        for (int i = 1; e * i < 65536; i++){
            if (e * i % ola == 1){
                d = i;
                break;
            }
        }

        byte[] reslut = new byte[6];

        for (int i = 0; i < key.length; i ++) {
            reslut[i] = (byte) (((int)(Math.pow(key[i],d) % n)) & 0xff);
        }

        System.out.println("" + Arrays.toString(reslut) + "-" + p +"-" + q + "-" + n + "-" + e + "-" + d);
    }
}
