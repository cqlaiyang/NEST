package com.example.laiyang.nest.activity.queue;

import com.example.laiyang.nest.connect.Connect_transport;
import com.example.laiyang.nest.taskManager.Mission;

/**
 * @author Creted by laiyang on 2018/11/4
 * 这是一个消息回传的类(该类携带的有回传数据，在实例化的时候会传入回传数据)，同样的他也实现了Mission这个接口，
 * 都有一个方法execute();实现该类以后，传入已经开启的队列线程；
 * 当线程执行得到这个实例化对象的时候执行execute()方法;
 */
public class SendQueue implements Mission {
    private Connect_transport connect_transport;

    public SendQueue(String result) {
        this.result = result;
    }

    public SendQueue(byte[] bytesResult) {
        this.bytesResult = bytesResult;
    }

    private String result = "";

    private byte[] bytesResult;

    @Override
    public void execute() {
        try {
            connect_transport = new Connect_transport();
            if (result.isEmpty()){
                // 发送byte[]
                connect_transport.sendBytes(bytesResult);
            }else {
                // 发送String ASCII码
                connect_transport.send(result);
            }

            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void execute(String s) {

    }

}
