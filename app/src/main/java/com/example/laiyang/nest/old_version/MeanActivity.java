package com.example.laiyang.nest.old_version;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.example.laiyang.nest.R;
import com.example.laiyang.nest.camera.Service.BridgeService;
import com.example.laiyang.nest.camera.utils.ContentCommon;
import com.example.laiyang.nest.camera.utils.SystemValue;
import com.example.laiyang.nest.connect.XcApplication;
import com.example.laiyang.nest.connect.connect_transport;
import com.example.laiyang.nest.utils.ConnectWiFi;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import vstc2.nativecaller.NativeCaller;

public class MeanActivity extends AppCompatActivity {
    private WifiManager wifiManager;
    private WifiConfiguration wifiConfiguration;
    private WifiInfo wifiInfo;

    private Spinner spinnerOpen;
    private Spinner SpinnerConnect;
    private boolean UsbConnect = false;
    private boolean WifiConnect = false;
    private boolean isConnect = false;
    private boolean isservice = false;
    private boolean isUsbConnect = false;
    private boolean isWifiConnect = false;

    private connect_transport connect_transport;

    private DhcpInfo dhcpInfo;
    //----------------------------关于相机------------------------------------------
    private int CameraType = ContentCommon.CAMERA_TYPE_MJPEG;
    private int option = ContentCommon.INVALID_OPTION;
    //----------------------------关于相机------------------------------------------



    //----------------------------点击事件------------------------------------------

    /**
     *
     */
    @OnClick(R.id.start) void startView(){
        /**
         * 等待窗口
         */
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("请等大约41s")
                .setCancelable(true)
                .setCancelOutside(true);
        LoadingDailog dailog = loadBuilder.create();
        dailog.show();

        /**
         * 初始化并且连接相机
         */
        Intent intent = new Intent();
        String strUser = "admin";
        String strPwd = "888888";
        String strDiD = "VSTD135448VYJWF";
        if (option == ContentCommon.INVALID_OPTION){
            option = ContentCommon.ADD_CAMERA;
        }
        intent.putExtra(ContentCommon.CAMERA_OPTION,option);
        intent.putExtra(ContentCommon.STR_CAMERA_ID,strDiD);
        intent.putExtra(ContentCommon.STR_CAMERA_USER,strUser);
        intent.putExtra(ContentCommon.STR_CAMERA_PWD,strPwd);
        intent.putExtra(ContentCommon.STR_CAMERA_TYPE,CameraType);
        SystemValue.deviceName = strUser;
        SystemValue.deviceId = strDiD;
        SystemValue.devicePass = strPwd;
        NativeCaller.Init();
        new Thread(new StartPPPPThread()).start();


        /**
         * 延遲，一端時間后開啓視頻流待改進！
         */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(41000);
                    Intent intent = new Intent(MeanActivity.this,PlayActivity.class);
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    //----------------------------点击事件------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mean);
        Logger.addLogAdapter(new AndroidLogAdapter());
        ButterKnife.bind(this);


        /**
         * 选择控件
         */
        spinnerOpen = findViewById(R.id.spinner);
        SpinnerConnect = findViewById(R.id.connect);
        /**
         * 得到权限
         */
        getPermisson();

        /**
         * 设置
         */
        setting();

        /**
         * 连接我想要的Wifi
         */
        wificonnnect();

        /**
         * 初始化camera局域网连接
         */
        connectCamera();
        /**
         * 得到单片机的IP
         */
        wifi_Init();

        /**
         *连接socket通訊協議的單片機server端！
         */
        connect_transport = new connect_transport();
        connectSCM();
    }

    private void connectSCM() {
        XcApplication.executorServicetor.execute(new Runnable() {
            @Override
            public void run() {
                connect_transport.connect(rehHandler,wifi_Init());
            }
        });
    }

    private Handler rehHandler = new Handler(){
        public void handleMessage(Message msg){

        }
    };

    private String wifi_Init() {
        // 得到服务器的IP地址
        wifiManager = (WifiManager) getApplicationContext(). getSystemService(Context.WIFI_SERVICE);
        dhcpInfo = wifiManager.getDhcpInfo();
        String IPCar = Formatter.formatIpAddress(dhcpInfo.gateway);
        Log.i("StmIP",IPCar);
        return IPCar;
    }

    private void connectCamera() {

        Intent intent = new Intent(MeanActivity.this,BridgeService.class);
        startService(intent);

        /**
         * 很重要哦
         * 没有这个底层调用就会连接失败！
         */

        NativeCaller.PPPPInitialOther("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL");

    }

    /**
     * 连接我想要的Wifi
     */
    private void wificonnnect() {
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration configuration = ConnectWiFi.configWifiInfo(this, "BKRC0119", "12345678", 2);
        int netId = configuration.networkId;
        if (netId == -1) {
            netId = wifiManager.addNetwork(configuration);
            Logger.d(netId);
        }
        final int finalNetId = netId;
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        wifiManager.enableNetwork(finalNetId, true);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

    }


    /**
     * setting 连接选项和是否开启守护；
     */
    private void setting() {
        spinnerOpen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    isservice = true;
                    Logger.d(isservice);
                }
                if (position == 1) {
                    isservice = false;
                    Logger.d(isservice);
                }
                String s = parent.getItemAtPosition(position).toString();
                Toast.makeText(MeanActivity.this, s + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpinnerConnect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        isWifiConnect = true;
                        isUsbConnect = false;
                        break;
                    }
                    case 1: {
                        isUsbConnect = true;
                        isWifiConnect = false;
                        break;
                    }
                    default:{
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /**
     * @method 动态获取权限；Permission is Wifi
     */
    private void getPermisson() {
        String permisson = Manifest.permission.ACCESS_WIFI_STATE;
        if (ActivityCompat.checkSelfPermission(this, permisson) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MeanActivity.this, new String[]{permisson}, 123);
        }
    }

    private void getWifi() {
        Logger.d("--------------------------------------------------------------------");
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                wifiConfiguration = new WifiConfiguration();
                while (WifiConnect){
                    try {
                        wifiInfo = wifiManager.getConnectionInfo();
                        Logger.d(wifiInfo.toString());
                        if (!wifiInfo.getSSID().equals("6105")){
                            isConnect = false;
                            while(!wifiManager.isWifiEnabled()){
                                Logger.d("打开WiFi" + wifiManager.setWifiEnabled(true));
                                Thread.sleep(500);
                            }
                            List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
                            for (WifiConfiguration configuration : configurations){
                                if (configuration.SSID != null && configuration.SSID.equals("")){
                                    wifiConfiguration = configuration;
                                    break;
                                }
                            }
                            Logger.d("OnClick:" + wifiConfiguration.networkId + wifiConfiguration.SSID);
                            while(!wifiManager.enableNetwork(wifiConfiguration.networkId,true)){
                                Logger.d("尝试连接");
                                Thread.sleep(500);
                            }
                            Logger.d("连接失败");

                        }else{
                            isConnect = true;
                        }
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }



    class StartPPPPThread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(100);
                startCameraPPPP();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void startCameraPPPP() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (SystemValue.deviceId.toLowerCase().startsWith("vsta")) {
                NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                        SystemValue.devicePass, 1, "", "EFGFFBBOKAIEGHJAEDHJFEEOHMNGDCNJCDFKAKHLEBJHKEKMCAFCDLLLHAOCJPPMBHMNOMCJKGJEBGGHJHIOMFBDNPKNFEGCEGCBGCALMFOHBCGMFK", 0);
            } else if (SystemValue.deviceId.toLowerCase().startsWith("vstd")) {
                NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                        SystemValue.devicePass, 1, "", "HZLXSXIALKHYEIEJHUASLMHWEESUEKAUIHPHSWAOSTEMENSQPDLRLNPAPEPGEPERIBLQLKHXELEHHULOEGIAEEHYEIEK-$$", 1);
            } else if (SystemValue.deviceId.toLowerCase().startsWith("vstf")) {
                NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                        SystemValue.devicePass, 1, "", "HZLXEJIALKHYATPCHULNSVLMEELSHWIHPFIBAOHXIDICSQEHENEKPAARSTELERPDLNEPLKEILPHUHXHZEJEEEHEGEM-$$", 1);
            } else if (SystemValue.deviceId.toLowerCase().startsWith("vste")) {
                NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                        SystemValue.devicePass, 1, "", "EEGDFHBAKKIOGNJHEGHMFEEDGLNOHJMPHAFPBEDLADILKEKPDLBDDNPOHKKCIFKJBNNNKLCPPPNDBFDL", 0);
            } else if (SystemValue.deviceId.toLowerCase().startsWith("vstg")) {
                NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                        SystemValue.devicePass, 1, "", "EEGDFHBOKCIGGFJPECHIFNEBGJNLHOMIHEFJBADPAGJELNKJDKANCBPJGHLAIALAADMDKPDGOENEBECCIK:vstarcam2018", 0);
            } else if (SystemValue.deviceId.toLowerCase().startsWith("vstb") || SystemValue.deviceId.toLowerCase().startsWith("vstc")) {
                NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                        SystemValue.devicePass, 1, "", "ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL", 0);
            } else {
                NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                        SystemValue.devicePass, 1, "", "", 0);
            }
        }
    }
}
