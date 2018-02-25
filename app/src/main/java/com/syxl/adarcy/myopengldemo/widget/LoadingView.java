package com.syxl.adarcy.myopengldemo.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.syxl.adarcy.myopengldemo.R;


/**
 * Created by likun on 2018/2/13.
 */

public class LoadingView extends View {

    public static final int LINE_NUM = 12;
    public static final int PER_DEGREE = 360/LINE_NUM;
    private Paint mPaint;
    private int currentDegree = 0;
    private int animatedValue;
    private ValueAnimator animator;
    private int viewSize;
    private int color;


    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LoadingView);
        viewSize = typedArray.getDimensionPixelSize(R.styleable.LoadingView_loading_view_size, dp2px(32));
        color = typedArray.getColor(R.styleable.LoadingView_color, Color.BLACK);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //否则view是march parent的
        setMeasuredDimension(viewSize,viewSize);
    }

    private int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (density* dp +0.5);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //记住translate层次，去除历史位移
//        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null,Canvas.ALL_SAVE_FLAG);
        drawLoadingView(canvas);
//        canvas.restoreToCount(saveCount);
    }

    private void drawLoadingView(Canvas canvas) {
        int width  = viewSize /2;
        int height = viewSize /3;
        int offset = viewSize /6;
        canvas.rotate(animatedValue * PER_DEGREE,width,width);
        canvas.translate(width,width);
        for (int i= 0;i<LINE_NUM;i++) {
            canvas.rotate(PER_DEGREE);
            mPaint.setAlpha(255 * (i+1)/LINE_NUM);
            canvas.drawLine(offset,0,height,0,mPaint);
        }
    }

    private void start() {
        if (animator == null) {
            animator = ValueAnimator.ofInt(0, LINE_NUM - 1);
            animator.setDuration(1000);
            animator.addUpdateListener(animatorListener);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        } else if (!animator.isStarted()){
            animator.start();
        }
    }

    private void stop() {
        if (animator != null) {
            animator.removeUpdateListener(animatorListener);
            animator.removeAllUpdateListeners();
            animator.cancel();
            animator = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    private ValueAnimator.AnimatorUpdateListener animatorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            animatedValue = (int) animation.getAnimatedValue();
            invalidate();
        }
    };
}
