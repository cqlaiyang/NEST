package com.example.laiyang.nest.test;


import java.util.Scanner;

public class PrimeNumber {
    public static void main(String[] args){
        boolean isPrime = true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入一个正整数");
        int num = scanner.nextInt();
        if (num > 0){
            int k = (int) Math.sqrt(num);// k是num的正立方根，取整数
            for (int i = 2; i <= k; i ++){
                if (num % i == 0){
                    isPrime = false;//不是素数
                    break;
                }
            }
        }
        if (isPrime){
            System.out.println(num + "是素数");
        }else {
            System.out.println(num + "不是素数");
        }
    }
}
