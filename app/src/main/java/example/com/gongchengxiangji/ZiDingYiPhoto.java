package example.com.gongchengxiangji;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;
import com.umeng.analytics.MobclickAgent;
import java.util.Timer;
import java.util.TimerTask;
import example.com.gongchengxiangji.location.GPSLocationListener;
import example.com.gongchengxiangji.location.GPSLocationManager;
import example.com.gongchengxiangji.location.GPSProviderStatus;
import example.com.gongchengxiangji.view.CameraSurfaceView;
import example.com.gongchengxiangji.view.RectOnCamera;

public class ZiDingYiPhoto extends Activity implements SensorEventListener,OnPermissionCallback,View.OnClickListener,RectOnCamera.IAutoFocus{
    private CameraSurfaceView mCameraSurfaceView;
    private RectOnCamera mRectOnCamera;
    private Button takePicBtn;
    public static TextView jindu,weidu,fangxiang;
    //权限检测类
    private PermissionHelper mPermissionHelper;
    private final static String[] MULTI_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    //private TextView text_gps;
    private GPSLocationManager gpsLocationManager;
    private static final int BAIDU_READ_PHONE_STATE =100;
    //手机旋转方向
    private SensorManager sensorManager = null;
    private Sensor gyroSensor = null;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    private float[] angle = new float[3];
    private int recLen = 50;
    //开启线程类
    Timer timer = new Timer();
    public static String mjindu,mweidu,mfangxiang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.zidingyi_photo);
        ById();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mRectOnCamera.setIAutoFocus(this);
        takePicBtn.setOnClickListener(this);
        checkPermissions();
        initData();
        if (Build.VERSION.SDK_INT >= 23) {
            showContacts();
        } else {
            gpsLocationManager.start(new MyListener());
        }
    }
    private void ById() {
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        mRectOnCamera = (RectOnCamera) findViewById(R.id.rectOnCamera);
        takePicBtn= (Button) findViewById(R.id.takePic);
        jindu = (TextView) findViewById(R.id.photo_jindu);
        weidu = (TextView) findViewById(R.id.photo_weidu);
        fangxiang = (TextView) findViewById(R.id.photo_fangxiang);
    }
    public ZiDingYiPhoto() {
        angle[0] = 0;
        angle[1] = 0;
        angle[2] = 0;
        timestamp = 0;
    }
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recLen--;
                    if(recLen < 0){
                        timer.cancel();
                        startActivity(new Intent(ZiDingYiPhoto.this,TuPianXianQing.class));
                    }
                }
            });
        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.takePic:
                mCameraSurfaceView.takePicture();
                timer.schedule(task, 50, 50);
                mjindu = jindu.getText().toString();
                mweidu = weidu.getText().toString();
                mfangxiang = fangxiang.getText().toString();
                break;
            default:
                break;
        }
    }

    @Override
    public void autoFocus() {
        mCameraSurfaceView.setAutoFocus();
    }
    private void checkPermissions() {
        mPermissionHelper = PermissionHelper.getInstance(ZiDingYiPhoto.this);
        mPermissionHelper.request(MULTI_PERMISSIONS);
    }

    private void initData() {
        gpsLocationManager = GPSLocationManager.getInstances(ZiDingYiPhoto.this);
    }
    //Android6.0申请权限的回调方法
    public void showContacts(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                ) {
            Toast.makeText(getApplicationContext(),"没有权限,请手动开启定位权限",Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(ZiDingYiPhoto.this,new String[]
                    {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, BAIDU_READ_PHONE_STATE);
        }else{
            gpsLocationManager.start(new MyListener());
            Toast.makeText(this,"权限以申请",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    gpsLocationManager .start(new MyListener());
                } else {
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPermissionGranted(@NonNull String[] permissionName) { }

    @Override
    public void onPermissionDeclined(@NonNull String[] permissionName) { }

    @Override
    public void onPermissionPreGranted(@NonNull String permissionsName) { }

    @Override
    public void onPermissionNeedExplanation(@NonNull String permissionName) { }

    @Override
    public void onPermissionReallyDeclined(@NonNull String permissionName) { }

    @Override
    public void onNoPermissionNeeded() { }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] >= 0f && event.values[0] < 5f
                || event.values[0] >= 315f && event.values[0] < 360f){
            fangxiang.setText("方向角：正北方向");
        }else if (event.values[0] >= 5f && event.values[0] < 45f){
            float nan = 90-event.values[0];
            fangxiang.setText("方向角：东偏北"+(int)nan+"度");
        }else if (event.values[0] >= 45f && event.values[0] <= 90f ){
            fangxiang.setText("方向角：正东方向");
        }else if (event.values[0] >= 90f && event.values[0] < 135f) {
            float xi= 180-event.values[0];
            fangxiang.setText("方向角：东偏南"+(int)xi+"度");
        } else if ((event.values[0] >= 135f && event.values[0] <= 180f)) {
            fangxiang.setText("方向角：正南方向");
        } else if (event.values[0] >= 180f && event.values[0] < 225f) {
            float bei = 270 - event.values[0];
            fangxiang.setText("方向角：西偏南"+(int)bei+"度");
        } else if (event.values[0] >= 225f && event.values[0] < 270f) {
            fangxiang.setText("方向角：正西方向");
        } else if (event.values[0] >= 270f && event.values[0] < 315f) {
            float dong = 360 - event.values[0];
            fangxiang.setText("方向角：西偏北"+(int)dong+"度");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this); // 解除监听器注册
        MobclickAgent.onResume(this);
        MobclickAgent.onProfileSignIn("GongChengXiangJi");
        MobclickAgent.onProfileSignIn("WB","GongChengXiangJi");
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gyroSensor,
                SensorManager.SENSOR_DELAY_NORMAL); //为传感器注册监听器
        MobclickAgent.onResume(this);
        //错误统计
        MobclickAgent.setCatchUncaughtExceptions(true);
    }
    class MyListener implements GPSLocationListener {
        @Override
        public void UpdateLocation(Location location) {
            if (location != null) {
                jindu.setText("经度："+location.getLongitude());
                weidu.setText("纬度："+location.getLatitude());
            }
        }

        @Override
        public void UpdateStatus(String provider, int status, Bundle extras) {
            if ("gps" == provider) {
                Toast.makeText(ZiDingYiPhoto.this, "定位类型：" + provider, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void UpdateGPSProviderStatus(int gpsStatus) {
            switch (gpsStatus) {
                case GPSProviderStatus.GPS_ENABLED:
                    Toast.makeText(ZiDingYiPhoto.this, "GPS开启", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_DISABLED:
                    Toast.makeText(ZiDingYiPhoto.this, "GPS关闭", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_OUT_OF_SERVICE:
                    Toast.makeText(ZiDingYiPhoto.this, "GPS不可用", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_TEMPORARILY_UNAVAILABLE:
                    Toast.makeText(ZiDingYiPhoto.this, "GPS暂时不可用", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_AVAILABLE:
                    Toast.makeText(ZiDingYiPhoto.this, "GPS可用啦", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}
