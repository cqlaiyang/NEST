package com.example.laiyang.nest.connect;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.example.laiyang.nest.activity.MeanActivity;

import android.serialport.SerialPort;

import com.example.laiyang.nest.threadPool.ThreadPoolProxy;
import com.example.laiyang.nest.threadPool.ThreadPoolProxyFactory;
import com.example.laiyang.nest.utils.Logger;
import com.example.laiyang.nest.utils.MessageFilter;
import com.example.laiyang.nest.utils.Turn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;


public class Connect_transport {
    // Wifi Soket通信
    private int port = 60000;
    public static DataInputStream bInputStream = null;
    public static DataOutputStream bOutputStream = null;
    public static Socket socket = null;

    // 串口通信(谷歌官方)
    private SerialPort mSerialPort = null;
    private static OutputStream SerialOutputStream;
    private InputStream SerialInputStream;
    // Linux  万物皆文件
    private String path = "/dev/tty4";
    private int baudrate = 115200;


    // 接受数据串口与Wifi都在使用
    private byte[] rbyte;

    // Handler 多线程通信
    public static Handler reHandler = null;

    /**
     * 连接Socket通讯服务端口！
     *
     * @param reHandler 线程通讯
     * @param IP        Socket服务端口IP地址
     */
    public void Wificonnect(Handler reHandler, String IP) {
        try {
            this.reHandler = reHandler;
            socket = new Socket();
            socket.connect(new InetSocketAddress(IP, port), 5000);
            Logger.i("info", "连接小车Servers端成功！");

            bInputStream = new DataInputStream(socket.getInputStream());
            bOutputStream = new DataOutputStream(socket.getOutputStream());

            // 开启接受线程
            wifiReceiveThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serialConnect(Handler reHandler) {
        // 搭建线程间通信
        this.reHandler = reHandler;

 /*       try {
            // 得到输入输出流实例
           // mSerialPort = new SerialPort(new File(path),baudrate,0);
            SerialOutputStream = mSerialPort.getOutputStream();
            SerialInputStream = mSerialPort.getInputStream();
            serialReceiveThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * 接受串口数据
     */
    private Thread serialReceiveThread = new Thread(new Runnable() {
        byte[] serialreadbyte = new byte[20];

        @Override
        public void run() {
            while (SerialInputStream != null) {
                try {
                    int num = SerialInputStream.read(serialreadbyte);
                    String readSerialStr = new String(serialreadbyte, 0, num, "utf-8");
                    Logger.i("----serialReadByte----", "****" + readSerialStr);

                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = serialreadbyte;
                    reHandler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 增大周期 ，减低频率
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });


    /**
     * 接收数据！WIFI
     * 就目前来说，
     * 接收线程是一个连接以后就是开启状态的线程，
     * 不能被GC回收
     * 所以用普通线程最好；
     * 2018/11/4  12：05
     */
    private Thread wifiReceiveThread = new Thread(new Runnable() {
        @Override
        public void run() {
            // TODO Auto1-generated method stub
            while (socket != null && !socket.isClosed()) {
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
    public static void send(final String result) {

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
                // 判断是否为Wifi发送
                if (MeanActivity.instance.isWifiConnect) {
                    try {
                        if (socket != null && !socket.isClosed()) {
                            Log.i("error", "WIFI发送成功");
                            Logger.i("error", "WIFI发送成功");
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
                } else if (MeanActivity.instance.isUsbConnect) {// 使用串口发送数据
                    try {
                        Log.i("error", "串口发送成功");
                        Logger.i("error", "串口发送成功");
                        MeanActivity.sPort.write(sendData, 5000);
                       /* SerialOutputStream.write(sendData,0,sendData.length);
                        SerialOutputStream.flush();*/
                        Message message = new Message();
                        message.what = 2;
                        message.obj = result;
                        reHandler.sendMessage(message);
                        Thread.sleep(100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    public static void sendBytes(final byte[] result) {

        //使用了自己封装的一个类
        //用于打包成通讯协议
        final byte[] sendData = MessageFilter.sendFilter(result);
        Logger.i("info", "发送数据值" + result + Arrays.toString(sendData));
        Log.i("info", "发送数据值：" + result + Arrays.toString(sendData));
        //线程池的使用

        ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
            @Override
            public void run() {
                // 判断是否为Wifi发送
                if (MeanActivity.instance.isWifiConnect) {
                    try {
                        if (socket != null && !socket.isClosed()) {
                            Log.i("error", "WIFI发送成功");
                            Logger.i("error", "WIFI发送成功");
                            bOutputStream.write(sendData, 0, sendData.length);
                            bOutputStream.flush();
                            //MeanActivity.sPort.write(sendData, 5000);
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
                } else if (MeanActivity.instance.isUsbConnect) {// 使用串口发送数据
                    try {
                        Log.i("error", "串口发送成功");
                        Logger.i("error", "串口发送成功");
                /*    SerialOutputStream.write(sendData,0,sendData.length);
                    SerialOutputStream.flush();*/
                        MeanActivity.sPort.write(sendData, 5000);
                        Message message = new Message();
                        message.what = 2;
                        message.obj = Turn.byte2hex(result).toString();
                        reHandler.sendMessage(message);
                        Thread.sleep(100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 发送数据
     */
    public static void DelaySend(final String result) {

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
                // 判断是否为Wifi发送
                if (MeanActivity.instance.isWifiConnect) {
                    try {
                        if (socket != null && !socket.isClosed()) {
                            Log.i("error", "WIFI发送成功");
                            Logger.i("error", "WIFI发送成功");
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
                } else if (MeanActivity.instance.isUsbConnect) {// 使用串口发送数据
                    try {
                        Log.i("error", "串口发送成功");
                        Logger.i("error", "串口发送成功");
                        /*SerialOutputStream.write(sendData,0,sendData.length);
                        SerialOutputStream.flush();*/
                        MeanActivity.sPort.write(sendData, 5000);
                        Thread.sleep(2000);
                        Message message = new Message();
                        message.what = 2;
                        message.obj = result;
                        reHandler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
/*                try {
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
                }*/
            }
        });
    }


}
