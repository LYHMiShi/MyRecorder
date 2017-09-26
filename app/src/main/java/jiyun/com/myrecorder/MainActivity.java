package jiyun.com.myrecorder;

import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SurfaceView my_surface;
    private Button record;
    private Button stop;
//  记录是否在录制
    private boolean isRecording = false;
    private File videoFile;
    private MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
//        设置横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//        设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        选择支持半透明模式，再有surfaceview的activity中使用
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
//        让stop按钮不可用
        stop.setEnabled(false);
//        设置分辨率
        my_surface.getHolder().setFixedSize(1280,720);
//        该组件让屏幕不会 自动关闭
        my_surface.getHolder().setKeepScreenOn(true);
    }

    private void initView() {
        my_surface = (SurfaceView) findViewById(R.id.my_surface);
        record = (Button) findViewById(R.id.record);
        stop = (Button) findViewById(R.id.stop);

        record.setOnClickListener(this);
        stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record:
                if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(MainActivity.this, "SD卡不存在，请插入SD卡", Toast.LENGTH_SHORT).show();
                    return;
                }
//                创建保存录制视频的视频文件
                try {
                    videoFile = new File(Environment.getExternalStorageDirectory().getCanonicalFile()+"/testVideos.3gp");
                    Log.e("TAG", Environment.getExternalStorageDirectory().getCanonicalFile()+"/testVideos.3gp");
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.reset();
//                    设置从麦克风采集声音
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    设置从摄像头采集图像
                    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//                    设置视频文件的输出格式
//                    必须在设置声音编码格式、图像编码格式之前
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                    设置声音编码的格式
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                    设置图像编码的格式
                    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                    mediaRecorder.setVideoSize(1280,720);
//                    每秒  4帧
                    mediaRecorder.setVideoFrameRate(20);
                    mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
//                    指定使用surfaceview来预览视频
                    mediaRecorder.setPreviewDisplay(my_surface.getHolder().getSurface());
                    mediaRecorder.prepare();
//                    开始录制
                    mediaRecorder.start();
                    Toast.makeText(MainActivity.this, "正在录制", Toast.LENGTH_SHORT).show();
//                    设置录制按钮不可点击
                    record.setEnabled(false);
//                    设置停止按钮可点击
                    stop.setEnabled(true);
                    isRecording = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.stop:
                if(isRecording) {
//                    停止 录制
                    mediaRecorder.stop();
//                    释放资源
                    mediaRecorder.release();
                    mediaRecorder = null;
//                    让两个按钮
                    record.setEnabled(true);
                    stop.setEnabled(false);

                }
                break;
        }
    }
}
