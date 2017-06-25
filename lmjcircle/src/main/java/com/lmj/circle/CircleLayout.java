package com.lmj.circle;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Created by lmj on 2016/11/16 0016. on 下午 7:36
 * limengjie
 */
public class CircleLayout extends FrameLayout {
    private ArrayList<View> childsViewList;
    private int width;
    private int height;
    private VelocityTracker mVelocityTracker;
    private int mMaxVelocity;
    private int mPointerId;
    private float velocityX;
    private float velocityY;
    private float flingSX;
    private float flingSy;
    private ValueAnimator valueAnimator;
    private double ca;
    private double da;


    public CircleLayout(Context context) {
        this(context, null);
    }

    public CircleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        childsViewList = new ArrayList<View>();
        mMaxVelocity = ViewConfiguration.get(context).getMaximumFlingVelocity();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddiingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        int childCount = getChildCount();
        double angle = 360 / childCount * Math.PI / 180;
        int x = 0, y = 0;
        int maxWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int tw = child.getMeasuredWidth();
            maxWidth = maxWidth > tw ? maxWidth : tw;
            int th = child.getMeasuredHeight();
            maxHeight = maxHeight > th ? maxHeight : th;
        }
        int r = Math.min(width - paddingLeft - paddingRight, height - paddiingTop - paddingBottom) / 2 - Math.max(maxWidth / 2, maxHeight / 2);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            x = (int) (Math.cos(angle * i + cPianyi) * r) + width / 2 - child.getMeasuredWidth() / 2;
            y = (int) (Math.sin(angle * i + cPianyi) * r) + height / 2 - child.getMeasuredHeight() / 2;
            child.layout(x, y, x + child.getMeasuredWidth(), y + child.getMeasuredHeight());
        }

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private float sX = 0;
    private float sY = 0;
    private double sa = 0;
    private double cPianyi = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        acquireVelocityTracker(event);
        final VelocityTracker verTracker = mVelocityTracker;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                sX = event.getX() - width / 2;
                sY = event.getY() - height / 2;
                sa = getAngle(sX, sY);
                mPointerId = event.getPointerId(0);
                if (null != valueAnimator) {
                    valueAnimator.cancel();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float cX = event.getX() - width / 2;
                float cY = event.getY() - height / 2;
                ca = getAngle(cX, cY);
                da = ca - sa;
                if (da < -Math.PI) {
                    da = Math.abs(2 * Math.PI + da);
                } else if (da > Math.PI) {
                    da = -Math.abs(2 * Math.PI - da);
                }
                cPianyi = cPianyi + da;
                Log.i("aaa", "cPianyi：" + da + ",ca:" + ca + ",sa:" + sa);
                fixPianyi();
                sa = ca;
                requestLayout();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                verTracker.computeCurrentVelocity(1000, mMaxVelocity);
                velocityX = verTracker.getXVelocity(mPointerId);
                velocityY = verTracker.getYVelocity(mPointerId);
                velocityX = Math.max(Math.abs(velocityX), Math.abs(velocityY));
                if (velocityX > 1000) {
                    flingSX = event.getX();
                    flingSy = event.getY();
                    valueAnimator = new ValueAnimator();
                    valueAnimator.setDuration(2000);
                    valueAnimator.setInterpolator(new DecelerateInterpolator());
                    valueAnimator.setFloatValues(0, 1.0f);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public float px;

                        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float fraction = animation.getAnimatedFraction();
                            float cx = flingSX + velocityX * fraction;
                            double flingangle = Math.abs(cx - px) * (Math.PI / 180);
                            px = cx;
                            if (da > 0) {
                                flingangle = -flingangle;
                            }
                            cPianyi = cPianyi - flingangle / 10;
                            fixPianyi();

                            requestLayout();
                        }
                    });
                    valueAnimator.start();
                }

                releaseVelocityTracker();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public void fixPianyi() {
        if (cPianyi > Math.PI * 2) {
            cPianyi = cPianyi - ((int) (cPianyi / Math.PI * 2)) * Math.PI * 2;
        } else if (cPianyi < -Math.PI * 2) {
            cPianyi = cPianyi + ((int) (Math.abs(cPianyi) / Math.PI * 2)) * Math.PI * 2;
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                return true;
            case MotionEvent.ACTION_MOVE:

                break;

        }
        return super.onTouchEvent(event);
    }

    public double getAngle(float x, float y) {
        if (y == 0 && x >= 0) {
            return 0;
        } else if (x == 0 && y >= 0) {
            return 90;
        } else if (y == 0 && x < 0) {
            return 180;
        } else if (x == 0 && y < 0) {
            return 270;
        }

        double sA = Math.asin(Math.abs(y) / Math.sqrt(x * x + y * y));

        if (x >= 0 && y >= 0) {
            return sA;
        } else if (x <= 0 && y >= 0) {
            return Math.PI - sA;
        } else if (x <= 0 && y <= 0) {
            return Math.PI + sA;
        } else if (x >= 0 && y <= 0) {
            return Math.PI + Math.PI / 2 + Math.asin(Math.abs(x) / Math.sqrt(x * x + y * y));
        }
        return 0;
    }

    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
