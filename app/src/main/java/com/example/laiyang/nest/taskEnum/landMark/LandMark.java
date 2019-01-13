package com.example.laiyang.nest.taskEnum.landMark;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class LandMark {
    public static int landMark(Bitmap bitmap) {
        //hue 色相
        //0°红 60°黄 120°绿 180°青 240°蓝 300°品红
        int HueMin = 85;
        int HueMax = 95;

        //saturation 饱和度，也叫色度
        int sMin = 51;
        int sMax = 204;

        //value
        //亮度值
        int valueMin = 242;
        int valueMax = 255;

        //资源Mat
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);

        //资源Mat转HSVMat
        Mat HSVMat = new Mat();
        Imgproc.cvtColor(mat,HSVMat,Imgproc.COLOR_RGB2HSV);
        //通道数/长/宽/高
        int channels = HSVMat.channels();
        int width = HSVMat.width();
        int height = HSVMat.height();

        byte[]pixl = new byte[channels * width * height];

        HSVMat.get(0,0,pixl);

        //计数值
        int count = 0;
        for (int i = 0 ; i < pixl.length ;i = i + 3 ){
            int h = pixl[i] & 0xff;
            int s = pixl[i + 1] & 0xff;
            int v = pixl[i + 2] & 0xff;
            if (h > HueMin && h < HueMax && s > sMin && s < sMax && v > valueMin && v < valueMax) {
                count ++;
            }
        }

        //释放内存
        mat.release();
        HSVMat.release();
        return count;
    }
}
