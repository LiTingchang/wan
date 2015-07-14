package com.kuaikan.comic.ui.fragment;

import android.annotation.TargetApi;
import android.support.v4.app.FragmentManager;
import android.content.res.Configuration;
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
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.Share.Oauth2AccessToken;
import com.kuaikan.comic.rest.model.TopicDetail;
import com.kuaikan.comic.ui.adapter.SmartFragmentStatePagerAdapter;
import com.kuaikan.comic.util.FixedAspectRatioFrameLayout;
import com.kuaikan.comic.util.UIUtil;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;
/**
 * Created by skyfishjy on 12/28/14.
 */
public class TopicDetailFragment extends Fragment {

    public static final String TAG = TopicDetailFragment.class.getSimpleName();
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    @InjectView(R.id.image)
    ImageView mImageView;
    @InjectView(R.id.overlay)
    View mOverlayView;
    @InjectView(R.id.container)
    TouchInterceptionFrameLayout mInterceptionLayout;
    @InjectView(R.id.pager_wrapper)
    LinearLayout mPageWrapper;
    @InjectView(R.id.topic_detail_header_topic_name)
    TextView mTopicName;
    @InjectView(R.id.topic_detail_like_comment_layout)
    LinearLayout mTopicLikeCommentLayout;
    @InjectView(R.id.topic_detail_header_like_count)
    TextView mLikeCount;
    @InjectView(R.id.topic_detail_header_comment_count)
    TextView mCommentCount;
    @InjectView(R.id.cover_layout)
    FixedAspectRatioFrameLayout mFixedAspectRatioFrameLayout;
    private int mSlop;
    private int mFlexibleSpaceHeight;
    private int mTabHeight;
    private int mTitleBarHeight;
    private int mTitleLeftMarigin;
    private boolean mScrolled;
    private TopicDetail mTopicDetail = new TopicDetail();


    @InjectView(R.id.topic_detail_viewpager)
    ViewPager topicDetailViewPager;
    @InjectView(R.id.detail_typehost)
    RadioGroup comicTypeHost;

    TopicPagerAdapter topicPagerAdapter;
    TopicDetailPagerOnPageChangeListener topicDetailPagerOnPageChangeListener;
    Fragment topicComicListFragment;
    Fragment topicInfoFragment;
    List<Fragment> topicDetailFragmentList;
    Oauth2AccessToken accessToken;
    Transformation roundedTransformation;

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId) {
                case R.id.tab_info:
                    topicDetailViewPager.setCurrentItem(0);
                    break;
                case R.id.tab_comic_list:
                    topicDetailViewPager.setCurrentItem(1);
                    break;
            }
        }
    };

//    private static final String KEY_TOPIC_ID = "topic_id";

//    long topicID;
//    @InjectView(R.id.recyclerView)
//    RecyclerView mRecyclerView;

    public static TopicDetailFragment newInstance(TopicDetail topicDetail) {
        TopicDetailFragment topicDetailFragment = new TopicDetailFragment(topicDetail);
        return topicDetailFragment;
    }

    public TopicDetailFragment(TopicDetail topicDetail){
        this.mTopicDetail = topicDetail;
    }

    public TopicDetailFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topicDetailFragmentList = new ArrayList<>();
        topicInfoFragment = TopicDetailAboutTabFragment.newInstance(mTopicDetail);
        topicDetailFragmentList.add(topicInfoFragment);
        topicComicListFragment = TopicDetailComicListFragment.newInstance(mTopicDetail);
        topicDetailFragmentList.add(topicComicListFragment);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic_detail, container, false);
        ButterKnife.inject(this, view);

        topicPagerAdapter = new TopicPagerAdapter(getChildFragmentManager(), topicDetailFragmentList);
        topicDetailViewPager.setAdapter(topicPagerAdapter);

        topicDetailPagerOnPageChangeListener = new TopicDetailPagerOnPageChangeListener();
        topicDetailViewPager.setOnPageChangeListener(topicDetailPagerOnPageChangeListener);

        comicTypeHost.setOnCheckedChangeListener(onCheckedChangeListener);

        comicTypeHost.check(R.id.tab_comic_list);
        if(mTopicDetail != null){
            mTopicName.setText(mTopicDetail.getTitle());
        }

//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), LoginActivity.class);
//                startActivity(intent);
//            }
//        });


//        roundedTransformation = new RoundedTransformationBuilder()
//                .borderWidthDp(0)
//                .cornerRadius(getActivity().getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width) / 2)
//                .oval(false)
//                .build();


        if(mTopicDetail != null && !TextUtils.isEmpty(mTopicDetail.getCover_image_url())){
            Picasso.with(getActivity())
                    .load(mTopicDetail.getCover_image_url())
                    .fit()
                    .centerCrop()
                    .into(mImageView);
            //.resize(KKMHApp.getScreenWidth(), getActivity().getResources().getDimensionPixelSize(R.dimen.profile_tab_image_height))
        }

        mTabHeight = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);//R.dimen.profile_tab_tab_height);

        mTitleBarHeight = getResources().getDimensionPixelSize(R.dimen.profile_tab_tab_height);//R.dimen.profile_tab_tab_height);
        mTitleLeftMarigin = getResources().getDimensionPixelSize(R.dimen.topic_detail_title_marigin_left);

        ViewConfiguration vc = ViewConfiguration.get(getActivity());
        mSlop = vc.getScaledTouchSlop();
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);


        final ViewTreeObserver obs = mFixedAspectRatioFrameLayout.getViewTreeObserver();
        obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw () {
                mFlexibleSpaceHeight = mFixedAspectRatioFrameLayout.getHeight();
                mPageWrapper.setPadding(0, mFlexibleSpaceHeight, 0, 0);
                ScrollUtils.addOnGlobalLayoutListener(mInterceptionLayout, new Runnable() {
                    @Override
                    public void run() {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInterceptionLayout.getLayoutParams();
                        lp.height = KKMHApp.getContentHeight() + mFlexibleSpaceHeight + KKMHApp.getToolBarHeight();
                        mInterceptionLayout.requestLayout();
                        updateFlexibleSpace();
                    }
                });
                return true;
            }
        });

        if(mTopicDetail != null){
            mLikeCount.setText(UIUtil.getCalculatedCount(mTopicDetail.getLikes_count()));
            mCommentCount.setText(UIUtil.getCalculatedCount(mTopicDetail.getComments_count()));
        }

        return view;
    }

//    onWindo

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        loadData();
        Timber.tag(TopicDetailFragment.class.getSimpleName());
    }


    @Override
    public void onResume() {
        super.onResume();
//        refreshFavLayout();
        MobclickAgent.onPageStart("专题列表");
//        accessToken = PreferencesStorageUtil.readWeiboAccessToken(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("专题列表");
    }

    public static class TopicPagerAdapter extends SmartFragmentStatePagerAdapter {
        List<Fragment> fragmentList;

        public TopicPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
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

    public class TopicDetailPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            comicTypeHost.check(position == 0 ? R.id.tab_info : R.id.tab_comic_list);
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
        Fragment fragment = topicDetailFragmentList.get(topicDetailViewPager.getCurrentItem());
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
        int minOverlayTransitionY = - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-translationY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        float flexibleRange = mFlexibleSpaceHeight;
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat(-translationY / flexibleRange, 0, 1));
        ViewHelper.setAlpha(mTopicLikeCommentLayout, ScrollUtils.getFloat(1 - ((-translationY / flexibleRange) * 2), 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange + translationY - mTabHeight) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        setPivotXToTitle();
        ViewHelper.setPivotY(mTopicName, (mTopicName.getMeasuredHeight() / 2));
        ViewHelper.setScaleX(mTopicName, scale);
        ViewHelper.setScaleY(mTopicName, scale);
        ViewHelper.setTranslationX(mTopicName, ScrollUtils.getFloat(-translationY , 10, (KKMHApp.getScreenWidth() - mTopicName.getMeasuredWidth() - mTitleLeftMarigin) / 2));
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle() {
        try {
            Configuration config = getResources().getConfiguration();
            if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                    && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                ViewHelper.setPivotX(mTopicName, KKMHApp.getScreenWidth());
            } else {
                ViewHelper.setPivotX(mTopicName, (mTopicName.getMeasuredWidth() / 2));
            }
        } catch (IllegalStateException e) {

        }

    }

}
