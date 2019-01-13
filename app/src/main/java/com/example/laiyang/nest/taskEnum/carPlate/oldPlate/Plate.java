package com.example.laiyang.nest.taskEnum.carPlate.oldPlate;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.example.laiyang.nest.taskEnum.shape.ShapeUtils;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class Plate {
    public static long handle;
    public static String PlateRecognition(Bitmap PlatePic) throws Exception {
        // 得到资源照片
        Mat srcMat = new Mat();
        Utils.bitmapToMat(PlatePic,srcMat);

        // 得到待识别区域；
        Mat PlateMat = new Mat();
        PlateMat = ShapeUtils.GetROI(srcMat);
        Log.d("laiyang666","" + PlateMat.cols() + "-" + PlateMat.rows());

        // 转换
        Bitmap Src = Bitmap.createBitmap(PlateMat.cols(),PlateMat.rows(),Bitmap.Config.RGB_565);
        Utils.matToBitmap(PlateMat,Src);

        // 识别
        String Plate = SimpleRecog(Src,3);

        Log.d("laiyang666","" + Plate);
        return Plate;
    }
    public static String SimpleRecog(Bitmap bmp, int dp) {

        float dp_asp = dp / 10.f;
//        Mat mat_src = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC1);
        Mat mat_src = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC4);

        float new_w = bmp.getWidth() * dp_asp;
        float new_h = bmp.getHeight() * dp_asp;
        Size sz = new Size(new_w, new_h);
        Utils.bitmapToMat(bmp, mat_src);
        Imgproc.resize(mat_src, mat_src, sz);
        long currentTime1 = System.currentTimeMillis();
        Log.d("laiyang666","Res:" + "???");
        String res = PlateRecognition.SimpleRecognization(mat_src.getNativeObjAddr(),handle);

        if (res.length() > 1){
            res = res.substring(1);
            res = "国" + res;
        }

        Log.d("laiyang666","Res:" + res);
        return res;
    }
    public void initRecognizer(Context context)
    {
        String assetPath = "pr";

        String sdcardPath = Environment.getExternalStorageDirectory()
                + File.separator + assetPath;
        copyFilesFromAssets(context, assetPath, sdcardPath);


        String cascade_filename  =  sdcardPath
                + File.separator+"cascade.xml";
        String finemapping_prototxt  =  sdcardPath
                + File.separator+"HorizonalFinemapping.prototxt";
        String finemapping_caffemodel  =  sdcardPath
                + File.separator+"HorizonalFinemapping.caffemodel";
        String segmentation_prototxt =  sdcardPath
                + File.separator+"Segmentation.prototxt";
        String segmentation_caffemodel =  sdcardPath
                + File.separator+"Segmentation.caffemodel";
        String character_prototxt =  sdcardPath
                + File.separator+"CharacterRecognization.prototxt";
        String character_caffemodel=  sdcardPath
                + File.separator+"CharacterRecognization.caffemodel";
        handle  =  PlateRecognition.InitPlateRecognizer(
                cascade_filename,
                finemapping_prototxt,finemapping_caffemodel,
                segmentation_prototxt,segmentation_caffemodel,
                character_prototxt,character_caffemodel
        );
        Log.d("laiyang666", "initRecognizer: 4");
    }
    public void copyFilesFromAssets(Context context, String oldPath, String newPath) {
        try {
            String[] fileNames = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                // directory
                File file = new File(newPath);
                if (!file.mkdir())
                {
                    Log.d("mkdir","can't make folder");

                }
//                    return false;                // copy recursively
                for (String fileName : fileNames) {
                    copyFilesFromAssets(context, oldPath + "/" + fileName,
                            newPath + "/" + fileName);
                }
            } else {
                // file
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
