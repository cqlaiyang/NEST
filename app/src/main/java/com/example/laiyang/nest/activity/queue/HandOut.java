package com.example.laiyang.nest.activity.queue;
/**
 * @author 赖杨 2018/10/26
 * 我们来理一下思维
 * 这是一个类实现了Mission这个接口，该接口有一个方法
 * @link com.example.laiyang.nest.new_version.playActivity.handler
 * 当我们实例化这个类（该对象携带接收到的任务信息）；并且加入队列（队列已经开启）（线程已经开启）
 * @link com.example.laiyang.nest.taskManager.run();
 *队列线程就会挨个执行，执行时取出该对象，执行excute方法（把不同的任务分发出去）；
 */

import android.util.Log;

import com.example.laiyang.nest.camera.veer.VeerCamera;
import com.example.laiyang.nest.taskEnum.TaskEnum;
import com.example.laiyang.nest.taskManager.Mission;
import com.example.laiyang.nest.utils.Logger;
import com.example.laiyang.nest.utils.MessageFilter;

import java.util.Arrays;

public class HandOut implements Mission {
    byte[] Mission;

    public HandOut(byte[] Mission) {
        this.Mission = Mission;
    }

/*    enum comd {
        CMD_QR_CODE,   //识别二维码
        CMD_SHAPE,     //识别图形
        CMD_TRAFFIC,   //识别交通灯
        CMD_PLATE,     //识别车牌
        CMD_RFID_CARD, //解析RFID card
        CMD_STEMAK_IPS,//立体显示识别指令
        CMD_SQR_CODE,  //从车二维码解析
        CMD_WAIT_START //等待开始指令
    }*/


    @Override
    public void execute() {
        /**
         * 分发4个任务！
         */
        Log.i("info","该命令的byte打印：" + Arrays.toString(Mission));
        Logger.i("info","该命令的byte打印：" + Arrays.toString(Mission));
        String cmd = MessageFilter.getCMD(Mission);
        Log.i("info","正在分发该命令：" + cmd);
        Logger.i("info","正在分发该命令：" + cmd);

        // 拆分该命令 主指令:副指令
        String subCmd = cmd.substring(cmd.indexOf(":") + 1,cmd.length());
        cmd = cmd.substring(0,cmd.indexOf(":") + 1);

        Logger.d("laiyang666","" + subCmd + "-" + cmd);
        switch (cmd) {

            // 二维码
            case "CMD_QR_CODE:": {
                direction(subCmd);
                TaskEnum.QR.execute();
                break;
            }

            // 车牌
            case "CMD_PLATE:":{
                direction(subCmd);
                TaskEnum.CAR_PLATE.execute();
                break;
            }

            // 图形
            case "CMD_SHAPE:": {
                direction(subCmd);
                TaskEnum.SHAPE.execute();
                break;
            }

            // 交通灯
            case "CMD_TRAFFIC:": {
                direction(subCmd);
                TaskEnum.TRAFFIC_LIGHT.execute();
                break;
            }

            // RFID卡
            case "CMD_RFID_CARD:": {
                direction(subCmd);
                break;
            }

            // 立体显示标志物
            case "CMD_STEMAK_IPS:":{
                direction(subCmd);
                TaskEnum.IPS.execute();
                break;
            }

            // 从车二维码解析任务
            case "CMD_SQRCODE:":{
                TaskEnum.SQRCODE.execute(subCmd);
                break;
            }
            default:{
                Logger.e("error","错误的命令！上位机没有该命令" + cmd);
                break;
            }
        }
    }

    @Override
    public void execute(String s) {

    }

    public void direction(String dir){
        switch (dir){
            case "FRONT":{
                TaskEnum.FRONT.execute();
                break;
            }
            case "LEFT":{
                TaskEnum.LIFT.execute();
                break;
            }
            case "RIGHT":{
                TaskEnum.RIGHT.execute();
                break;
            }
            default:{
                Logger.e("laiyang666","参数命令有错误没有这个参数！" + dir);
                Logger.i("laiyang666","使用默认参数：FRONT");
                VeerCamera.Reset();
                break;
            }
        }
    }
}
