package example.com.gongchengxiangji;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.umeng.analytics.MobclickAgent;
import java.io.File;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener {
    private Button bendi;
    private Button pai;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            // 透明状态栏
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        initViews();
    }

    private void initViews() {
        bendi = (Button) findViewById(R.id.natives);
        pai = (Button) findViewById(R.id.pai);
        bendi.setOnClickListener(this);
        pai.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.natives:
                try {
                    String getUrl = "/storage/emulated/0/cameratool/";
                    File file = new File(getUrl);
                    if(null==file || !file.exists()){
                        return;
                    }
                    Uri photoURI = FileProvider.getUriForFile
                            (this, this.getApplicationContext().getPackageName() + ".example.com.gongchengxiangji", file);
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(photoURI, "image/*");
                    startActivity(intent);
                    startActivity(Intent.createChooser(intent,getUrl));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.pai:
                MobclickAgent.onEvent(this,"GongChengXiangJi");
                intent= new Intent(MainActivity.this,ZiDingYiPhoto.class);
                startActivity(intent);
                break;

        }
    }

    public void onResume() {
        super.onResume();
        //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onPageStart("MainActivity");
        MobclickAgent.onResume(this);//统计时长
        /** 设置是否对日志信息进行加密, 默认false(不加密).
         * 6.0.0版本及以后 */
        MobclickAgent.enableEncrypt(true);
        //账号统计
        MobclickAgent.onProfileSignIn("GongChengXiangJi");
        //错误统计
        MobclickAgent.setCatchUncaughtExceptions(true);
    }
    public void onPause() {
        super.onPause();
        // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,
        // 因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPageEnd("MainActivity");
        //账号统计
        MobclickAgent.onProfileSignIn("GongChengXiangJi");
        MobclickAgent.onPause(this);
    }
}
