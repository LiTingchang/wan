package com.kuaikan.comic.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.kuaikan.comic.ui.view.BannerImageView;

import java.util.List;

/**
 * Created by a on 2015/3/30.
 */
public class MainPageAdapter extends PagerAdapter {

    private List<BannerImageView> mViews;

    public MainPageAdapter(List<BannerImageView> views){
        this.mViews = views;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(mViews.get(position));
    }
}
