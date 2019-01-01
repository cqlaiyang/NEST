package com.example.laiyang.nest.taskEnum.carPlate.latest;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.laiyang.PlateRecongnization;
import com.example.laiyang.nest.activity.PlayActivity;
import com.example.laiyang.nest.taskEnum.shape.ShapeUtils;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class LatestPlate {
    public static String latestPlate(Bitmap bitmap){
        // 得到资源照片
        Mat srcMat = new Mat();
        Utils.bitmapToMat(bitmap,srcMat);

        // 得到待识别区域；
        Mat PlateMat = new Mat();
        try {
            PlateMat = ShapeUtils.GetROI(srcMat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("laiyang666","" + PlateMat.cols() + "-" + PlateMat.rows());

        // 转换
        Bitmap Src = Bitmap.createBitmap(PlateMat.cols(),PlateMat.rows(),Bitmap.Config.RGB_565);
        Utils.matToBitmap(PlateMat,Src);

        // 车牌识别
        String Reslut = PlayActivity.instance.plateRecongnization.SimpleRecog(Src,3);

        return Reslut;
    }
}
