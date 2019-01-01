package com.example.laiyang.nest.taskEnum.carPlate.oldPlate;

/**
 * Created by yujinke on 24/10/2017.
 */

public class PlateRecognition {
    static {
        System.loadLibrary("hyperlpr");
    }
    public static native long InitPlateRecognizer(String casacde_detection,
                                           String finemapping_prototxt,String finemapping_caffemodel,
                                           String segmentation_prototxt,String segmentation_caffemodel,
                                           String charRecognization_proto,String charRecognization_caffemodel);

    public static native void ReleasePlateRecognizer(long  object);
    public static native String SimpleRecognization(long  inputMat,long object);

}
