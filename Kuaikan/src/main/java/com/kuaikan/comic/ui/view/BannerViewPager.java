package com.kuaikan.comic.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by a on 2015/4/1.
 */
public class BannerViewPager extends ViewPager {

    private int mTureItemCount;

    public BannerViewPager(Context context) {
        super(context);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean bool = super.onInterceptTouchEvent(ev);
        getParent().requestDisallowInterceptTouchEvent(false);
        if (bool)
            getParent().requestDisallowInterceptTouchEvent(true);
        return bool;
    }

    public int getTureItemCount() {
        return mTureItemCount;
    }

    public void setTureItemCount(int mTureItemCount) {
        this.mTureItemCount = mTureItemCount;
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
            boolean bool = super.onTouchEvent(paramMotionEvent);
            getParent().requestDisallowInterceptTouchEvent(false);
            if (bool)
                getParent().requestDisallowInterceptTouchEvent(true);
            return bool;
    }

}
