package example.com.gongchengxiangji;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.umeng.analytics.MobclickAgent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import example.com.gongchengxiangji.glide.GlideImageMassage;
import example.com.gongchengxiangji.view.CameraSurfaceView;

public class TuPianXianQing extends AppCompatActivity implements View.OnClickListener {
    private ImageView pic;
    private TextView jindu, weidu, name, time, fangxiang;
    private Button baocun_btn, quxiao_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tu_pian_xian_qing);
        ById();
        name.setText("文件名：" + CameraSurfaceView.filePath.substring(19, 37));
        Log.e("--asdad",CameraSurfaceView.filePath.substring(19, 37));
        jindu.setText(ZiDingYiPhoto.mjindu);
        weidu.setText(ZiDingYiPhoto.mweidu);
        fangxiang.setText(ZiDingYiPhoto.mfangxiang);
        time.setText("时间：" + CameraSurfaceView.time);
        File file = new File(CameraSurfaceView.filePath);
        if (file.exists()) {
            String  url= CameraSurfaceView.filePath;
            //将图片显示到ImageView中
            GlideImageMassage.glideLoader(this,url,R.mipmap.ic_launcher,R.mipmap.ic_launcher,pic,1);
        }
    }

    private void ById() {
        pic = (ImageView) findViewById(R.id.wenjian_pic);
        jindu = (TextView) findViewById(R.id.wenjian_jindu);
        weidu = (TextView) findViewById(R.id.wenjian_weidu);
        name = (TextView) findViewById(R.id.wenjian_name);
        time = (TextView) findViewById(R.id.wenjian_time);
        fangxiang = (TextView) findViewById(R.id.wenjian_fangxiang);
        baocun_btn = (Button) findViewById(R.id.wenjian_baocun);
        quxiao_btn = (Button) findViewById(R.id.wenjian_quxiao);
        baocun_btn.setOnClickListener(this);
        quxiao_btn.setOnClickListener(this);
    }

    // 写一个文件到SDCard
    private void writeFileToSDCard() throws IOException {
        // 比如可以将一个文件作为普通的文档存储，那么先获取系统默认的文档存放根目录
        File parent_path = Environment.getExternalStorageDirectory();
        // 可以建立一个子目录专门存放自己专属文件
        File dir = new File(parent_path.getAbsoluteFile(), "cameratool");
        dir.mkdir();
        File file = new File(dir.getAbsoluteFile(),
                CameraSurfaceView.filePath.substring(CameraSurfaceView.filePath.indexOf("2"),CameraSurfaceView.filePath.indexOf("."))+".txt");
        // 创建这个文件，如果不存在
        file.createNewFile();
        OutputStream fos = new FileOutputStream(file);
        String fileName = name.getText().toString();
        String fileJinDu = jindu.getText().toString();
        String fileWeiDu = weidu.getText().toString();
        String fileFangXiang = fangxiang.getText().toString();
        String fileTime = time.getText().toString();
        String data = "\n"+"------------------"+"\n"+fileName+"\n"+fileJinDu
                +"\n"+fileWeiDu+"\n"+fileFangXiang+"\n"+fileTime;
        byte[] buffer = data.getBytes();
        // 开始写入数据到这个文件。
        fos.write(buffer);
        fos.flush();
        fos.close();
        Toast.makeText(TuPianXianQing.this,"保存路径："+CameraSurfaceView.filePath,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wenjian_baocun:
                MobclickAgent.onEvent(this,"GongChengXiangJi_BaoCun");
                    // 首先判断设备是否挂载SDCard
                    boolean isMounted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                    if (isMounted) {
                        try {
                            writeFileToSDCard();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("SDCard错误", "未安装SDCard！");
                    }
                startActivity(new Intent(TuPianXianQing.this, MainActivity.class));
                break;
            case R.id.wenjian_quxiao:
                startActivity(new Intent(TuPianXianQing.this, MainActivity.class));
                break;
        }
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        //错误统计
        MobclickAgent.setCatchUncaughtExceptions(true);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        //错误统计
        MobclickAgent.setCatchUncaughtExceptions(true);
    }
}
