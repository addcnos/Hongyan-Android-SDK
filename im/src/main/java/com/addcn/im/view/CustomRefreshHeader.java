package com.addcn.im.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.addcn.im.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * Author:YangBin
 * SmartRefreshLayout Header
 */
public class CustomRefreshHeader extends LinearLayout implements RefreshHeader {

    /**
     * 指示下拉和释放的箭头
     */
    private ImageView arrow;

    /**
     * 指示下拉和释放的文字描述
     */
    private TextView description;
    private boolean hasSetPullDownAnim = false;
    /**
     * 进度条
     **/
    private PullToRefreshProgressView pb_progress;
    /**
     * 旋转动画
     **/
    private RotateAnimation refreshingAnimation;

    public CustomRefreshHeader(Context context) {
        this(context, null, 0);
    }

    public CustomRefreshHeader(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRefreshHeader(Context context,   AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View header = View.inflate(context, R.layout.layout_pull_to_refresh, this);
        arrow = (ImageView) header.findViewById(R.id.arrow);
        description = (TextView) header.findViewById(R.id.description);
        pb_progress = (PullToRefreshProgressView) header.findViewById(R.id.ptpv_view);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotating);
        LinearInterpolator lir = new LinearInterpolator();
        refreshingAnimation.setInterpolator(lir);
        pb_progress.setProgress(0);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {

    }

    /**
     * 状态改变时调用。在这里切换第三阶段的动画卖萌小人
     *
     * @param refreshLayout
     * @param oldState
     * @param newState
     */
    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case PullDownToRefresh: //下拉刷新开始。正在下拉还没松手时调用
                //每次重新下拉时，将图片资源重置为小人的大脑袋
                arrow.setVisibility(View.VISIBLE);
                description.setText(getResources().getString(R.string.pull_to_refresh));
                pb_progress.setVisibility(View.VISIBLE);
                break;
            case Refreshing: //正在刷新。只调用一次
                //状态切换为正在刷新状态时，设置图片资源为小人卖萌的动画并开始执行
                description.setText(getResources().getString(R.string.refreshing));
                arrow.setVisibility(View.GONE);
                pb_progress.startAnimation(refreshingAnimation);
                break;
            case ReleaseToRefresh:
                description.setText(getResources().getString(R.string.release_to_refresh));
                arrow.setVisibility(View.GONE);
                pb_progress.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }


    /**
     * 动画结束后调用
     */
    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        // 结束动画
        if (pb_progress != null) {
            pb_progress.clearAnimation();
        }
        //重置状态
        hasSetPullDownAnim = false;
        return 0;
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        //isDragging true 手指正在拖动
        if (isDragging){
            // 下拉的百分比小于100%时，不断调用 setScale 方法改变图片大小
            if (percent < 1) {
                pb_progress.setProgress(Math.abs((int) (percent * 100)));
                //是否执行过翻跟头动画的标记
                if (hasSetPullDownAnim) {
                    hasSetPullDownAnim = false;
                }
            }
            //当下拉的高度达到Header高度100%时，开始加载正在下拉的初始动画，即翻跟头
            if (percent >= 1.0) {
                //因为这个方法是不停调用的，防止重复
                if (!hasSetPullDownAnim) {
//                pb_progress.startAnimation(refreshingAnimation);
                    pb_progress.setProgress(Math.abs((int) (percent * 100)));
                    hasSetPullDownAnim = true;
                }
            }
        }
    }

    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int extendHeight) {

    }



    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }


}
