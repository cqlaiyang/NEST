package com.example.laiyang.nest.taskEnum.qrCode;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.laiyang.nest.utils.Logger;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.THRESH_OTSU;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.threshold;

/**
 * @deprecated 2018/12/7
 * 更换更加牛逼的方案
 * @author 赖杨 2018/10/27
 * @class 这是一个解析二位码的类
 */
public class QrCode_decode{

    /**
     * 解析二维码
     *
     * @param bitmap 被解析的图形对象
     * @return 解析的结果
     */
    public static String decode(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        RGBLuminanceSource luminanceSource = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
        try {
            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);

            Result result = new QRCodeReader().decode(binaryBitmap, hints);
            Log.d("QrCode", "decode: "+result.getText());
            return result.getText();
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
            return "- not find -";
        }
    }

    /**
     * Zxing识别
     * @param bmpYUVBytes
     * @param bmpWidth
     * @param bmpHeight
     * @return
     */
    private static String decodeYUVByZxing(byte[] bmpYUVBytes, int bmpWidth, int bmpHeight) {
        String zxingResult = "";
        // Both dimensions must be greater than 0
        if (null != bmpYUVBytes && bmpWidth > 0 && bmpHeight > 0) {
            try {
                PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(bmpYUVBytes, bmpWidth,
                        bmpHeight, 0, 0, bmpWidth, bmpHeight, true);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
                Reader reader = new QRCodeReader();
                Result result = reader.decode(binaryBitmap);
                if (null != result) {
                    zxingResult = result.getText();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return zxingResult;
    }

    private static String decodeYUVByZbar(byte[] bmpYUVBytes, int bmpWidth, int bmpHeight) {
        String zbarResult = "";
        // Both dimensions must be greater than 0
        if (null != bmpYUVBytes && bmpWidth > 0 && bmpHeight > 0) {
//            ZBarDecoder decoder = new ZBarDecoder();
  //          zbarResult = decoder.decodeRaw(bmpYUVBytes, bmpWidth, bmpHeight);
        }
        Log.e("HtscCodeScanningUtil", "decode by zbar, result = " + zbarResult);
        return zbarResult;
    }


    /**
     * 找到二维码区域；找到以后给识别类处理，但是识别效果提升不大
     * @param pic
     * @return
     */
    public static Bitmap GetROI(Bitmap pic){

        Bitmap Debug = Bitmap.createBitmap(pic.getWidth(),pic.getHeight(),Bitmap.Config.RGB_565);

        Mat grayMat = new Mat();

        // 转化为灰度图
        Utils.bitmapToMat(pic, grayMat);
        Imgproc.cvtColor(grayMat, grayMat, Imgproc.COLOR_RGB2GRAY);

        Mat binaryMat = new Mat();

        // 图像二值化
        threshold(grayMat, binaryMat, 0, 255, THRESH_BINARY | THRESH_OTSU);

        Mat OpenMat = new Mat();
        Mat ErodeMat = new Mat();
        // 腐蚀 - 形态学开操作
        Mat k = Imgproc.getStructuringElement(MORPH_RECT, new Size(30, 30), new Point(15, 15));
        Imgproc.morphologyEx(binaryMat, OpenMat, Imgproc.MORPH_OPEN, k);

        // Debug
        Utils.matToBitmap(OpenMat,Debug);

        // 全部轮廓
        List<MatOfPoint> Whole = new ArrayList<MatOfPoint>();
        // 外部轮廓
        List<MatOfPoint> Margin = new ArrayList<MatOfPoint>();
        Mat Top = new Mat();

        // 轮廓查找
        findContours(OpenMat, Whole, Top, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        findContours(OpenMat, Margin, Top, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));


        // 轮廓剔除
        for (int b = 0; b < Margin.size(); b++) {
            MatOfPoint marginPoint = Margin.get(b);
            double marginLenth = Imgproc.arcLength(new MatOfPoint2f(marginPoint.toArray()), false);

            for (int i = 0; i < Whole.size(); i++) {
                MatOfPoint wholePoint = Whole.get(i);
                double wholeLenth = Imgproc.arcLength(new MatOfPoint2f(wholePoint.toArray()), false);
                double Area = Imgproc.contourArea(wholePoint, false);
                if (wholeLenth == marginLenth) {
                    Whole.remove(i);
                    i = 0;
                }

                if (Area < 2000){
                    Whole.remove(i);
                    i = 0;
                }
            }
        }

        // ROI 区域
        // 定义Rect用于截取
        Rect rect = null;

        for (int i = 0; i < Whole.size(); i++) {
            RotatedRect minRect = Imgproc.minAreaRect(new MatOfPoint2f(Whole.get(i).toArray()));

            double w = minRect.size.width;
            double h = minRect.size.height;
            double rate = Math.min(w, h) / Math.max(w, h);
            if (rate > 0.85 && w > binaryMat.cols() / 4 && h > binaryMat.rows() / 3) {
                rect = Imgproc.boundingRect(Whole.get(i));
            }
        }

        if (rect != null){
            // 截取二维码区域
            Mat ROI;
            ROI = binaryMat.submat(rect);

            // 转化为Bitmap
            Bitmap ROIArea = Bitmap.createBitmap(ROI.cols(), ROI.rows(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(ROI, ROIArea);

            return ROIArea;
        }else {
            Logger.d("laiyang666","没有找到二维码区域");
            Utils.matToBitmap(binaryMat,pic);
            return pic;
        }

    }


    public static Bitmap GetBinay(Bitmap pic){
        Mat grayMat = new Mat();

        // 转化为灰度图
        Utils.bitmapToMat(pic, grayMat);
        Imgproc.cvtColor(grayMat, grayMat, Imgproc.COLOR_RGB2GRAY);

        Mat binaryMat = new Mat();

        // 图像二值化
        threshold(grayMat, binaryMat, 0, 255, THRESH_BINARY | THRESH_OTSU);

        Utils.matToBitmap(binaryMat,pic);

        return pic;
    }

}
