package com.example.zywang.demo;

import android.app.Activity;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final int FILE_SELECT_CODE = 1;
    private static final String TAG = "MainActivity";
    protected Resources mResources;
    private MediaPlayer mMediaPlayer;
    private MySurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResources = getResources();
        mMediaPlayer = new MediaPlayer();
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        Button play = (Button) findViewById(R.id.play);
//        Button pause = (Button) findViewById(R.id.pause);
//        Button replay = (Button) findViewById(R.id.replay);
        Button choice = (Button) findViewById(R.id.choice);//按钮的初始化
        choice.setOnClickListener(this);
//        play.setOnClickListener(this);
//        pause.setOnClickListener(this);
//        replay.setOnClickListener(this);//给按钮加监听
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);//判断你是否授权
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "允许权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "拒绝权限将无法访问程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public void onClick(View v) {//各按钮的功能
//        switch (v.getId()) {
//            case R.id.play:
//                if (!videoView.isPlaying()) {//播放
//                    videoView.start();
//                }
//                break;
//            case R.id.pause:
//                if (videoView.isPlaying()) {//暂停
//                    videoView.pause();
//                }
//                break;
//            case R.id.replay:
//                if (videoView.isPlaying()) {
//                    videoView.resume();//重新播放
//                }
//                break;
           // case R.id.choice://选择文件
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");//设置类型，这是任意类型
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
        }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            //将选择的文件路径给播放器
                try {
                    mMediaPlayer.setDataSource(this, uri);
                }catch (IOException e){
                    e.printStackTrace();
                }
                 //初始化GLSurfaceView
                mGLSurfaceView = new MySurfaceView(this,mMediaPlayer);
                // 切换到主界面
                setContentView(mGLSurfaceView);
                //setContentView();
            super.onActivityResult(requestCode, resultCode, data);
            return;
    }
        if (requestCode == FILE_SELECT_CODE) {
            Uri uri = data.getData();
            Log.i(TAG, "------->" + uri.getPath());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
//    protected void onResume() {
//        super.onResume();
//        mGLSurfaceView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mGLSurfaceView.onPause();
//    }
//    public void choseFile(){
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("video/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        try {
//            startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
//        }
//
//    }
}
