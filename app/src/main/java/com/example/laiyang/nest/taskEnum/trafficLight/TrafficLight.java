package com.example.laiyang.nest.taskEnum.trafficLight;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.example.laiyang.nest.utils.Logger;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TrafficLight {
    /**
     * 最开始就是一个简单的像素统计，
     * 但是往往阀值设置很困难，所以应该进行滤波操作，
     * 我在这里选择最小值滤波，可以过滤掉那些高亮区域；
     * @param bitmap 传入参数
     * @return 返回R Y G
     */
    public static String Detection(Bitmap bitmap){
        //资源Mat
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);

        Imgproc.resize(mat,mat,new Size(640,360));
        // 图片截取，滤波操作很吃CPU性能；所以我们在这里进行截取图片，只获得上半部分图片；

        Rect rect = new Rect(0,0,mat.cols(),mat.rows()/2);

        mat = mat.submat(rect);

        // 滤波操作：
        // 将高亮区域压制下去，体现主体颜色
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(35,35));
        Imgproc.erode(mat,mat,kernel);

        //转HSV色彩空间
        Mat HSVMat = new Mat();
        Imgproc.cvtColor(mat,HSVMat,Imgproc.COLOR_RGB2HSV);

        //通道数/长/宽/高
        int channels = HSVMat.channels();
        int width = HSVMat.width();
        int hight = HSVMat.height();

        byte[]Pixl = new byte[channels * width * hight];

        HSVMat.get(0,0,Pixl);

        //计数
        int RedCounter = 0;
        int YellowCounter = 0;
        int GreenCounter = 0;

        //灯颜色阀值
        //------------------------------红
        int RedHueMin = 8;
        int RedHueMax = 169;

        int RedSatMin = 87;
        int RedSatMax = 200;

        int RedValueMin = 0;
        int RedValueMax = 256;
        //------------------------------黄
        int YellowHueMin = 24;
        int YellowHueMax = 36;

        int YellowSatMin = 25;
        int YellowSatMax = 127;

        int YellowValueMin = 160;
        int YellowValueMax = 256;
        //------------------------------绿
        int GreenHueMin = 50;
        int GreenHueMax = 80;

        int GreenSatMin = 50;
        int GreenSatMax = 200;

        int GreenValueMin = 0;
        int GreenValueMax = 256;

        for (int i = 0; i < Pixl.length; i = i + 3) {
            int hue = Pixl[i] & 0xff;
            int saturation = Pixl[i + 1] & 0xff;
            int value = Pixl[i + 2] & 0xff;
            //统计颜色出现的次数
            //绿灯
            if (hue > GreenHueMin && hue < GreenHueMax && saturation > GreenSatMin &&
                    saturation < GreenSatMax && value > GreenValueMin && value < GreenValueMax) {
                GreenCounter ++;
            }
            //红灯
            if ((hue < RedHueMin || hue > RedHueMax )&& saturation > RedSatMin &&
                    saturation < RedSatMax && value > RedValueMin && value < RedValueMax) {
                RedCounter++;
            }
            //黄灯
            if (hue > YellowHueMin && hue < YellowHueMax && saturation > YellowSatMin &&
                    saturation < YellowSatMax && value > YellowValueMin && value < YellowValueMax) {
                YellowCounter++;
            }
        }
        String Color = "";
        //1红 2黄 3绿
        if (RedCounter > GreenCounter && RedCounter > YellowCounter){
            Color = "R";
        }else if (YellowCounter > RedCounter && YellowCounter >GreenCounter){
            Color = "Y";
        }else {
            Color = "G";
        }

        if (RedCounter > 1000){
            Color = "R";
        }

        Logger.i("TrafficLight","" +RedCounter + "-" + YellowCounter + "-" + GreenCounter);
        //释放内存
        HSVMat.release();
        mat.release();
        return Color;
    }

    public static String Detection2(Bitmap bitmap){
        //资源Mat
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);

        Imgproc.resize(mat,mat,new Size(640,360));
        // 图片截取，滤波操作很吃CPU性能；所以我们在这里进行截取图片，只获得上半部分图片；

        Rect rect = new Rect(0,0,mat.cols(),mat.rows()/3);

        mat = mat.submat(rect);


        // 滤波操作：
        // 将高亮区域压制下去，体现主体颜色
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(35,35));
        Imgproc.erode(mat,mat,kernel);


        Bitmap bitmap1 = Bitmap.createBitmap(rect.width,rect.height, Bitmap.Config.RGB_565);

        Utils.matToBitmap(mat,bitmap1);

        //转HSV色彩空间
        Mat HSVMat = new Mat();
        Imgproc.cvtColor(mat,HSVMat,Imgproc.COLOR_RGB2HSV);

        //通道数/长/宽/高
        int channels = HSVMat.channels();
        int width = HSVMat.width();
        int hight = HSVMat.height();

        byte[]Pixl = new byte[channels * width * hight];

        HSVMat.get(0,0,Pixl);

        //计数
        int RedCounter = 0;
        int YellowCounter = 0;
        int GreenCounter = 0;

        //灯颜色阀值
        //------------------------------红
        int RedHueMin = 3;
        int RedHueMax = 165;

        int RedSatMin = 125;
        int RedSatMax = 255;

        int RedValueMin = 125;
        int RedValueMax = 256;
        //------------------------------黄
        int YellowHueMin = 15;
        int YellowHueMax = 30;

        int YellowSatMin = 125;
        int YellowSatMax = 255;

        int YellowValueMin = 125;
        int YellowValueMax = 256;
        //------------------------------绿
        int GreenHueMin = 57;
        int GreenHueMax = 70;

        int GreenSatMin = 125;
        int GreenSatMax = 256;

        int GreenValueMin = 125;
        int GreenValueMax = 256;

        for (int i = 0; i < Pixl.length; i = i + 3) {
            int hue = Pixl[i] & 0xff;
            int saturation = Pixl[i + 1] & 0xff;
            int value = Pixl[i + 2] & 0xff;
            //统计颜色出现的次数
            //绿灯
            if (hue > GreenHueMin && hue < GreenHueMax && saturation > GreenSatMin &&
                    saturation < GreenSatMax && value > GreenValueMin && value < GreenValueMax) {
                GreenCounter ++;
            }
            //红灯
            if ((hue < RedHueMin || hue > RedHueMax )&& saturation > RedSatMin &&
                    saturation < RedSatMax && value > RedValueMin && value < RedValueMax) {
                RedCounter++;
            }
            //黄灯
            if (hue > YellowHueMin && hue < YellowHueMax && saturation > YellowSatMin &&
                    saturation < YellowSatMax && value > YellowValueMin && value < YellowValueMax) {
                YellowCounter++;
            }
        }
        String Color = "";
        //1红 2黄 3绿
        if (RedCounter > GreenCounter && RedCounter > YellowCounter){
            Color = "R";
        }else if (YellowCounter > RedCounter && YellowCounter >GreenCounter){
            Color = "Y";
        }else {
            Color = "G";
        }

        if (RedCounter > 1500){
            Color = "R";
        }
        saveBitmap(bitmap1);
        Logger.i("TrafficLight","" +RedCounter + "-" + YellowCounter + "-" + GreenCounter);
        //释放内存
        HSVMat.release();
        mat.release();
        return Color;
    }
    public static void saveBitmap(Bitmap bmp) {
        FileOutputStream out;
        String bitmapName = "TrafficPic.jpg";
        String QQFilePath;

        try { // 获取SDCard指定目录下
            String sdCardDir = Environment.getExternalStorageDirectory() + "/Traffic/";
            File dirFile = new File(sdCardDir);  //目录转化成文件夹
            if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
            }                          //文件夹有啦，就可以保存图片啦
            File file = new File(sdCardDir, bitmapName);// 在SDcard的目录下创建图片文,以当前时间为其命名
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
