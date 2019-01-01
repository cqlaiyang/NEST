package com.example.laiyang.nest.utils;

import android.graphics.Bitmap;

import com.example.laiyang.nest.activity.PlayActivity;

public class GetPicture {

    public static Bitmap getPicture(){
        PlayActivity.isTakepic = true;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = PlayActivity.mBmp;


        return bitmap;
    }


}
