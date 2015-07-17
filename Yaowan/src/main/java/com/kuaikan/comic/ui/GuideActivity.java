package com.kuaikan.comic.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.kuaikan.comic.R;
import com.kuaikan.comic.ui.adapter.SmartFragmentStatePagerAdapter;
import com.kuaikan.comic.ui.fragment.GuideImageFragment;
import com.kuaikan.comic.ui.fragment.LastGuideImageFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by skyfishjy on 1/11/15.
 */
public class GuideActivity extends FragmentActivity {

    @InjectView(R.id.dot_first)
    ImageButton dotFirstImageButton;
    @InjectView(R.id.dot_second)
    ImageButton dotSecondImageButton;
    @InjectView(R.id.dot_third)
    ImageButton dotThirdImageButton;

    @InjectView(R.id.guide_viewpager)
    ViewPager viewPager;

    @InjectView(R.id.guide_dots)
    LinearLayout mDotsLayout;

    GuidePagerAdapter adapterViewPager;
    List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        ButterKnife.inject(this);
        dotFirstImageButton.setSelected(true);

        fragmentList = new ArrayList<>();

        fragmentList.add(GuideImageFragment.newInstance(R.drawable.guide_bg1, R.drawable.guide_img1, "一分钟", "看完一个超赞漫画"));
        fragmentList.add(GuideImageFragment.newInstance(R.drawable.guide_bg2, R.drawable.guide_img2, "搜索", "更多精彩内容任你挑"));
        fragmentList.add(LastGuideImageFragment.newInstance(R.drawable.guide_bg3, R.drawable.guide_img3, "收藏", "喜欢的可以先收藏哦"));

        adapterViewPager = new GuidePagerAdapter(getSupportFragmentManager(), fragmentList);

        viewPager.setAdapter(adapterViewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int newPosition) {
                switch (newPosition) {
                    case 0:
                        mDotsLayout.setVisibility(View.VISIBLE);
                        dotFirstImageButton.setSelected(true);
                        dotSecondImageButton.setSelected(false);
                        dotThirdImageButton.setSelected(false);
                        break;
                    case 1:
                        mDotsLayout.setVisibility(View.VISIBLE);
                        dotFirstImageButton.setSelected(false);
                        dotSecondImageButton.setSelected(true);
                        dotThirdImageButton.setSelected(false);
                        break;
                    case 2:
                        mDotsLayout.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static class GuidePagerAdapter extends SmartFragmentStatePagerAdapter {

        List<Fragment> fragmentList;

        public GuidePagerAdapter(android.support.v4.app.FragmentManager fragmentManager, List<Fragment> fragments) {
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
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

}
