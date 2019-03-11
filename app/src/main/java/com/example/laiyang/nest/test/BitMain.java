package com.example.laiyang.nest.test;

/**
 * 正负数的移位操作
 */
public class BitMain {
    public static void main(String[] args) {
        int a = -15,b = 15;
        System.out.println(a>>2);
        System.out.println(b>>2);

        // 判断奇数偶数
        // 末尾为一就为奇数 末尾为零就为偶数
        for (int i = 0; i < 100; i++ ){
            if ((i & 1) == 0){//偶数
                System.out.println(i);
            }
        }

        int a1 = 1,b1 = 2;
        a1^= b1;
        b1 ^= a1;
        a1 ^= b1;

        System.out.println("a = " + a1);
        System.out.println("b = " + b1);

        a = -15;
        b = 15;

        System.out.println(~a + 1);
        System.out.println(~b + 1);
    }
}
