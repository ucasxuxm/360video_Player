package com.example.zywang.demo;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//@SuppressLint("ViewConstructor")
class MySurfaceView extends GLSurfaceView
{
    private  String TAG = "MySurfaceView";
    //private MediaPlayer mMediaPlayer;
	private final float TOUCH_SCALE_FACTOR = 1.0f/1024;
    private SceneRenderer mRenderer;
    Ball ball;

    //int textureId;//系统分配的纹理id
	
	private float mPreviousY;
    private float mPreviousX;
	public MySurfaceView(Context context,MediaPlayer mp) {
        super(context);
        this.setEGLContextClientVersion(2);
        //mMediaPlayer=mp;
        mRenderer = new SceneRenderer(context,mp);
        setRenderer(mRenderer);
    }
//    @Override
//    public void onResume() {
//        queueEvent(new Runnable(){
//            public void run() {
//                mRenderer.setMediaPlayer(mMediaPlayer);
//            }});
//
//        super.onResume();
//    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;
            float dx = x - mPreviousX;
            MatrixState.updateCamera(dx*TOUCH_SCALE_FACTOR,dy*TOUCH_SCALE_FACTOR);
            String str = String.format("%f\t%f",dx,dy);
            Log.e("dx dy",str);
        }
        mPreviousY = y;
        mPreviousX = x;
        return true;
    }
	private class SceneRenderer implements Renderer
    {
        private  String TAG = "SceneRenderer";
        private int mTextureID;
        private SurfaceTexture mSurface;
        private boolean updateSurface = false;
        //private float[] mSTMatrix = new float[16];

        private MediaPlayer mMediaPlayer;
        public SceneRenderer(Context context,MediaPlayer mp){
            mMediaPlayer=mp;

        }
//        public void  setMediaPlayer(MediaPlayer mp){mMediaPlayer=mp;}



        public void onDrawFrame(GL10 gl)
        {
            synchronized(this) {
                if (updateSurface) {
                    Log.e(TAG,"onDrawFrame");
                    mSurface.updateTexImage();
                    //mSurface.getTransformMatrix(MatrixState.mSTMatrix);
                    updateSurface = false;
                }
            }

            GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
            GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT);

            ball.drawSelf(mTextureID);
            GLES20.glFinish();


        }  

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            Constant.ratio = (float)width/(float)height;
            MatrixState.setProjectFrustum(-Constant.ratio, Constant.ratio, -1, 1, (float)1.8, 3);
            MatrixState.setCamera();
            //textureId=initTexture(R.drawable.pic);

            MatrixState.setInitStack();
            //mMediaPlayer.start();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0f,0f,0f, 1.0f);
            ball=new Ball(MySurfaceView.this);
//            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            GLES20.glEnable(GLES20.GL_CULL_FACE);//开启背面剪裁

            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);

            mTextureID = textures[0];
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);
            checkGlError("glBindTexture mTextureID");

            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);

            /*
             * Create the SurfaceTexture that will feed this textureID,
             * and pass it to the MediaPlayer
             */
            mSurface = new SurfaceTexture(mTextureID);
            mSurface.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener(){

                @Override
                synchronized public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    Log.e(TAG,"onFrameAvailable");
                    updateSurface = true;
                }
            });

            Surface surface = new Surface(mSurface);
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            surface.release();
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e(TAG,"onPrepared");
                    mp.start();
                }
            });

            synchronized(this) {
                updateSurface = false;
            }



        }

    }
    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }



}
