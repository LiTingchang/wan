package com.kuaikan.comic.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kuaikan.comic.R;
import com.kuaikan.comic.ui.adapter.SmartFragmentStatePagerAdapter;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by skyfishjy on 12/18/14.
 */
public class MainTabKuaikanFragment extends Fragment {

    public static final String TAG = MainTabKuaikanFragment.class.getSimpleName();

    @InjectView(R.id.container)
    FrameLayout mContainerLayout;
    @InjectView(R.id.pager_wrapper)
    FrameLayout mPageWrapper;
    @InjectView(R.id.feed_viewpager)
    ViewPager mFeedViewPager;

    MainFeedPagerAdapter mMainFeedPagerAdapter;
    MainFeedPagerOnPageChangeListener mMainFeedPagerOnPageChangeListener;
    AttentionFeedFragment attentionFeedFragment;
    RecommendFeedFragment recommendFeedFragment;
    List<Fragment> feedFragmentList;

    private boolean mAttentionShowBefore = false;
    private boolean mHasUnreadMsg = false;

    public static MainTabKuaikanFragment newInstance() {
        MainTabKuaikanFragment fragment = new MainTabKuaikanFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedFragmentList = new ArrayList<>();
        attentionFeedFragment = AttentionFeedFragment.newInstance();
        feedFragmentList.add(attentionFeedFragment);
        recommendFeedFragment = RecommendFeedFragment.newInstance();
        feedFragmentList.add(recommendFeedFragment);
    }


    public void scrollToFirst(){
//        mRecyclerView.smoothScrollToPosition(0);
    }

    public boolean ismHasUnreadMsg() {
        return mHasUnreadMsg;
    }

    public void setmHasUnreadMsg(boolean mHasUnreadMsg) {
        this.mHasUnreadMsg = mHasUnreadMsg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_kuaikan, container, false);
        ButterKnife.inject(this, view);

        mMainFeedPagerAdapter = new MainFeedPagerAdapter(getChildFragmentManager(), feedFragmentList);
        mFeedViewPager.setAdapter(mMainFeedPagerAdapter);

        mMainFeedPagerOnPageChangeListener = new MainFeedPagerOnPageChangeListener();
        mFeedViewPager.setOnPageChangeListener(mMainFeedPagerOnPageChangeListener);
        mFeedViewPager.setCurrentItem(1);

        return view;
    }

    public class MainFeedPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
//            favTypeHost.check(position == 0 ? R.id.fav_topic : R.id.fav_comic);
            //TODO 主界面接口回调
            if(sKuaiKanFeedPageChangeListener != null){
                sKuaiKanFeedPageChangeListener.OnPageChange(position);
                //如果是关注列表页
                if(position == 0){
                    //有未读数时自动刷新列表
                    if(ismHasUnreadMsg()){
                        if(attentionFeedFragment != null){
                            attentionFeedFragment.loadData();
                        }
                    }else{
                        //之前没有展示过，调用第一次关注页展示逻辑
                        if(!mAttentionShowBefore){
                            if(attentionFeedFragment != null){
                                attentionFeedFragment.initDataTabFirstCheck();
                            }
                        }
                    }

                    mAttentionShowBefore = true;
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public void changeViewPager(int position){
        if(mFeedViewPager != null){
            mFeedViewPager.setCurrentItem(position);
        }
    }

    public interface KuaiKanFeedPageChangeListener{
        public void OnPageChange(int position);
    }

    public static KuaiKanFeedPageChangeListener sKuaiKanFeedPageChangeListener;

    public static void setKuaiKanFeedPageChangeListener(KuaiKanFeedPageChangeListener kuaiKanFeedPageChangeListener){
        sKuaiKanFeedPageChangeListener = kuaiKanFeedPageChangeListener;
    }

    public static class MainFeedPagerAdapter extends SmartFragmentStatePagerAdapter {
        List<Fragment> fragmentList;

        public MainFeedPagerAdapter(android.support.v4.app.FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragmentList = fragments;
        }

        @Override
        public Fragment getItem(int position) {

            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.tag(TAG);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("每日推荐");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("每日推荐");
    }

}
