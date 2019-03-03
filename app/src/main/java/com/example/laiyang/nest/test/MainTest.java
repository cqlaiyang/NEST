package com.example.laiyang.nest.test;

public class MainTest {

    public static void main(String[] args) {
        String text="8952262wsdf wef vew 885";//需要加密的字符串
        int key=1314;//key
        String jiami=Encrypt(text,key);//加密
        System.out.println("密文:"+jiami);

        String jiemi=Decrypt(jiami,key);//解密
        System.out.println("原文文:"+jiemi);

    }

    private static int C1= 52845;
    private static int C2= 22719;

    // 加密函数

    public static String Encrypt(String S, int Key)
    {

        StringBuffer Result=new StringBuffer();
        StringBuffer str;
        int i,j;


        for(i=0; i<S.length(); i++){
            // 依次对字符串中各字符进行操作
            Result.append((char)(S.charAt(i)^(Key>>8))); // 将密钥移位后与字符异或
            Key = ((byte)Result.charAt(i)+Key)*C1+C2; // 产生下一个密钥
        }
        S=Result.toString();
        System.out.println("密文中间值:"+S);
        Result=new StringBuffer();
        for(i=0; i<S.length(); i++) // 对加密结果进行转换
        {
            j=(int)S.charAt(i); // 提取字符
            // 将字符转换为两个字母保存
            str=new StringBuffer(); // 设置str长度为2
            str.append((char)(65+j/26));//这里将65改大点的数例如256，密文就会变乱码，效果更好，相应的，解密处要改为相同的数
            str.append((char)(65+j%26));
            Result.append(str);
        }
        return Result.toString();
    }

    // 解密函数

    public static String Decrypt(String S, int Key)
    {
        StringBuffer Result=new StringBuffer();
        StringBuffer str;
        int i,j;


        for(i=0; i < S.length()/2; i++) // 将字符串两个字母一组进行处理
        {
            j = ((int)S.charAt(2*i)-65)*26;//相应的，解密处要改为相同的数

            j += (int)S.charAt(2*i+1)-65;
            Result.append((char)j);
        }
        S=Result.toString(); // 保存中间结果
        System.out.println("原文中间值:"+S);
        Result=new StringBuffer();
        for(i=0; i<S.length(); i++) // 依次对字符串中各字符进行操作
        {
            Result.append((char)(S.charAt(i)^(Key>>8))); // 将密钥移位后与字符异或
            Key = ((byte)S.charAt(i)+Key)*C1+C2; // 产生下一个密钥
        }
        return Result.toString();
    }

}
