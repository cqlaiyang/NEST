package com.example.laiyang.nest.connect;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.example.laiyang.nest.threadPool.ThreadPoolProxyFactory;
import com.example.laiyang.nest.utils.Logger;
import com.example.laiyang.nest.utils.MessageFilter;
import com.example.laiyang.nest.utils.Turn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;


public class Connect_transport {
    private int port = 60000;
    public static DataInputStream bInputStream = null;
    public static DataOutputStream bOutputStream = null;
    public static Socket socket = null;
    private byte[] rbyte;
    private static Handler reHandler = null;


    private boolean Firstdestroy = false;  ////Firstactivity 是否已销毁了

    /**
     * 关闭Socket服务
     */
    public void destory() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                bInputStream.close();
                bOutputStream.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 连接Socket通讯服务端口！
     *
     * @param reHandler 线程通讯
     * @param IP        Socket服务端口IP地址
     */
    public void Wificonnect(Handler reHandler, String IP) {
        try {
            this.reHandler = reHandler;
            Firstdestroy = false;
            socket = new Socket();
            socket.connect(new InetSocketAddress(IP, port), 5000);
            Logger.i("info","连接小车Servers端成功！");

            bInputStream = new DataInputStream(socket.getInputStream());
            bOutputStream = new DataOutputStream(socket.getOutputStream());
            reThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 接收数据！
     * 就目前来说，
     * 接收线程是一个连接以后就是开启状态的线程，
     * 不能被GC回收
     * 所以用普通线程最好；
     * 2018/11/4  12：05
     */
    private Thread reThread = new Thread(new Runnable() {
        @Override
        public void run() {
            // TODO Auto1-generated method stub
            while (socket != null && !socket.isClosed()) {
                if (Firstdestroy == true)  //Firstactivity 已销毁了
                {
                    break;
                }
                try {
                    int count = 0;
                    while (count == 0) {
                        count = bInputStream.available();
                    }
                    rbyte = new byte[count];
                    bInputStream.read(rbyte);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = rbyte;
                    reHandler.sendMessage(msg);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    });

    /**
     * 发送数据
     * 普通发送
     */
    public static void  send(final String result) {

        //Todo:该改进，空指针异常；
        byte[] resultByte = Turn.strToByteArray(result);

        //使用了自己封装的一个类
        //用于打包成通讯协议
        final byte[] sendData = MessageFilter.sendFilter(resultByte);
        Logger.i("info", "发送数据值" + result + Arrays.toString(sendData));
        Log.i("info", "发送数据值：" + result + Arrays.toString(sendData));
        //线程池的使用
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null && !socket.isClosed()) {
                        Log.i("error", "发送成功");
                        Logger.i("error", "发送成功");
                        bOutputStream.write(sendData, 0, sendData.length);
                        bOutputStream.flush();
                        Message message = new Message();
                        message.what = 2;
                        message.obj = result;
                        reHandler.sendMessage(message);
                        Thread.sleep(100);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void  sendBytes(final byte[] result) {

        //使用了自己封装的一个类
        //用于打包成通讯协议
        final byte[] sendData = MessageFilter.sendFilter(result);
        Logger.i("info", "发送数据值" + result + Arrays.toString(sendData));
        Log.i("info", "发送数据值：" + result + Arrays.toString(sendData));
        //线程池的使用
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(() -> {
            try {
                if (socket != null && !socket.isClosed()) {
                    Log.i("error", "发送成功");
                    Logger.i("error", "发送成功");
                    bOutputStream.write(sendData, 0, sendData.length);
                    bOutputStream.flush();
                    Message message = new Message();
                    message.what = 2;
                    message.obj = Turn.byte2hex(result).toString();
                    reHandler.sendMessage(message);
                    Thread.sleep(100);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 发送数据
     */
    public static void  DelaySend(final String result) {

        //Todo:该改进，空指针异常；
        byte[] resultByte = Turn.strToByteArray(result);

        //使用了自己封装的一个类
        //用于打包成通讯协议
        final byte[] sendData = MessageFilter.sendFilter(resultByte);
        Logger.i("info", "发送数据值" + result + Arrays.toString(sendData));
        Log.i("info", "发送数据值：" + result + Arrays.toString(sendData));
        //线程池的使用
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null && !socket.isClosed()) {
                        Log.i("error", "发送成功");
                        Logger.i("error", "发送成功");
                        bOutputStream.write(sendData, 0, sendData.length);
                        bOutputStream.flush();
                        Thread.sleep(2000);
                        Message message = new Message();
                        message.what = 2;
                        message.obj = result;
                        reHandler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void send(final String result, final Handler reHandler) {

        //Todo:该改进，空指针异常；
        byte[] resultByte = Turn.strToByteArray(result);

        //使用了自己封装的一个类
        //用于打包成通讯协议
        final byte[] sendData = MessageFilter.sendFilter(resultByte);
        Logger.d("error", "发送数据值" + Arrays.toString(sendData));
        Log.d("error", "发送数据值：" + Arrays.toString(sendData));
        //线程池的使用
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("error", "发送成功?");
                    Logger.d("error", "发送成功？");
                    if (socket != null && !socket.isClosed()) {
                        Log.d("error", "发送成功");
                        Logger.d("error", "发送成功");
                        bOutputStream.write(sendData, 0, sendData.length);
                        bOutputStream.flush();
                        Message message = new Message();
                        message.what = 2;
                        message.obj = result;
                        reHandler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
