package com.example.laiyang.nest.old_version;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.laiyang.nest.R;
import com.example.laiyang.nest.camera.Service.BridgeService;
import com.example.laiyang.nest.camera.utils.AudioPlayer;
import com.example.laiyang.nest.camera.utils.ContentCommon;
import com.example.laiyang.nest.camera.utils.CustomBuffer;
import com.example.laiyang.nest.camera.utils.MyRender;
import com.example.laiyang.nest.camera.utils.StaticClass;
import com.example.laiyang.nest.camera.utils.SystemValue;
import com.example.laiyang.nest.connect.connect_transport;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

import butterknife.OnClick;
import vstc2.nativecaller.NativeCaller;

public class PlayActivity extends Activity implements View.OnTouchListener, BridgeService.PlayInterface {

    private static final int Up_DATA = 19971211;
    private boolean bProgress = true;
    private View progressView = null;
    private View osdView = null;
    private String strDID = null;
    private boolean isPTZPrompt;
    private MyRender myRender;

    private int nResolution = 0;//分辨率值
    private int nBrightness = 0;//亮度值
    private int nContrast = 0;//对比度
    //分辨率标识符
    private boolean ismax = false;
    private boolean ishigh = false;
    private boolean isp720 = false;
    private boolean ismiddle = false;
    private boolean isqvga1 = false;
    private boolean isvga1 = false;
    private boolean isqvga = false;
    private boolean isvga = false;


    private String stqvga = "qvga";
    private String stvga = "vga";
    private String stqvga1 = "qvga1";
    private String stvga1 = "vga1";
    private String stp720 = "p720";
    private String sthigh = "high";
    private String stmiddle = "middle";
    private String stmax = "max";


    //视频数据
    private byte[] videodata = null;
    private int videoDataLen = 0;
    public int nVideoWidths = 0;
    public int nVideoHeights = 0;

    private Bitmap mBmp;


    private boolean bDisplayFinished = true;
    private boolean isTakepic = false;

    private boolean isPictSave = false;

    private ImageView videoViewPortrait;

    private int i = 0;//拍照张数标志

    private CustomBuffer AudioBuffer = null;
    private AudioPlayer audioPlayer = null;
    private String strName = null;

    private boolean bInitCameraParam = false;
    public boolean isH264 = false;//是否是H264格式标志
    public boolean isJpeg = false;
    private boolean bManualExit = false;

    private int nStreamCodecType;//分辨率格式
    private boolean isTakeVideo = false;
    private long videotime = 0;// 录每张图片的时间



    private GLSurfaceView playSurface = null;

    private TextView textView_reslut, textView_time;
    private StaticClass staticClass;
    private connect_transport connect_transport;
    private boolean isQR = false;
    private boolean isLpr = false;
    private boolean isShape = false;

    public PlayActivity() {
    }

    @OnClick(R.id.GoUp) void Up(){
        connect_transport.go(100,100);
        Log.i("Send Go","Up!!!!!!!!!!");
    }
    @OnClick(R.id.GoDown) void Down(){
        connect_transport.left(100,100);
        Log.i("Send Go","Down!!!!!!!!!!");
    }
    @OnClick(R.id.GoLift) void Lift(){
        connect_transport.right(100,100);
        Log.i("Send Go","Lift!!!!!!!!!");
    }
    @OnClick(R.id.GoRight) void Right(){
        connect_transport.right(100,100);
        Log.i("Send Go","Right!!!!!!!!");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());


        /**
         * 视频窗口
         */

        playSurface = findViewById(R.id.mysufaceview);
        playSurface.setOnTouchListener(this);
        playSurface.setLongClickable(true);

        strName = SystemValue.deviceName;
        strDID = SystemValue.deviceId;
        disPlaywidth = getWindowManager().getDefaultDisplay().getWidth();
        AudioBuffer = new CustomBuffer();
        audioPlayer = new AudioPlayer(AudioBuffer);
        BridgeService.setPlayInterface(this);

        NativeCaller.StartPPPPLivestream(strDID, 10, 1);
        getCameraParams();

        myRender = new MyRender(playSurface);
        playSurface.setRenderer(myRender);

        /**
         *
         */
        connect_transport = new connect_transport();
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    /**
     * 获取reslution
     */

    public static Map<String, Map<Object, Object>> reslutionlist = new HashMap<String, Map<Object, Object>>();

    /**
     * 增加reslution
     */
    private void addReslution(String mess, boolean isfast) {
        if (reslutionlist.size() != 0) {
            if (reslutionlist.containsKey(strDID)) {
                reslutionlist.remove(strDID);
            }
        }
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put(mess, isfast);
        reslutionlist.put(strDID, map);
    }

    /**
     * 接收数据流？
     */
    int disPlaywidth;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Up_DATA) {
                String Reslut = staticClass.reslut;
                String time = staticClass.time;
                textView_reslut.setText(Reslut);
                textView_time.setText(time);
                // Qr_Reslult.setText(staticClass.QrReslut);
            }
            if (msg.what == 1 || msg.what == 2) {
                setViewVisible();
            }
            if (!isPTZPrompt) {
                isPTZPrompt = true;
                showToast("请按菜单键,进行云台控制");
            }
            int width = getWindowManager().getDefaultDisplay().getWidth();
            int height = getWindowManager().getDefaultDisplay().getHeight();
            switch (msg.what) {
                case 1: // h264
                {
                    if (reslutionlist.size() == 0) {
                        if (nResolution == 0) {
                            ismax = true;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(stmax, ismax);
                        } else if (nResolution == 1) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = true;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(sthigh, ishigh);
                        } else if (nResolution == 2) {
                            ismax = false;
                            ismiddle = true;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(stmiddle, ismiddle);
                        } else if (nResolution == 3) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = true;
                            isqvga1 = false;
                            isvga1 = false;
                            addReslution(stp720, isp720);
                            nResolution = 3;
                        } else if (nResolution == 4) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = false;
                            isvga1 = true;
                            addReslution(stvga1, isvga1);
                        } else if (nResolution == 5) {
                            ismax = false;
                            ismiddle = false;
                            ishigh = false;
                            isp720 = false;
                            isqvga1 = true;
                            isvga1 = false;
                            addReslution(stqvga1, isqvga1);
                        }
                    } else {
                        if (reslutionlist.containsKey(strDID)) {
                            getReslution();
                        } else {
                            if (nResolution == 0) {
                                ismax = true;
                                ismiddle = false;
                                ishigh = false;
                                isp720 = false;
                                isqvga1 = false;
                                isvga1 = false;
                                addReslution(stmax, ismax);
                            } else if (nResolution == 1) {
                                ismax = false;
                                ismiddle = false;
                                ishigh = true;
                                isp720 = false;
                                isqvga1 = false;
                                isvga1 = false;
                                addReslution(sthigh, ishigh);
                            } else if (nResolution == 2) {
                                ismax = false;
                                ismiddle = true;
                                ishigh = false;
                                isp720 = false;
                                isqvga1 = false;
                                isvga1 = false;
                                addReslution(stmiddle, ismiddle);
                            } else if (nResolution == 3) {
                                ismax = false;
                                ismiddle = false;
                                ishigh = false;
                                isp720 = true;
                                isqvga1 = false;
                                isvga1 = false;
                                addReslution(stp720, isp720);
                                nResolution = 3;
                            } else if (nResolution == 4) {
                                ismax = false;
                                ismiddle = false;
                                ishigh = false;
                                isp720 = false;
                                isqvga1 = false;
                                isvga1 = true;
                                addReslution(stvga1, isvga1);
                            } else if (nResolution == 5) {
                                ismax = false;
                                ismiddle = false;
                                ishigh = false;
                                isp720 = false;
                                isqvga1 = true;
                                isvga1 = false;
                                addReslution(stqvga1, isqvga1);
                            }
                        }

                    }

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                width, width * 3 / 4);
                        lp.gravity = Gravity.CENTER;
                        playSurface.setLayoutParams(lp);
                    } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                width, height);
                        lp.gravity = Gravity.CENTER;
                        playSurface.setLayoutParams(lp);
                    }
                    myRender.writeSample(videodata, nVideoWidths, nVideoHeights);
                }
                break;
                case 2: // JPEG
                {
                    if (reslutionlist.size() == 0) {
                        if (nResolution == 1) {
                            isvga = true;
                            isqvga = false;
                            addReslution(stvga, isvga);
                        } else if (nResolution == 0) {
                            isqvga = true;
                            isvga = false;
                            addReslution(stqvga, isqvga);
                        }
                    } else {
                        if (reslutionlist.containsKey(strDID)) {
                            getReslution();
                        } else {
                            if (nResolution == 1) {
                                isvga = true;
                                isqvga = false;
                                addReslution(stvga, isvga);
                            } else if (nResolution == 0) {
                                isqvga = true;
                                isvga = false;
                                addReslution(stqvga, isqvga);
                            }
                        }
                    }
                    mBmp = BitmapFactory.decodeByteArray(videodata, 0,
                            videoDataLen);
                    if (mBmp == null) {
                        bDisplayFinished = true;
                        return;
                    }
                    if (isTakepic) {
                        //takePicture(mBmp);
                        isTakepic = false;
                    }
                    nVideoWidths = mBmp.getWidth();
                    nVideoHeights = mBmp.getHeight();

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        // Bitmap
                        Bitmap bitmap = Bitmap.createScaledBitmap(mBmp, width,
                                width * 3 / 4, true);
                        //videoViewLandscape.setVisibility(View.GONE);
                        videoViewPortrait.setVisibility(View.VISIBLE);
                        videoViewPortrait.setImageBitmap(bitmap);

                    } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Bitmap bitmap = Bitmap.createScaledBitmap(mBmp, width, height, true);
                        videoViewPortrait.setVisibility(View.GONE);
                        //videoViewLandscape.setVisibility(View.VISIBLE);
                        //videoViewLandscape.setImageBitmap(bitmap);
                    }

                }
                break;
                default:
                    break;
            }
            if (msg.what == 1 || msg.what == 2) {
                bDisplayFinished = true;
            }
        }

    };

    //设置视频可见
    private void setViewVisible() {
        if (bProgress) {
            bProgress = false;
            // progressView.setVisibility(View.INVISIBLE);
            // osdView.setVisibility(View.VISIBLE);
            getCameraParams();
        }
    }

    /**
     * 得到相机参数
     */
    private void getCameraParams() {

        NativeCaller.PPPPGetSystemParams(strDID,
                ContentCommon.MSG_TYPE_GET_CAMERA_PARAMS);
    }

    /**
     * 简化Toast
     *
     * @param i 显示的String
     */
    private void showToast(String i) {
        Toast.makeText(PlayActivity.this, i, Toast.LENGTH_SHORT).show();
    }


    private void getReslution() {
        if (reslutionlist.containsKey(strDID)) {
            Map<Object, Object> map = reslutionlist.get(strDID);
            if (map.containsKey("qvga")) {
                isqvga = true;
            } else if (map.containsKey("vga")) {
                isvga = true;
            } else if (map.containsKey("qvga1")) {
                isqvga1 = true;
            } else if (map.containsKey("vga1")) {
                isvga1 = true;
            } else if (map.containsKey("p720")) {
                isp720 = true;
            } else if (map.containsKey("high")) {
                ishigh = true;
            } else if (map.containsKey("middle")) {
                ismiddle = true;
            } else if (map.containsKey("max")) {
                ismax = true;
            }
        }
    }


    private synchronized void savePicToSDcard(final Bitmap bmp) {
        String strDate = getStrDate();
        //String date = strDate.substring(0, 10);
        FileOutputStream fos = null;
        try {
            File div = new File(Environment.getExternalStorageDirectory(),
                    "StartCamByLaiyang/takepic");
            if (!div.exists()) {
                div.mkdirs();
            }
            ++i;
            Log.e("", i + "");
            File file = new File(div, strDate + "_" + strDID + "_" + i + ".jpg");
            fos = new FileOutputStream(file);
            if (bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                fos.flush();
                Log.d("tag", "takepicture success");
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showToast("拍照成功！");
                    }
                });
            }
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("拍照失败！");
                }
            });
            Log.d("tag", "exception:" + e.getMessage());
            e.printStackTrace();
        } finally {
            isPictSave = false;
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fos = null;
            }
        }
    }


    //时间格式
    private String getStrDate() {
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
        String strDate = f.format(d);
        return strDate;
    }

    /***
     * BridgeService callback 视频参数回调
     *
     * **/
    @Override
    public void callBackCameraParamNotify(String did, int resolution,
                                          int brightness, int contrast, int hue, int saturation, int flip, int mode) {
        Log.e("设备返回的参数", resolution + "," + brightness + "," + contrast + "," + hue + "," + saturation + "," + flip + "," + mode);
        nBrightness = brightness;
        nContrast = contrast;
        nResolution = resolution;
        bInitCameraParam = true;
        //deviceParamsHandler.sendEmptyMessage(flip);
    }

    @Override
    public void callBackVideoData(byte[] videobuf, int h264Data, int len, int width, int height) {
        Log.d("底层返回数据", "videobuf:" + videobuf + "--" + "h264Data" + h264Data + "len" + len + "width" + width + "height" + height);
        if (!bDisplayFinished)
            return;
        bDisplayFinished = false;
        videodata = videobuf;
        videoDataLen = len;
        Message msg = new Message();
        if (h264Data == 1) { // H264
            nVideoWidths = width;
            nVideoHeights = height;
            if (isTakepic) {
                isTakepic = false;
                byte[] rgb = new byte[width * height * 2];
                NativeCaller.YUV4202RGB565(videobuf, rgb, width, height);
                ByteBuffer buffer = ByteBuffer.wrap(rgb);
                mBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                mBmp.copyPixelsFromBuffer(buffer);
                //takePicture(mBmp);
            }
            isH264 = true;
            msg.what = 1;
        } else { // MJPEG
            isJpeg = true;
            msg.what = 2;
        }
        mHandler.sendMessage(msg);
        //录像数据
       /* if (isTakeVideo) {

            Date date = new Date();
            long times = date.getTime();
            int tspan = (int) (times - videotime);
            Log.d("tag", "play  tspan:" + tspan);
            videotime = times;
            if (videoRecorder != null) {
                if (isJpeg) {

                    videoRecorder.VideoRecordData(2, videobuf, width, height,tspan);
                }
            }
        }*/
    }

    @Override
    public void callBackMessageNotify(String did, int msgType, int param) {
        Log.d("tag", "MessageNotify did: " + did + " msgType: " + msgType
                + " param: " + param);
        if (bManualExit)
            return;

        if (msgType == ContentCommon.PPPP_MSG_TYPE_STREAM) {
            nStreamCodecType = param;
            return;
        }

        if (msgType != ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS) {
            return;
        }

        if (!did.equals(strDID)) {
            return;
        }

        Message msg = new Message();
        msg.what = 1;
        msgHandler.sendMessage(msg);
    }

    @Override
    public void callBackH264Data(byte[] h264, int type, int size) {
        Log.d("tag", "CallBack_H264Data" + " type:" + type + " size:" + size);
        if (isTakeVideo) {
            Date date = new Date();
            long time = date.getTime();
            int tspan = (int) (time - videotime);
            Log.d("tag", "play  tspan:" + tspan);
            videotime = time;
        }
    }

    @Override
    public void callBackAudioData(byte[] pcm, int len) {

    }

    private Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.d("tag", "断线了");
                Toast.makeText(getApplicationContext(),
                        R.string.pppp_status_disconnect, Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        }
    };

    //判断sd卡是否存在
    private boolean existSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
