package com.kuaikan.comic.ui.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.Share.Oauth2AccessToken;
import com.kuaikan.comic.rest.model.SignUserInfo;
import com.kuaikan.comic.ui.EditProfileActivity;
import com.kuaikan.comic.ui.LoginActivity;
import com.kuaikan.comic.ui.adapter.SmartFragmentStatePagerAdapter;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.kuaikan.comic.util.UserUtil;
import com.makeramen.RoundedTransformationBuilder;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by skyfishjy on 2/11/15.
 */
public class MainTabProfileFragment extends Fragment {
    public static final String TAG = MainTabProfileFragment.class.getSimpleName();

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    @InjectView(R.id.image)
    View mImageView;
    @InjectView(R.id.overlay)
    View mOverlayView;
    @InjectView(R.id.container)
    TouchInterceptionFrameLayout mInterceptionLayout;
    @InjectView(R.id.pager_wrapper)
    FrameLayout mPageWrapper;
//    TextView mTitleView;
    private int mSlop;
    private int mFlexibleSpaceHeight;
    private int mTabHeight;
    private boolean mScrolled;


    @InjectView(R.id.fav_viewpager)
    ViewPager favViewPager;
    @InjectView(R.id.fav_typehost)
    RadioGroup favTypeHost;
//    @InjectView(R.id.need_login_container)
//    RelativeLayout loginContainerRL;
//    @InjectView(R.id.login)
//    TextView loginButton;
    @InjectView(R.id.profile_login_avatar)
    ImageView mLoginAvatar;
    @InjectView(R.id.profile_login_name)
    TextView mProfileLoginName;

    FavPagerAdapter favPagerAdapter;
    FavTabViewPagerOnPageChangeListener favTabViewPagerOnPageChangeListener;
    FavComicListFragment favComicListFragment;
    FavTopicListFragment favTopicListFragment;
    List<Fragment> favFragmentList;
    Oauth2AccessToken accessToken;
    Transformation roundedTransformation;

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId) {
                case R.id.fav_topic:
                    favViewPager.setCurrentItem(0);
                    break;
                case R.id.fav_comic:
                    favViewPager.setCurrentItem(1);
                    break;
            }
        }
    };

    public static MainTabProfileFragment newInstance() {
        MainTabProfileFragment profileTabFragment = new MainTabProfileFragment();
        return profileTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favFragmentList = new ArrayList<>();
        favTopicListFragment = FavTopicListFragment.newInstance();
        favFragmentList.add(favTopicListFragment);
        favComicListFragment = FavComicListFragment.newInstance();
        favFragmentList.add(favComicListFragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_profile, container, false);
        ButterKnife.inject(this, view);

        favPagerAdapter = new FavPagerAdapter(getChildFragmentManager(), favFragmentList);
        favViewPager.setAdapter(favPagerAdapter);

        favTabViewPagerOnPageChangeListener = new FavTabViewPagerOnPageChangeListener();
        favViewPager.setOnPageChangeListener(favTabViewPagerOnPageChangeListener);

        favTypeHost.setOnCheckedChangeListener(onCheckedChangeListener);

        favTypeHost.check(R.id.fav_topic);

//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), LoginActivity.class);
//                startActivity(intent);
//            }
//        });

        roundedTransformation = new RoundedTransformationBuilder()
                .borderWidthDp(0)
                .cornerRadius(getActivity().getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width) / 2)
                .oval(false)
                .build();

        final SignUserInfo signUserInfo = PreferencesStorageUtil.readSignUserInfo(getActivity());
        mLoginAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserUtil.isUserLogin(getActivity())){
                    Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        if(signUserInfo != null && !TextUtils.isEmpty(signUserInfo.getAvatar_url())){
            Picasso.with(getActivity()).load(signUserInfo.getAvatar_url())
                    .resize(getActivity().getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width), getActivity().getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width))
                    .transform(roundedTransformation)
                    .into(mLoginAvatar);
            mProfileLoginName.setText(signUserInfo.getNickname());
        }

        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.profile_tab_image_height);
        mTabHeight = getResources().getDimensionPixelSize(R.dimen.profile_tab_tab_height);
        mPageWrapper.setPadding(0, mFlexibleSpaceHeight, 0, 0);

        ((FrameLayout.LayoutParams) favTypeHost.getLayoutParams()).topMargin = mFlexibleSpaceHeight - mTabHeight + 1;

        ViewConfiguration vc = ViewConfiguration.get(getActivity());
        mSlop = vc.getScaledTouchSlop();
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);
        ScrollUtils.addOnGlobalLayoutListener(mInterceptionLayout, new Runnable() {
            @Override
            public void run() {
                // Extra space is required to move mInterceptionLayout when it's scrolled.
                // It's better to adjust its height when it's laid out
                // than to adjust the height when scroll events (onMoveMotionEvent) occur
                // because it causes lagging.
                // See #87: https://github.com/ksoichiro/Android-ObservableScrollView/issues/87
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInterceptionLayout.getLayoutParams();
//                lp.height = KKMHApp.getScreenHeight() + mFlexibleSpaceHeight - tabBottonPadding;
                lp.height = KKMHApp.getContentHeight() + mFlexibleSpaceHeight - KKMHApp.getToolBarHeight();
                mInterceptionLayout.requestLayout();

                updateFlexibleSpace();
            }
        });

        return view;
    }


    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;

        try {

            c = Class.forName("com.android.internal.R$dimen");

            obj = c.newInstance();

            field = c.getField("status_bar_height");

            x = Integer.parseInt(field.get(obj).toString());

            sbar = getActivity().getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {

            e1.printStackTrace();

        }

        return sbar;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.tag(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFavLayout();
        MobclickAgent.onPageStart("我的收藏");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("我的收藏");
    }

    private void refreshFavLayout(){
        SignUserInfo signUser = PreferencesStorageUtil.readSignUserInfo(getActivity());
        accessToken = PreferencesStorageUtil.readWeiboAccessToken(getActivity());
//        if (accessToken.isSessionValid()) {
        if ((signUser != null && !TextUtils.isEmpty(signUser.getId())) || accessToken.isSessionValid()) {
//            loginContainerRL.setVisibility(View.GONE);
//            favViewPager.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(signUser.getAvatar_url())){
                Picasso.with(getActivity()).load(signUser.getAvatar_url())
                        .resize(getActivity().getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width), getActivity().getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width))
                        .transform(roundedTransformation)
                        .into(mLoginAvatar);
            }
            mProfileLoginName.setText(signUser.getNickname());
        } else {
            mLoginAvatar.setImageDrawable(new ColorDrawable(android.R.color.transparent));
            mProfileLoginName.setText("未登录");
//            loginContainerRL.setVisibility(View.VISIBLE);
//            favViewPager.setVisibility(View.GONE);
        }
    }

    public static class FavPagerAdapter extends SmartFragmentStatePagerAdapter {
        List<Fragment> fragmentList;

        public FavPagerAdapter(android.support.v4.app.FragmentManager fragmentManager, List<Fragment> fragments) {
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

    public class FavTabViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            favTypeHost.check(position == 0 ? R.id.fav_topic : R.id.fav_comic);
//            if(position == 0){
//                favTopicListFragment.initData();
//            }else if(position == 1){
//                favComicListFragment.initData();
//            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


    private TouchInterceptionFrameLayout.TouchInterceptionListener mInterceptionListener = new TouchInterceptionFrameLayout.TouchInterceptionListener() {
        @Override
        public boolean shouldInterceptTouchEvent(MotionEvent ev, boolean moving, float diffX, float diffY) {
            if (!mScrolled && mSlop < Math.abs(diffX) && Math.abs(diffY) < Math.abs(diffX)) {
                // Horizontal scroll is maybe handled by ViewPager
                return false;
            }

            Scrollable scrollable = getCurrentScrollable();
            if (scrollable == null) {
                mScrolled = false;
                return false;
            }

            // If interceptionLayout can move, it should intercept.
            // And once it begins to move, horizontal scroll shouldn't work any longer.
            int flexibleSpace = mFlexibleSpaceHeight - mTabHeight;
            int translationY = (int) ViewHelper.getTranslationY(mInterceptionLayout);
            boolean scrollingUp = 0 < diffY;
            boolean scrollingDown = diffY < 0;
            if (scrollingUp) {
                if (translationY < 0) {
                    mScrolled = true;
                    return true;
                }
            } else if (scrollingDown) {
                if (-flexibleSpace < translationY) {
                    mScrolled = true;
                    return true;
                }
            }
            mScrolled = false;
            return false;
        }

        @Override
        public void onDownMotionEvent(MotionEvent ev) {
        }

        @Override
        public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
            int flexibleSpace = mFlexibleSpaceHeight - mTabHeight;
            float translationY = ScrollUtils.getFloat(ViewHelper.getTranslationY(mInterceptionLayout) + diffY, -flexibleSpace, 0);
            updateFlexibleSpace(translationY);
        }

        @Override
        public void onUpOrCancelMotionEvent(MotionEvent ev) {
            mScrolled = false;
        }
    };

    private Scrollable getCurrentScrollable() {
        Fragment fragment = favFragmentList.get(favViewPager.getCurrentItem());
        if (fragment == null) {
            return null;
        }
        View view = fragment.getView();
        if (view == null) {
            return null;
        }
        return (Scrollable) view.findViewById(R.id.scroll);
    }

    private void updateFlexibleSpace() {
        updateFlexibleSpace(ViewHelper.getTranslationY(mInterceptionLayout));
    }

    private void updateFlexibleSpace(float translationY) {
        ViewHelper.setTranslationY(mInterceptionLayout, translationY);
        int minOverlayTransitionY = -mOverlayView.getHeight();
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-translationY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        float flexibleRange = mFlexibleSpaceHeight;
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat(-translationY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange + translationY - mTabHeight) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        setPivotXToTitle();
        ViewHelper.setPivotY(mProfileLoginName, 0);
        ViewHelper.setScaleX(mProfileLoginName, scale);
        ViewHelper.setScaleY(mProfileLoginName, scale);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle() {
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(mProfileLoginName, KKMHApp.getScreenWidth());
        } else {
            ViewHelper.setPivotX(mProfileLoginName, 0);
        }
    }

}
