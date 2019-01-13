package com.example.laiyang.nest.application;

import android.app.Application;

import com.example.laiyang.nest.utils.Logger;

public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init(this);
    }
}
