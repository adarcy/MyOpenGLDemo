package com.syxl.adarcy.myopengldemo.opengl.render;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.View;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by likun on 2018/2/20.
 */

public class Oval extends Shape {


    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "void main(){" +
                    "gl_Position = vMatrix*vPosition;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
            -0.5f,  0.5f, 0.0f,
            -0.5f,  -0.5f, 0.0f, // top
            0.5f, -0.5f, 0.0f, // bottom left
            0.5f, 0.5f, 0.0f  // bottom right
    };

    static short index[]={
            0,1,2,0,2,3
    };

    private int mPositionHandle;
    private int mColorHandle;
    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    //顶点之间的偏移量
//    private final int vertexStride = COORDS_PER_VERTEX * 4; // 每个顶点四个字节
    private final int vertexStride = 0; // 每个顶点四个字节
    private int mMatrixHandler;

    //设置颜色，依次为红绿蓝和透明通道
    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };

    private int program;
    private int n = 360;

    private float radius = 1.0f;
    private float[] shapePos;


    public Oval(View view) {
        super(view);
        shapePos = createPositions();

        ByteBuffer bb = ByteBuffer.allocateDirect(shapePos.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(shapePos);
        vertexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
    }

    private float[]  createPositions(){
        ArrayList<Float> data=new ArrayList<>();
        data.add(0.0f);             //设置圆心坐标
        data.add(0.0f);
        data.add(0.0f);
        float angDegSpan=360f/n;
        for(float i=0;i<360+angDegSpan;i+=angDegSpan){
            data.add((float) (radius*Math.sin(i*Math.PI/180f)));
            data.add((float)(radius*Math.cos(i*Math.PI/180f)));
            data.add(0.0f);
        }
        float[] f=new float[data.size()];
        for (int i=0;i<f.length;i++){
            f[i]=data.get(i);
        }
        return f;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = (float) width/height;
        Matrix.frustumM(mProjectMatrix,0,-ratio,ratio,-1,1,3,7);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0, 0, 0, 0, 1.0f, 0);
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);

    }

    public void setMatrix(float[] matrix) {
        this.mMVPMatrix = matrix;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glUseProgram(program);
        mMatrixHandler = GLES20.glGetUniformLocation(program,"vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);

        mPositionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle,COORDS_PER_VERTEX,GLES20.GL_FLOAT,
                false,vertexStride,vertexBuffer);

        mColorHandle = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        //顶点法
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, shapePos.length / 3);
        //索引法正方形
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.length,GLES20.GL_UNSIGNED_SHORT,indexBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
