package com.example.laiyang.nest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.laiyang.nest.old_version.MeanActivity;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    //---------------------------点击事件---------------------------------------------

    /**
     * @ method 打开老的郭宇衡的改进版本
     */
    @OnClick(R.id.OldVersion)
    void OpenOld() {
        Intent intent = new Intent(MainActivity.this, MeanActivity.class);
        startActivity(intent);
    }

    /**
     * @method 打开新版本
     */
    @OnClick(R.id.NewVersion)
    void OpenNew() {
        Intent intent = new Intent();
    }
    //---------------------------点击事件---------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }
}
