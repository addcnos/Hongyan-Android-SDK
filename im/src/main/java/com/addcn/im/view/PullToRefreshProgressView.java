package com.addcn.im.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.addcn.im.R;


/**
 * Author:YangBin
 *  Progress
 */
public class PullToRefreshProgressView extends View{

    //圆环进度颜色
    private int ringProgressColor;

    //圆环的宽度
    private float ringWidth;

    //最大值
    private int ringMax;

    //中间X坐标
    private int  centerX;

    //中间Y坐标
    private int  centerY;

    //画笔
    private Paint mPaint;

    //View宽度
    private int width;

    //View高度
    private int height;

    //进度
    private int progress;

    //半径
    private int radius;


    public PullToRefreshProgressView(Context context) {
        this(context,null);
    }

    public PullToRefreshProgressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PullToRefreshProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshProgressView);
        ringProgressColor=typedArray.getColor(R.styleable.PullToRefreshProgressView_ringProgressColor, Color.GRAY);
        ringWidth=typedArray.getDimension(R.styleable.PullToRefreshProgressView_ringWidth, 5);
        ringMax=typedArray.getInteger(R.styleable.PullToRefreshProgressView_ringmax, 50);
        init();

    }

    private void init() {
        mPaint=new Paint();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);

        if(widthMode==MeasureSpec.AT_MOST) {
            width=dp2px(30);
        } else {
            width=widthSize;
        }
        if(heightMode==MeasureSpec.AT_MOST) {
            height=dp2px(30);
        } else {
            height=heightSize;
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //获取中心点的位置
        centerX=getWidth()/2;
        centerY=getHeight()/2;
        radius=(int) (centerX - ringWidth / 2);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //确定View的宽高
        width = w;
        height = h;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawProgress(canvas);
    }

    /**
     * 绘制进度条
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {

        mPaint.setAntiAlias(true);
        mPaint.setColor(ringProgressColor);
        mPaint.setStrokeWidth(ringWidth);
        //设置画笔样式
        mPaint.setStyle(Paint.Style.STROKE);
        RectF rectF=new RectF(centerX-radius,centerY-radius,centerX+radius,centerY+radius);
        //绘制圆弧
        canvas.drawArc(rectF,-90,360*progress/ringMax-10,false,mPaint);
    }

    /**
     * dp转px
     * @param dp
     * @return
     */
    public int dp2px(int dp){
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    /**
     * 设置进度
     * @param progress
     */
    public  synchronized  void setProgress(int progress){
        if(progress<0){
            progress=0;
        }
        if(progress>=ringMax){
            progress=ringMax;
        }
        this.progress=progress;
        postInvalidate();
    }

}
