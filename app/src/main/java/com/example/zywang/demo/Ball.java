package com.example.zywang.demo;

import static com.example.zywang.demo.Constant.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;


public class Ball {
	int mProgram;
	int muMVPMatrixHandle;

	int maTexCoorHandle;
	FloatBuffer   mTexCoorBuffer;

	int maPositionHandle;
	int muSTMatrixHandle;
	int muRHandle;
	String mVertexShader;
	String mFragmentShader;

	FloatBuffer mVertexBuffer;
	int vCount = 0;
	float yAngle = 0;
	float xAngle = 0;
	float zAngle = 0;

	float r = 3f;
	public Ball(MySurfaceView mv) {
		initVertexData();
		initShader(mv);
	}

	public void initVertexData() {
		ArrayList<Float> alVertix = new ArrayList<Float>();
		final int angleSpan = 15;
		for (int vAngle = -90; vAngle < 90; vAngle = vAngle + angleSpan)
		{
			for (int hAngle = 0; hAngle < 360; hAngle = hAngle + angleSpan)
			{
				float x0 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle)) * Math.cos(Math
						.toRadians(hAngle)));
				float y0 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle)) * Math.sin(Math
						.toRadians(hAngle)));
				float z0 = (float) (r * UNIT_SIZE * Math.sin(Math
						.toRadians(vAngle)));

				float x1 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle)) * Math.cos(Math
						.toRadians(hAngle + angleSpan)));
				float y1 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle)) * Math.sin(Math
						.toRadians(hAngle + angleSpan)));
				float z1 = (float) (r * UNIT_SIZE * Math.sin(Math
						.toRadians(vAngle)));

				float x2 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
						.cos(Math.toRadians(hAngle + angleSpan)));
				float y2 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
						.sin(Math.toRadians(hAngle + angleSpan)));
				float z2 = (float) (r * UNIT_SIZE * Math.sin(Math
						.toRadians(vAngle + angleSpan)));

				float x3 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
						.cos(Math.toRadians(hAngle)));
				float y3 = (float) (r * UNIT_SIZE
						* Math.cos(Math.toRadians(vAngle + angleSpan)) * Math
						.sin(Math.toRadians(hAngle)));
				float z3 = (float) (r * UNIT_SIZE * Math.sin(Math
						.toRadians(vAngle + angleSpan)));
				float xs=(float)0.5;
//				if(z0>xs*r){ z0=xs*r;}
//				if(z1>xs*r){ z1=xs*r;}
//				if(z2>xs*r){ z2=xs*r;}
//				if(z3>xs*r){ z3=xs*r;}
				alVertix.add(x0);
				alVertix.add(y0);
				alVertix.add(z0);
				alVertix.add(x3);
				alVertix.add(y3);
				alVertix.add(z3);
				alVertix.add(x1);
				alVertix.add(y1);
				alVertix.add(z1);


				alVertix.add(x1);
				alVertix.add(y1);
				alVertix.add(z1);
				alVertix.add(x3);
				alVertix.add(y3);
				alVertix.add(z3);
				alVertix.add(x2);
				alVertix.add(y2);
				alVertix.add(z2);



			}
		}
		vCount = alVertix.size() / 3;

		float vertices[] = new float[vCount * 3];
		for (int i = 0; i < alVertix.size(); i++) {
			vertices[i] = alVertix.get(i);
		}

		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
		//将alTexCoor中的纹理坐标值转存到一个float数组中
		float[] texCoor=generateTexCoor(//获取切分整图的纹理数组
				(int)(360/angleSpan), //纹理图切分的列数
				(int)(180/angleSpan)  //纹理图切分的行数
		);
		ByteBuffer llbb = ByteBuffer.allocateDirect(texCoor.length*4);
		llbb.order(ByteOrder.nativeOrder());//设置字节顺序
		mTexCoorBuffer=llbb.asFloatBuffer();
		mTexCoorBuffer.put(texCoor);
		mTexCoorBuffer.position(0);
	}

	public void initShader(MySurfaceView mv) {
		mVertexShader = com.example.zywang.demo.ShaderUtil.loadFromAssetsFile("vertex.sh",
				mv.getResources());
		mFragmentShader = com.example.zywang.demo.ShaderUtil.loadFromAssetsFile("frag.sh",
				mv.getResources());
		mProgram = com.example.zywang.demo.ShaderUtil.createProgram(mVertexShader, mFragmentShader);

		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");

		//muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");

		maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");

		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		//muRHandle = GLES20.glGetUniformLocation(mProgram, "uR");
	}

	public void drawSelf(int texId) {
		String TAG="drawself";
		Log.e(TAG,"run");
		
    	MatrixState.rotate(xAngle, 1, 0, 0);
    	MatrixState.rotate(yAngle, 0, 1, 0);
    	MatrixState.rotate(zAngle, 0, 0, 1);
		GLES20.glUseProgram(mProgram);
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0);
		//GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, MatrixState.mSTMatrix, 0);

		GLES20.glUniform1f(muRHandle, r * UNIT_SIZE);

		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
				false, 3 * 4, mVertexBuffer);

		GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT,
				false, 2*4, mTexCoorBuffer);

		GLES20.glEnableVertexAttribArray(maPositionHandle);
		GLES20.glEnableVertexAttribArray(maTexCoorHandle);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,texId);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);

	}

	//自动切分纹理产生纹理数组的方法
	public float[] generateTexCoor(int bw,int bh){
		float[] result=new float[bw*bh*6*2];
		float sizew=1.0f/bw;//列数
		float sizeh=1.0f/bh;//行数
		int c=0;
		for(int i=0;i<bh;i++){
			for(int j=0;j<bw;j++){
				//每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
				float s=j*sizew;
				float t=i*sizeh;
				result[c++]=s;
				result[c++]=t;
				result[c++]=s;
				result[c++]=t+sizeh;
				result[c++]=s+sizew;
				result[c++]=t;
				result[c++]=s+sizew;
				result[c++]=t;
				result[c++]=s;
				result[c++]=t+sizeh;
				result[c++]=s+sizew;
				result[c++]=t+sizeh;
			}}
		return result;
	}
}
