package com.example.zywang.demo;
import java.nio.ByteBuffer;
import android.opengl.Matrix;
import android.util.Log;

public class MatrixState 
{
	static float[] mSTMatrix = new float[16];
	private static float[] mProjMatrix = new float[16];
    private static float[] mVMatrix = new float[16];

    private static float[] currMatrix;
	private static float [] Camera_c = {0,0,0};
	private static float [] Camera_t = {1,0,0};
	private static float [] Camera_up = {0,0,-1};
	private static float [] angel={0,0};
      

    static float[][] mStack=new float[10][16];
    static int stackTop=-1;
    
    public static void setInitStack()
    {
    	currMatrix=new float[16];
    	Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
    }
    
    public static void pushMatrix()
    {
    	stackTop++;
    	for(int i=0;i<16;i++)
    	{
    		mStack[stackTop][i]=currMatrix[i];
    	}
    }
    
    public static void popMatrix()
    {
    	for(int i=0;i<16;i++)
    	{
    		currMatrix[i]=mStack[stackTop][i];
    	}
    	stackTop--;
    }
    
    public static void translate(float x,float y,float z)
    {
    	Matrix.translateM(currMatrix, 0, x, y, z);
    }
    
    public static void rotate(float angle,float x,float y,float z)
    {
    	Matrix.rotateM(currMatrix,0,angle,x,y,z);
    }
    

    static ByteBuffer llbb= ByteBuffer.allocateDirect(3*4);
    static float[] cameraLocation=new float[3];
	public static void updateCamera(float M_x,float M_y){
		angel[0]=(float)((angel[0]-M_x)%(2*Math.PI));
		angel[1]=(float)((angel[1]-M_y)%(2*Math.PI));
		Camera_t[0]= (float) (Math.cos(angel[1])*Math.cos(angel[0]));
		Camera_t[1]= (float) (Math.cos(angel[1])*Math.sin(angel[0]));
		Camera_t[2]= (float) (Math.sin(angel[1]));
		Camera_up[0]= (float) (Math.sin(angel[1])*Math.cos(angel[0]));
		Camera_up[1]= (float) (Math.sin(angel[1])*Math.sin(angel[0]));
		Camera_up[2]=(float) (-Math.cos(angel[1]));
		String msg=String.format("%f,%f",angel[0],angel[1]);
		Log.e("angel",msg);
		setCamera();

	}
	public static void setCamera()
	{

		String msg1=String.format("%f,%f",Camera_t[0],Camera_up[0]);
		Log.e("angel data",msg1);
		Matrix.setLookAtM
				(
						mVMatrix,
						0,
						Camera_c[0],
						Camera_c[1],
						Camera_c[2],
						Camera_t[0],
						Camera_t[1],
						Camera_t[2],
						Camera_up[0],
						Camera_up[1],
						Camera_up[2]
				);
	}

    public static void setProjectFrustum
    ( 
    	float left,
    	float right,
    	float bottom,
    	float top,
    	float near,
    	float far
    )
    {
    	Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    public static void setProjectOrtho
    (
    	float left,
    	float right,
    	float bottom,
    	float top,
    	float near,
    	float far
    )
    {    	
    	Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    static float[] mMVPMatrix=new float[16];
    public static float[] getFinalMatrix()
    {	
    	Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);        
        return mMVPMatrix;
    }

    public static float[] getMMatrix()
    {       
        return currMatrix;
    }
}
