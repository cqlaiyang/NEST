package com.example.laiyang.nest.activity;

import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.example.laiyang.nest.R;
import com.example.laiyang.nest.camera.Adapter.SearchListAdapter;
import com.example.laiyang.nest.camera.Service.BridgeService;
import com.example.laiyang.nest.camera.utils.ContentCommon;
import com.example.laiyang.nest.camera.utils.SystemValue;
import com.example.laiyang.nest.connect.Connect_transport;
import com.example.laiyang.nest.taskEnum.carPlate.oldPlate.Plate;
import com.example.laiyang.nest.taskManager.MissionQueue;
import com.example.laiyang.nest.utils.WifiAdmin;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vstc2.nativecaller.NativeCaller;

public class MeanActivity extends AppCompatActivity implements BridgeService.IpcamClientInterface, BridgeService.AddCameraInterface {
    {
        System.loadLibrary("opencv_java3");
    }

    private WifiManager wifiManager;
    private WifiConfiguration wifiConfiguration;
    private WifiInfo wifiInfo;

    private Spinner spinnerOpen;
    private Spinner SpinnerConnect;
    private boolean UsbConnect = false;
    private boolean WifiConnect = false;
    private boolean isConnect = false;
    private boolean iscar = false;
    private boolean isUsbConnect = false;
    private boolean isWifiConnect = false;
    private Plate plate;
    public static MeanActivity instance = null;


    private Connect_transport connect_transport;

    MissionQueue missionQueue;

    private DhcpInfo dhcpInfo;
    //----------------------------关于相机------------------------------------------
    private static final String STR_MSG_PARAM = "msgparm";
    private int CameraType = ContentCommon.CAMERA_TYPE_MJPEG;
    private int option = ContentCommon.INVALID_OPTION;
    private static final String STR_DID = "did";
    private Intent intentbrod;
    private SearchListAdapter listAdapter = null;
    //----------------------------关于相机------------------------------------------


    //----------------------------视图绑定-------------------------------------------------

    @BindView(R.id.progressBar1)
    ProgressBar progressBar;
    @BindView(R.id.resultText)
    TextView resultText;
    //----------------------------视图绑定-------------------------------------------------


    //----------------------------点击事件------------------------------------------

    /**
     *
     */
    @OnClick(R.id.start)
    void startView() {
        /**
         * 等待窗口
         */
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("请稍等。。。")
                .setCancelable(true)
                .setCancelOutside(true);
        LoadingDailog dailog = loadBuilder.create();
        dailog.show();

        /**
         * 初始化并且连接相机
         */
        Intent intent = new Intent();
        String strUser = "admin";
        String strPwd;
        String strDiD;
        if (iscar) {
            strPwd = "888888";
            strDiD = "VSTD135448VYJWF";
        } else {
            strDiD = "VSTA305435FHUWH";
            strPwd = "888888";
        }
        if (option == ContentCommon.INVALID_OPTION) {
            option = ContentCommon.ADD_CAMERA;
        }
        intent.putExtra(ContentCommon.CAMERA_OPTION, option);
        intent.putExtra(ContentCommon.STR_CAMERA_ID, strDiD);
        intent.putExtra(ContentCommon.STR_CAMERA_USER, strUser);
        intent.putExtra(ContentCommon.STR_CAMERA_PWD, strPwd);
        intent.putExtra(ContentCommon.STR_CAMERA_TYPE, CameraType);

        SystemValue.deviceName = strUser;
        SystemValue.deviceId = strDiD;
        SystemValue.devicePass = strPwd;
        BridgeService.setIpcamClientInterface(this);
        NativeCaller.Init();
        new Thread(new StartPPPPThread()).start();
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
         * 设置
         */
        setting();

        /**
         * 连接我想要的Wifi
         */
        //wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        //wifiInfo = wifiManager.getConnectionInfo();
        //Log.d("wifiInfo", "onCreate: "+ wifiInfo.getSSID());
        //  wificonnnect();


        /**
         * 初始化camera局域网连接
         */
        connectCamera();

        // Meanactivity实例化
        instance = this;

        listAdapter = new SearchListAdapter(this);


        plate = new Plate();
        plate.initRecognizer(this);
    }

    private void wificonnnect() {
        WifiAdmin wifiAdmin = new WifiAdmin(MeanActivity.this);
        wifiAdmin.openWifi();
        wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo("RKRC0119", "12345678", 3));

    }


    private void connectCamera() {

        Intent intent = new Intent(MeanActivity.this, BridgeService.class);
        startService(intent);

        BridgeService.setAddCameraInterface(this);
        intentbrod = new Intent("drop");

    }


    /**
     * setting 连接选项；
     */
    private void setting() {
        spinnerOpen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    iscar = true;
                }
                if (position == 1) {
                    iscar = false;
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
                    default: {
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


    @Override
    public void BSMsgNotifyData(String did, int type, int param) {
        Log.d("ip", "type:" + type + " param:" + param);
        Bundle bd = new Bundle();
        Message msg = PPPPMsgHandler.obtainMessage();
        msg.what = type;
        bd.putInt(STR_MSG_PARAM, param);
        bd.putString(STR_DID, did);
        msg.setData(bd);
        PPPPMsgHandler.sendMessage(msg);
        if (type == ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS) {
            intentbrod.putExtra("ifdrop", param);
            sendBroadcast(intentbrod);
        }
    }

    @Override
    public void BSSnapshotNotify(String did, byte[] bImage, int len) {

    }

    @Override
    public void callBackUserParams(String did, String user1, String pwd1, String user2, String pwd2, String user3, String pwd3) {

    }

    @Override
    public void CameraStatus(String did, int status) {

    }

    @Override
    public void callBackSearchResultData(int cameraType, String strMac, String strName, String strDeviceID, String strIpAddr, int port) {
        Log.e("AddCameraActivity", strDeviceID + strName);
        if (!listAdapter.AddCamera(strMac, strName, strDeviceID)) {
            return;
        }
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


    private Handler PPPPMsgHandler = new Handler() {
        public void handleMessage(Message msg) {

            Bundle bd = msg.getData();
            int msgParam = bd.getInt(STR_MSG_PARAM);
            int msgType = msg.what;
            Log.i("aaa", "====" + msgType + "--msgParam:" + msgParam);
            String did = bd.getString(STR_DID);
            switch (msgType) {
                case ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS:
                    int resid;
                    switch (msgParam) {
                        case ContentCommon.PPPP_STATUS_CONNECTING://0
                            resid = R.string.pppp_status_connecting;
                            progressBar.setVisibility(View.VISIBLE);
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_FAILED://3
                            resid = R.string.pppp_status_connect_failed;
                            progressBar.setVisibility(View.GONE);
                            break;
                        case ContentCommon.PPPP_STATUS_DISCONNECT://4
                            resid = R.string.pppp_status_disconnect;
                            progressBar.setVisibility(View.GONE);
                            break;
                        case ContentCommon.PPPP_STATUS_INITIALING://1
                            resid = R.string.pppp_status_initialing;
                            progressBar.setVisibility(View.VISIBLE);
                            break;
                        case ContentCommon.PPPP_STATUS_INVALID_ID://5
                            resid = R.string.pppp_status_invalid_id;
                            progressBar.setVisibility(View.GONE);
                            break;
                        case ContentCommon.PPPP_STATUS_ON_LINE://2 在线状态
                            resid = R.string.pppp_status_online;
                            progressBar.setVisibility(View.GONE);
                            //摄像机在线之后读取摄像机类型
                            String cmd = "get_status.cgi?loginuse=admin&loginpas=" + SystemValue.devicePass
                                    + "&user=admin&pwd=" + SystemValue.devicePass;
                            NativeCaller.TransferMessage(did, cmd, 1);
                            /**
                             * 开启下一个Activity
                             */
                            try {
                                Intent intent = new Intent(MeanActivity.this, PlayActivity.class);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            break;
                        case ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE://6
                            resid = R.string.device_not_on_line;
                            progressBar.setVisibility(View.GONE);
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT://7
                            resid = R.string.pppp_status_connect_timeout;
                            progressBar.setVisibility(View.GONE);
                            break;
                        case ContentCommon.PPPP_STATUS_CONNECT_ERRER://8
                            resid = R.string.pppp_status_pwd_error;
                            progressBar.setVisibility(View.GONE);
                            break;
                        default:
                            resid = R.string.pppp_status_unknown;
                    }
                    resultText.setText(getResources().getString(resid));
                    if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
                        NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_PARAMS);
                    }
                    if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED
                            || msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT
                            || msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
                        NativeCaller.StopPPPP(did);
                    }
                    break;
                case ContentCommon.PPPP_MSG_TYPE_PPPP_MODE:
                    break;

            }

        }
    };
}
