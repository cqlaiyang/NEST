package com.example.laiyang.nest.utils;

import android.util.Log;

import java.util.Arrays;

public class MessageFilter {
    private static int length = 0;
    private static byte sum = 0;

    /**
     * 该方法，用于过滤来自接收端的消息！
     * 包头（#），数据长度，数据求和校验，数据，包尾（0xff）
     * 【#,length,sum,data,0xff】
     *
     * @return
     */
    public static boolean receiveFilter(byte[] msg) {
        length = msg.length;

        //Todo :bug Arrays outofindex
        for (int i = 2; i <= length - 3; i++) {
            sum += msg[i];
        }

        Log.d("Logger1", "filter: " + sum);

        if (msg[0] == (byte)'#') {
            if (msg[length - 1] == (byte) 0xFF) {
                /**
                 * 比较长度位，需要把int转为字符串让后再将字符串转为char，再将char转为hex（String），再将String和byte equals相比！
                 */
                if (msg[1] == (length - 4)) {
                    if (msg[length - 2] ==  sum) {
                        /**
                         * 判断成功返回数据
                         */
                        Log.d("Logger1","" + Arrays.toString(msg));
                        sum = 0;
                        return true;
                    } else {
                        Log.e("error", "filter: 校验位有误" + String.valueOf(sum &0xff) + String.valueOf(msg[length - 2] & 0xff) );
                    }
                } else {
                    Log.e("error", "filter: 长度位有错" + msg[1] + msg.length);
                }

            } else {
                Log.e("error", "filter: 包尾有错" + msg[length -1] +Arrays.toString(msg) +msg.length );
            }
        } else {
            Log.d("error", "filter: 包头有错！" + msg[0]);
        }
        sum = 0;
        return false;
    }


    /**
     *该方法，用于任务处理后接收到的消息封装成通讯协议进行发送！
     * @param msg
     */
    public static byte[] sendFilter(byte[] msg){
        byte[]format = new byte[msg.length + 4];
        byte sum = 0;
        for (byte m:msg) {
            sum += m;
        }

        format[0] = (byte)'#';
        format[1] = (byte)msg.length;
        format[format.length - 2] = sum;
        format[format.length - 1] = (byte)0xff;

        System.arraycopy(msg,0,format,2,msg.length);

        return format;
    }


    /**
     * 提取有用的命令，交给上位机处理；
     * @param cmd 传入一个原始的任务信息，
     * @return 返回一个String对象，用于接下来的任务分派；
     */
    public static String getCMD(byte[] cmd){
        int length = cmd.length;
        String CMDSting = "";
        byte[] CMD = Arrays.copyOfRange(cmd,2,length - 2);

        CMDSting = Turn.byteArrayToStr(CMD);
        return CMDSting;
    }

}
