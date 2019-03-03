package com.example.laiyang.nest.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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
import com.example.laiyang.nest.utils.Transparent;
import com.fizzer.doraemon.passwordinputdialog.PassWordDialog;
import com.fizzer.doraemon.passwordinputdialog.impl.DialogCompleteListener;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public boolean isUsbConnect = false;
    public boolean isWifiConnect = false;
    private Plate plate;
    public static MeanActivity instance = null;


    public Connect_transport Connect_transport;

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
        SharedPreferences pref = getSharedPreferences("password", MODE_PRIVATE);
        String password = pref.getString("password", null);
        String[] error = new String[]{"7", "8", "9",};
        if (password == null) {
            Toast.makeText(this, "密码错误请重新输入", Toast.LENGTH_SHORT).show();
            checkPasswords();
            return;
        } else {
            for (int i = 0; i < error.length; i++) {
                if (password.substring(0, 1).equals(error[i]) ||
                        password.substring(1, 2).equals(error[i]) ||
                        password.substring(2, 3).equals(error[i])) {
                    Toast.makeText(this, "密码错误请重新输入", Toast.LENGTH_SHORT).show();
                    checkPasswords();
                    return;
                }
            }
        }

     /*   // 储存WIfi状态
        SharedPreferences.Editor editor = getSharedPreferences("mode",MODE_PRIVATE).edit();
        editor.putBoolean("IsUSB",isUsbConnect);
        editor.putBoolean("IsWIFI",isWifiConnect);
        editor.apply();*/

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
            strPwd = "88888888";
            strDiD = "VSTG027289SZLXJ";
        } else {
            strPwd = "88888888";
            strDiD = "VSTD135448VYJWF";
            // 调试摄像头
            /*strDiD = "VSTA305435FHUWH";
            strPwd = "88888888";*/
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

        Connect_transport = new Connect_transport();
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
         * 初始化camera局域网连接
         */
        connectCamera();

        // Meanactivity实例化
        instance = this;

        listAdapter = new SearchListAdapter(this);

        // 输入启动密码
        SharedPreferences.Editor editor = getSharedPreferences("password", MODE_PRIVATE).edit();
        editor.putString("password", null);// 初始化密码
        editor.apply();
        checkPasswords();

        plate = new Plate();
        plate.initRecognizer(this);
    }

    /**
     * 密码输入
     */
    private void checkPasswords() {
        new PassWordDialog(this).setTitle("输入电机启动密码").setSubTitle("无关人员请勿操作")
                .setMoney("   ").setCompleteListener(new DialogCompleteListener() {
            @Override
            public void dialogCompleteListener(String money, String pwd) {
                SharedPreferences.Editor editor = getSharedPreferences("password", MODE_PRIVATE).edit();
                editor.putString("password", pwd);
                editor.apply();
            }
        }).show();
    }

    private void connectCamera() {

        Intent intent = new Intent(MeanActivity.this, BridgeService.class);
        startService(intent);

        BridgeService.setAddCameraInterface(this);
        intentbrod = new Intent("drop");

    }
    //--------------------------------------------USB连接------------------------------------------------------------------------------------------

    public static UsbSerialPort sPort = null;
    private static final int MESSAGE_REFRESH = 101;
    private static final long REFRESH_TIMEOUT_MILLIS = 5000;
    private UsbManager mUsbManager;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private List<UsbSerialPort> mEntries = new ArrayList<UsbSerialPort>();
    private final String TAG = MeanActivity.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private UsbDeviceConnection connection;
    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.e(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {   //新的数据
                    MeanActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = data;
                            Connect_transport.reHandler.sendMessage(msg);
                        }
                    });
                }
            };
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_REFRESH:
                    // 更新串口列表
                    refreshDeviceList();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private Handler usbHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                useUsbtoserial();
            }
        }
    };


    /**
     * @method 更新串口列表
     * 通过异步的方式实现
     */
    private void refreshDeviceList() {
        // 对USBManager的初始化
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        // 在主线程里面创建的异步任务， 就是在 doInBackground();
        // 里面做一些耗时操作
        // onPreExecute在doInBackground调用前使用；
        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
                Log.e(TAG, "Refreshing device list ...");
                Log.e("mUsbManager is :", "  " + mUsbManager);
                final List<UsbSerialDriver> drivers =
                        UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);

                final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
                for (final UsbSerialDriver driver : drivers) {
                    final List<UsbSerialPort> ports = driver.getPorts();
                    Log.e(TAG, String.format("+ %s: %s port%s",
                            driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
                    result.addAll(ports);
                }
                return result;
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                mEntries.clear();
                mEntries.addAll(result);
                usbHandler.sendEmptyMessage(2);
                Log.e(TAG, "Done refreshing, " + mEntries.size() + " entries found.");
            }
        }.execute((Void) null);
    }

    /**
     * 现在已经得到USB设备。打开指定串口并且设置
     */
    private void useUsbtoserial() {
        final UsbSerialPort port = mEntries.get(0);  //A72上只有一个 usb转串口，用position =0即可
        final UsbSerialDriver driver = port.getDriver();
        final UsbDevice device = driver.getDevice();
        final String usbid = String.format("Vendor %s  ，Product %s",
                HexDump.toHexString((short) device.getVendorId()),
                HexDump.toHexString((short) device.getProductId()));
        // Message msg = LeftFragment.showidHandler.obtainMessage(22, usbid);
        //msg.sendToTarget();
        MeanActivity.sPort = port;
        if (sPort != null) {
            controlusb();  //使用usb功能
        }
    }

    // 在打开usb设备前，弹出选择对话框，尝试获取usb权限
    private void openUsbDevice() {
        tryGetUsbPermission();
    }

    /**
     * 已经得到了指定的USB串口，现在进行设置
     */
    protected void controlusb() {
        // 打印该串口
        Log.e(TAG, "Resumed, port=" + sPort);
        if (sPort == null) {
            // 如果为空打印没有找到设备
            Toast.makeText(MeanActivity.this, "No serial device.", Toast.LENGTH_SHORT).show();
        } else {

            // 获取权限
            openUsbDevice();
            // 如果连接状态为空，这是一个嵌套调用，防止空指针异常
            if (connection == null) {
                // 重新连接
                mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
                Toast.makeText(MeanActivity.this, "Opening device failed", Toast.LENGTH_SHORT).show();
                return;
            }

            // 如果 connection已经实例化设置参数
            try {
                sPort.open(connection);
                sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                // 出现异常
                // Error Opening
                Toast.makeText(MeanActivity.this, "Error opening device: ", Toast.LENGTH_SHORT).show();
                try {
                    // 并且关闭串口，写的还挺好的
                    sPort.close();
                } catch (IOException e2) {
                }
                // 并且sport置为空
                sPort = null;
                // 结束调用
                return;
            }
            Toast.makeText(MeanActivity.this, "Serial device: " + sPort.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
        }

        onDeviceStateChange();
        Transparent.dismiss();//关闭加载对话框
    }


    // 获取USB权限，动态获取权限
    private void tryGetUsbPermission() {

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);

        // 注册广播
        registerReceiver(mUsbPermissionActionReceiver, filter);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        //here do emulation to ask all connected usb device for permission
        for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
            //add some conditional check if necessary
            if (mUsbManager.hasPermission(usbDevice)) {
                //if has already got permission, just goto connect it
                //that means: user has choose yes for your previously popup window asking for grant perssion for this usb device
                //and also choose option: not ask again

                // 如果有权限，直接开启连接
                afterGetUsbPermission(usbDevice);
            } else {
                //this line will let android popup window, ask user whether to allow this app to have permission to operate this usb device
                mUsbManager.requestPermission(usbDevice, mPermissionIntent);
            }
        }
    }

    private final BroadcastReceiver mUsbPermissionActionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        //user choose YES for your previously popup window asking for grant perssion for this usb device
                        if (null != usbDevice) {
                            afterGetUsbPermission(usbDevice);
                        }
                    } else {
                        //user choose NO for your previously popup window asking for grant perssion for this usb device
                        Toast.makeText(context, String.valueOf("Permission denied for device" + usbDevice), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    private void onDeviceStateChange() {
        // 首先关掉以前的串口监听，开始新的一波的串口监听
        stopIoManager();
        startIoManager();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.e(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.e(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener); //添加监听
            // 可以使用线程池替换
            mExecutor.submit(mSerialIoManager); //在新的线程中监听串口的数据变化
        }
    }

    // 当得到权限以后。打开设备实例化Connection
    private void afterGetUsbPermission(UsbDevice usbDevice) {

        // Toast一个Text；
        Toast.makeText(MeanActivity.this, String.valueOf("Found USB device: VID=" + usbDevice.getVendorId() + " PID=" + usbDevice.getProductId()), Toast.LENGTH_LONG).show();
        doYourOpenUsbDevice(usbDevice);
    }

    private void doYourOpenUsbDevice(UsbDevice usbDevice) {
        connection = mUsbManager.openDevice(usbDevice);
    }

    //--------------------------------------------USB连接------------------------------------------------------------------------------------------

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
                        // 延时发送，（5）秒
                        mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS); //启动usb的识别和获取

                        // 加载等待效果框
                        Transparent.showLoadingMessage(MeanActivity.this, "加载中", false);//启动旋转效果的对话框，实现usb的识别和获取
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
