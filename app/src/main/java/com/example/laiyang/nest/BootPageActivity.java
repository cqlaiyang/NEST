package com.example.laiyang.nest;

/**
 * @author 赖杨
 * @data 2018/10/2
 * @Activity：启动页面过度
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.example.laiyang.nest.activity.MeanActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import vstc2.nativecaller.NativeCaller;

public class BootPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot_page);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏


        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ActivityCompat.checkSelfPermission(BootPageActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(BootPageActivity.this, new String[]
                    {permission}, 123);

        }

        immersion();
        NativeCaller.PPPPInitialOther("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL");

        NativeCaller.SetAPPDataPath("/data/data/com.example.laiyang.nest/files");
        Log.d("eye4","" +getApplicationContext().getFilesDir().getAbsolutePath());
        delayshow();
    }

    /**
     * @method 设置延迟时间；
     */
    private void delayshow() {  new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {

            Intent mainIntent = new Intent(BootPageActivity.this,MeanActivity.class);
            BootPageActivity.this.startActivity(mainIntent);
            BootPageActivity.this.finish();
        }
    },3000);
    }

    /**
     * @method 沉浸式导航栏
     */
    private void immersion() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 激活状态栏
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint 激活导航栏
            tintManager.setNavigationBarTintEnabled(true);
            //设置系统栏设置颜色
            //tintManager.setTintColor(R.color.red);
            //给状态栏设置颜色
            /*tintManager.setStatusBarTintResource(R.color.mask_tags_1);*/
            //Apply the specified drawable or color resource to the system navigation bar.
            //给导航栏设置资源
            /*  tintManager.setNavigationBarTintResource(R.color.mask_tags_1);*/
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        }
        return super.onKeyDown(keyCode,event);
    }
}
