package com.example.laiyang.nest.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.laiyang.PlateRecongnization;
import com.example.laiyang.nest.R;
import com.example.laiyang.nest.camera.Service.BridgeService;
import com.example.laiyang.nest.camera.utils.AudioPlayer;
import com.example.laiyang.nest.camera.utils.ContentCommon;
import com.example.laiyang.nest.camera.utils.CustomBuffer;
import com.example.laiyang.nest.camera.utils.MyRender;
import com.example.laiyang.nest.camera.utils.SystemValue;
import com.example.laiyang.nest.connect.Connect_transport;
import com.example.laiyang.nest.activity.queue.HandOut;
import com.example.laiyang.nest.taskEnum.TaskEnum;
import com.example.laiyang.nest.taskEnum.qrCode.QrCallBack;
import com.example.laiyang.nest.taskEnum.qrCode.QrCode_decode;
import com.example.laiyang.nest.taskManager.MissionQueue;
import com.example.laiyang.nest.threadPool.ThreadPoolProxyFactory;
import com.example.laiyang.nest.utils.MessageFilter;
import com.orhanobut.logger.Logger;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import vstc2.nativecaller.NativeCaller;

public class PlayActivity extends AppCompatActivity implements BridgeService.PlayInterface, QRCodeView.Delegate {

    private static final int Up_DATA = 19971211;
    private boolean bProgress = true;
    private View progressView = null;

    private String strDID = SystemValue.deviceId;
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

    public static Bitmap mBmp;


    private boolean bDisplayFinished = true;
    public static boolean isTakepic = false;


    private ImageView videoViewPortrait;

    private int i = 0;//拍照张数标志

    private CustomBuffer AudioBuffer = null;
    private AudioPlayer audioPlayer = null;

    private boolean bInitCameraParam = false;
    private int nStreamCodecType;//分辨率格式
    public boolean isH264 = false;//是否是H264格式标志
    public boolean isJpeg = false;
    private boolean bManualExit = false;

    private boolean isTakeVideo = false;
    private long videotime = 0;// 录每张图片的时间

    private MeanActivity meanActivity;

    private GLSurfaceView playSurface = null;

    private Connect_transport connect_transport;

    public static PlayActivity instance = null;

    //----------------------------------以下事件的绑定----------------------------------------------------
    @BindView(R.id.ShowCmd)
    TextView ShowCmd;
    @BindView(R.id.setImageView)
    ImageView setImageView;
    @BindView(R.id.SendMessage)
    TextView SendMessage;

    public ZBarView zBarView;
    public ZXingView zXingView;

    private DhcpInfo dhcpInfo;
    private MissionQueue missionQueue;
    private static QrCallBack qrCallBack;
    private boolean doZbarBefore = false;
    private static String QrResult = null;
    public static String CheckSend = "";
    public static int countSendCheck = 0;
    public PlateRecongnization plateRecongnization;
    //----------------------------------以上事件的绑定----------------------------------------------------

    //----------------------------------------以下點擊事件-------------------------------------------------------

    /**
     * 这是一个用于现场调试的方法
     *
     * @param view
     */
    @OnClick({R.id.Start, R.id.Trffic, R.id.landMark, R.id.Qrcode, R.id.plate, R.id.Shape, R.id.ClearAccept, R.id.ClearSend,R.id.Test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Start: {
                SharedPreferences pref = getSharedPreferences("password",MODE_PRIVATE);
                String password = pref.getString("password","000000");
                connect_transport.send(password);
                break;
            }
            case R.id.Trffic: {
                TaskEnum.TRAFFIC_LIGHT.execute();
                break;
            }
            case R.id.landMark: {
                TaskEnum.IPS.execute();
                break;
            }
            case R.id.Qrcode: {
                TaskEnum.QR.execute();
                break;
            }
            case R.id.plate: {
                TaskEnum.CAR_PLATE.execute();
                break;
            }
            case R.id.Shape: {
                TaskEnum.SHAPE.execute();
                break;
            }
            case R.id.ClearAccept: {
                ShowCmd.setText("");
                break;
            }
            case R.id.ClearSend: {
                SendMessage.setText("");
                break;
            }
            case R.id.Test:{
                TaskEnum.FRONT.execute();
            }

        }
    }


    //------------------------------------以上是點擊事件----------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // 初始化类和控件
        initAll();

        // 连接单片机建立Sokect通讯
        connectSCM();

        // 一个普通任务队列打开
        // size打开一个窗口
        missionQueue = new MissionQueue(1);
        missionQueue.start();

        zXingView.setDelegate(this);
        zBarView.setDelegate(this);

        instance = this;
    }

    private void initAll() {
        // 初始化控件ButterKnife。。。
        ButterKnife.bind(this);

        // 实例化连接类
        connect_transport = new Connect_transport();

        // 视频窗口的初始化
        initView();

        // 设置Services接口
        BridgeService.setPlayInterface(this);

        // 底层调用
        NativeCaller.StartPPPPLivestream(strDID, 10, 1);

        // 得到相机参数
        getCameraParams();

        // 二维码
        zBarView = findViewById(R.id.Zbar);
        zXingView = findViewById(R.id.Zxing);
        zXingView.startSpot();
        zBarView.startSpot();

        // 车牌
        plateRecongnization = new PlateRecongnization(this);
        plateRecongnization.initRecognizer();
    }

    public void connectSCM() {

        if (MeanActivity.instance.isWifiConnect){

            //连接操作是一个操作过后就是一个垃圾线程
            //最好使用线程池来进行操作
            ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
                @Override
                public void run() {
                    connect_transport.Wificonnect(handler, wifi_Init());
                }
            });
        } else if (MeanActivity.instance.isUsbConnect) {
            ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
                @Override
                public void run() {
                    connect_transport.serialConnect(handler);
                }
            });

        }

    }

    private String wifi_Init() {
        // 得到服务器的IP地址
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        dhcpInfo = wifiManager.getDhcpInfo();
        String IPCar = Formatter.formatIpAddress(dhcpInfo.gateway);
        Log.d("Stm", "" + IPCar);
        Log.d("IPCar", "wifi_Init: " + IPCar);
        return IPCar;
        //return "192.168.3.11";
    }

    /**
     * 把这个handle传入消息“发送”，“接收”，“连接”类；
     * 一旦有消息接收到，handle就会子线程，通知这个主线程
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    byte[] mByte;
                    mByte = (byte[]) msg.obj;


                    //过滤不对的通讯协议，如果协议通过，那么int puanduan为1进入队列的分发任务！
                    boolean panduan = MessageFilter.receiveFilter(mByte);


                    //Todo:完成过滤器的设计！finished at 2018/10/24
                    //Todo:任务队列的分发！执行！ finished at 2018/10/26
                    //Todo:得到当前视频流图片的命令！finished at 2018/11/7
                    if (panduan) {
                        if (!MessageFilter.getCMD(mByte).equals("NP")) {
                            connect_transport.send("NP");
                        }
                        com.example.laiyang.nest.utils.Logger.d("info", "加入队列");
                        Log.d("error", "加入队列");
                        HandOut handOut = new HandOut(mByte);
                        missionQueue.add(handOut);


                        //把命令显示在平板上
                        ShowCMD(mByte);
                    }

                    break;
                }

                case 2: {

                    // 消息接收发送不再这个线程
                    // 如果消息发送接收到我的任务结果发送这个动作
                    // handle就通知主线程打印该发送出去的消息在主线程TextView；
                    String SendString = (String) msg.obj;

                    Log.d("laiyang666","what fuck!!!!!!!!");



                    ShowCMD(SendString);

                    break;
                }
                default: {
                    break;
                }
            }
            return false;
        }
    });

    /**
     * 这个方法把符合通讯协议的消息显示在TextView上
     *
     * @param mByte
     */
    private void ShowCMD(byte[] mByte) {
        String cmd = "";
        cmd = MessageFilter.getCMD(mByte);
        ShowCmd.append(cmd);

        // 用于判断是否接收成功
        CheckSend = cmd;

        ShowCmd.append("\n");
        if (ShowCmd.getText().length() > 1024) {
            ShowCmd.setText("");
        }
    }

    private void ShowCMD(String s) {
        SendMessage.append(s);
        SendMessage.append("\n");

        // Todo：重新发送，保证小车接收到我的消息 finised at 2018/12/13 日
        // 判断如果没有接收到单片机的NP就重新发送；
        if ((!CheckSend.equals("NP")) && countSendCheck < 3){

            // 判断发送的消息是否为NP是NP就重新不发送
            if (!s.equals("NP")){


                // 进入再次发送，就会嵌套调用-死循环,这也是一个耗时操作
                Connect_transport.DelaySend(s);


                // 重新发送计数
                countSendCheck ++;
            }else {
                CheckSend = "";
            }

        }else {

            // 重新发送的出口
            // 如果接收为NP和countSendCheck >= 5; 退出循环，接收判断给空，计数给0；
            CheckSend = "";
            countSendCheck = 0;
        }


        if (ShowCmd.getText().length() > 1024) {
            ShowCmd.setText("");
        }
    }

    /**
     * 把每次任务处理的图片显示在主线程View上
     */
    private Handler ShowHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                setImageView.setImageBitmap(mBmp);
            }
        }
    };


    /**
     * 接口回调
     *
     * @param qrCallBack2
     */
    public void RecogQrCode(QrCallBack qrCallBack2) {

        qrCallBack = qrCallBack2;

        isTakepic = true;

        zBarView.decodeQRCode(mBmp);
        // 通过Zxing解析；
        //zXingView.decodeQRCode(mBmp);

        ThreadPoolProxyFactory.getNormalThreadPoolProxy().excute(new Runnable() {
            @Override
            public void run() {

                // 延时1.5秒
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 如果识别失败调用ZBar进行识别；
                // Zbar对近处的识别效果特别好；
                if (QrResult == null) {

                    Logger.d("laiyang666","开始识别！");
                    zXingView.decodeQRCode(mBmp);
                }

                // 因为Zbar识别特别快；
                // 所以延迟3秒
                // 进行Zbar识别以后；判断是否成功；
                // 如果失败，回调null；
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (QrResult == null) {
                    qrCallBack.callbleBack(null);
                } else {
                    QrResult = null;
                }
            }
        });

    }

    @Override
    public void onScanQRCodeSuccess(String result) {

        Log.d("laiyang6666", "二维码回调" + result);
        QrResult = result;
        // 接口回调
        qrCallBack.callbleBack(result);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.d("laiyang666", "-----------------");
    }

    // ------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------
    private void initView() {

        playSurface = findViewById(R.id.mysufaceview);
        playSurface.setLongClickable(true);


        disPlaywidth = getWindowManager().getDefaultDisplay().getWidth();
        AudioBuffer = new CustomBuffer();
        audioPlayer = new AudioPlayer(AudioBuffer);

        myRender = new MyRender(playSurface);
        playSurface.setRenderer(myRender);
        //命令展示框
        ShowCmd.setMovementMethod(ScrollingMovementMethod.getInstance());
        SendMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
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
     * 对视频数据流进行处理
     */
    int disPlaywidth;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Up_DATA) {
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

    /**
     * 底层数据回调，所有的Video数据，照片数据都在这里；
     *
     * @param videobuf
     * @param h264Data
     * @param len
     * @param width
     * @param height
     */
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
                Message message = new Message();
                message.what = 1;
                ShowHandler.sendMessage(message);
                //takePicture(mBmp);
            }
            isH264 = true;
            msg.what = 1;
        } else { // MJPEG
            isJpeg = true;
            msg.what = 2;
        }
        mHandler.sendMessage(msg);
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

}
