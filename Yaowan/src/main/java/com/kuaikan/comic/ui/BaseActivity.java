package com.kuaikan.comic.ui;

import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import com.kuaikan.comic.R;

/**
 * Created by skyfishjy on 12/19/14.
 */
public class BaseActivity extends ActionBarActivity {

    // 手势返回
    private static final int FLING_MIN_DISTANCE = 100;// 移动最小距离(dp)
    private static final int FLING_MIN_VELOCITY = 700;// 移动最小速度

    private Toolbar mActionBarToolbar;
    private VelocityTracker mVelocityTracker;
    private MotionEvent mCurrentDownEvent;
    private int mMaximumVelocity;
    private int mTouchSlop;
    boolean isDrag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTouchSlop = getResources().getDimensionPixelSize(R.dimen.touch_slop);
        final ViewConfiguration configuration = ViewConfiguration.get(getApplicationContext());
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    public Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //Log.d("ges", "dispatchTouchEvent x=" + event.getX() + " y=" + event.getY());
        if (isOnGestureBack(event)) {
            final int action = event.getAction() & MotionEventCompat.ACTION_MASK;

            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(event);

            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    // Log.d("ges", "dispatchTouchEvent ACTION_DOWN");

                    if (mCurrentDownEvent != null) {
                        mCurrentDownEvent.recycle();
                    }
                    mCurrentDownEvent = MotionEvent.obtain(event);

                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (mCurrentDownEvent == null) {
                        mCurrentDownEvent = MotionEvent.obtain(event);
                    }

                    final float x = MotionEventCompat.getX(event, 0);
                    final float xDiff = x - mCurrentDownEvent.getX();
                    final float y = MotionEventCompat.getY(event, 0);
                    final float yDiff = Math.abs(y - mCurrentDownEvent.getY()/*mLastMotionY*/) * 2;
                    if ((xDiff > (mTouchSlop * 3)) && (xDiff > yDiff)) {

                        isDrag = true;
                        event.setAction(MotionEvent.ACTION_CANCEL);
                    }

                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (isDrag) {

                        final VelocityTracker velocityTracker = mVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                        final float xVelocity = (int) velocityTracker.getXVelocity();

                        isDrag = false;

                        if (mVelocityTracker != null) {
                            mVelocityTracker.recycle();
                            mVelocityTracker = null;
                        }

                        if (xVelocity > FLING_MIN_VELOCITY) {
                            onGestureBack();
                            event.setAction(MotionEvent.ACTION_CANCEL);
                        }
                    }
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                    isDrag = false;
                    break;
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    /* 子类是否响应手势返回
    * @return 默认返回true，支持手势返回。
    */
    protected boolean isOnGestureBack(MotionEvent event) {
        return false;
    }

    /**
     * 子类可以重写此方法来处理手势返回时需要进行的操作；
     * 默认与返回键一致
     */
    protected void onGestureBack() {
        this.finish();
    }

}
