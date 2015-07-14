package com.kuaikan.comic.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.Share.Oauth2AccessToken;
import com.kuaikan.comic.db.JsonSD;
import com.kuaikan.comic.rest.RestClient;
import com.kuaikan.comic.rest.model.API.EmptyDataResponse;
import com.kuaikan.comic.rest.model.API.FavComicResponse;
import com.kuaikan.comic.rest.model.API.TopicListResponse;
import com.kuaikan.comic.rest.model.Comic;
import com.kuaikan.comic.rest.model.SignUserInfo;
import com.kuaikan.comic.ui.ComicDetailActivity;
import com.kuaikan.comic.ui.EditProfileActivity;
import com.kuaikan.comic.ui.LoginActivity;
import com.kuaikan.comic.ui.TopicDetailActivity;
import com.kuaikan.comic.ui.adapter.ProfileListAdapter;
import com.kuaikan.comic.util.GsonUtil;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.UserUtil;
import com.makeramen.RoundedTransformationBuilder;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by skyfishjy on 2/11/15.
 */
public class MainTabProfile2Fragment extends Fragment implements ObservableScrollViewCallbacks {
    public static final String TAG = MainTabProfile2Fragment.class.getSimpleName();

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    @InjectView(R.id.recyclerView)
    ObservableRecyclerView mRecyclerView;
    @InjectView(R.id.image)
    View mImageView;
    @InjectView(R.id.overlay)
    View mOverlayView;
    @InjectView(R.id.profile_user_layout)
    LinearLayout mUserLayout;
//    TextView mTitleView;
    private int mSlop;
    private int mFlexibleSpaceHeight;
    private int mTabHeight;
    private boolean mScrolled;
    private int mActionBarSize;
    private boolean mUserLayoutIsShown = true;
    private int mCurrentMode = ProfileListAdapter.MODE_COMIC;
//    private List<Comic> mComicList;
//    private List<Topic> mTopicList;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.profile_login_avatar)
    ImageView mLoginAvatar;
    @InjectView(R.id.profile_login_name)
    TextView mProfileLoginName;

    Oauth2AccessToken accessToken;
    Transformation roundedTransformation;
    LinearLayoutManager mLayoutManager;
    ProfileListAdapter mProfileAdapter;
    ShowDeleteFavDialogFragment showDelComicDialogFragment;
    ShowDeleteFavDialogFragment showDelTopicDialogFragment;
    private boolean mIsLoginBefore = true;


    public static MainTabProfile2Fragment newInstance() {
        MainTabProfile2Fragment profileTabFragment = new MainTabProfile2Fragment();
        return profileTabFragment;
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            initData();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_profile2, container, false);
        ButterKnife.inject(this, view);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setScrollViewCallbacks(this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        roundedTransformation = new RoundedTransformationBuilder()
                .borderWidthDp(0)
                .cornerRadius(getActivity().getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width) / 2)
                .oval(false)
                .build();

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_primary));
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);

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
//        mPageWrapper.setPadding(0, mFlexibleSpaceHeight, 0, 0);
        mActionBarSize = getResources().getDimensionPixelSize(R.dimen.profile_tab_tab_height);

        ViewHelper.setTranslationY(mOverlayView, mFlexibleSpaceHeight);

        return view;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceHeight;// - mActionBarSize;
        int minOverlayTransitionY = - mOverlayView.getHeight();//mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));
        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));
        if(scrollY > 20){
            hidemUserLayout(true);
            mSwipeRefreshLayout.setEnabled(false);
        }else{
            showmUserLayout(true);
            mSwipeRefreshLayout.setEnabled(true);
        }

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProfileAdapter = new ProfileListAdapter(getActivity(), new ProfileListAdapter.ProfileItemLongClickListener() {
            @Override
            public boolean onLongClick(int mode,final int position) {
                if(mode == ProfileListAdapter.MODE_COMIC){
                    showDelComicDialogFragment = ShowDeleteFavDialogFragment.newInstance();
                    showDelComicDialogFragment.setAlertDialogClickListener(new ShowDeleteFavDialogFragment.AlertDialogClickListener() {
                        @Override
                        public void onPositiveButtonClick() {
                            delFavComic(position);
                        }

                        @Override
                        public void onNegativeButtonClick() {
                        }
                    });
                    showDelComicDialogFragment.show(getChildFragmentManager().beginTransaction(), "del_comic");
                }else{
                    showDelTopicDialogFragment = ShowDeleteFavDialogFragment.newInstance();
                    showDelTopicDialogFragment.setAlertDialogClickListener(new ShowDeleteFavDialogFragment.AlertDialogClickListener() {
                        @Override
                        public void onPositiveButtonClick() {
                            delFavTopic(position);
                        }

                        @Override
                        public void onNegativeButtonClick() {
                        }
                    });
                    showDelTopicDialogFragment.show(getChildFragmentManager().beginTransaction(), "del_topic");
                }
                return false;
            }
        }, new ProfileListAdapter.ProfileItemComicClickListener() {
            @Override
            public void onClick(int mode, final int position) {

                if(mode == ProfileListAdapter.MODE_COMIC){
                    Comic comic = mProfileAdapter.getComicObject(position);
                    Intent intent = new Intent(getActivity(), ComicDetailActivity.class);
                    intent.putExtra("comic_id", comic.getId());
                    intent.putExtra("comic_title", comic.getTitle());
                    getActivity().startActivity(intent);
                }else{

                    Intent intent = new Intent(getActivity(), TopicDetailActivity.class);
                    intent.putExtra("topic_id", mProfileAdapter.getTopicObject(position).getId());
                    startActivity(intent);

                }

            }
        }, new ProfileListAdapter.ProfileTabChangeListener() {
            @Override
            public void onTabChange(int tab, boolean needLoad) {
                mCurrentMode = tab;
                if (needLoad && UserUtil.isUserLogin(getActivity())) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    loadData(RestClient.DEFAULT_OFFSET, RestClient.DEFAULT_LIMIT, tab);
                }
            }
        }, new ProfileListAdapter.ProfileRefreshListener() {
            @Override
            public void onLoadMoreItem(int newPosition) {
                loadData(newPosition, RestClient.DEFAULT_LIMIT, mCurrentMode);
            }
        });
        mRecyclerView.setAdapter(mProfileAdapter);
        initData();
        Timber.tag(TAG);
    }

    private void initData() {
        if(UserUtil.isUserLogin(getActivity())){
            loadData(RestClient.DEFAULT_OFFSET, RestClient.DEFAULT_LIMIT, mCurrentMode);
        }else{
            mProfileAdapter.clear();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    private void loadData(final int offset, int limit, int tab) {
        if(tab == ProfileListAdapter.MODE_COMIC){
            KKMHApp.getRestClient().getFavComics(offset, limit, new Callback<FavComicResponse>() {
                @Override
                public void success(FavComicResponse favTopicResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                        return;
                    }
                    if(favTopicResponse != null){
                        if (offset == RestClient.DEFAULT_OFFSET) {
                            mProfileAdapter.initComicData(favTopicResponse.getFavComicList());
                            JsonSD.writeJsonToFile(JsonSD.CATEGORY.FAV_COMIC, favTopicResponse.toJSON());
                        } else {
                            mProfileAdapter.addAllComic(favTopicResponse.getFavComicList());
                        }
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void failure(RetrofitError error) {
                    //读取缓存数据
                    RetrofitErrorUtil.handleError(getActivity(), error);
                    String comicJson = JsonSD.getJsonFromFile(JsonSD.CATEGORY.FAV_COMIC);
                    if(offset == RestClient.DEFAULT_OFFSET && !TextUtils.isEmpty(comicJson)){
                        mProfileAdapter.refreshComicList(GsonUtil.fromJson(comicJson, FavComicResponse.class).getFavComicList());
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }else if(tab == ProfileListAdapter.MODE_TOPIC){
            KKMHApp.getRestClient().getFavTopics(offset, limit, new Callback<TopicListResponse>() {
                @Override
                public void success(TopicListResponse topicListResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                        return;
                    }
                    if(topicListResponse != null){
                        if (offset == RestClient.DEFAULT_OFFSET) {
                            mProfileAdapter.initTopicData(topicListResponse.getTopics());
                            JsonSD.writeJsonToFile(JsonSD.CATEGORY.FAV_TOPIC, topicListResponse.toJSON());
                        } else {
                            mProfileAdapter.addAllTopic(topicListResponse.getTopics());
                        }
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void failure(RetrofitError error) {
                    //读取缓存数据
                    RetrofitErrorUtil.handleError(getActivity(), error);
                    String topicJson = JsonSD.getJsonFromFile(JsonSD.CATEGORY.FAV_TOPIC);
                    if(offset == RestClient.DEFAULT_OFFSET && !TextUtils.isEmpty(topicJson)){
                        mProfileAdapter.refreshTopicList(GsonUtil.fromJson(topicJson, TopicListResponse.class).getTopics());
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }

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
//        accessToken = PreferencesStorageUtil.readWeiboAccessToken(getActivity());
//        if ((signUser != null && !TextUtils.isEmpty(signUser.getId())) || accessToken.isSessionValid()) {
        if(UserUtil.isUserLogin(getActivity())){
            if(!TextUtils.isEmpty(signUser.getAvatar_url())){
                Picasso.with(getActivity()).load(signUser.getAvatar_url())
                        .resize(getActivity().getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width), getActivity().getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width))
                        .transform(roundedTransformation)
                        .into(mLoginAvatar);
            }
            mProfileLoginName.setText(signUser.getNickname());
            if(!mIsLoginBefore){
                initData();
            }
        } else {
            mIsLoginBefore = false;
            mLoginAvatar.setImageDrawable(new ColorDrawable(android.R.color.transparent));
            mProfileLoginName.setText("未登录");
            if(mProfileAdapter != null){
                mProfileAdapter.clear();
            }
        }
    }

    private void showmUserLayout(boolean animated) {
        if (mUserLayout == null) {
            return;
        }
        if (!mUserLayoutIsShown) {
            if (animated) {
                ViewPropertyAnimator.animate(mUserLayout).cancel();
                ViewPropertyAnimator.animate(mUserLayout).scaleX(1).scaleY(1).setDuration(100).start();
            } else {
                ViewHelper.setScaleX(mUserLayout, 1);
                ViewHelper.setScaleY(mUserLayout, 1);
            }
            mUserLayoutIsShown = true;
        }
//        else {
//            // Ensure that FAB is shown
//            ViewHelper.setScaleX(mUserLayout, 1);
//            ViewHelper.setScaleY(mUserLayout, 1);
//        }
    }

    private void hidemUserLayout(boolean animated) {
        if (mUserLayout == null) {
            return;
        }
        if (mUserLayoutIsShown) {
            if (animated) {
                ViewPropertyAnimator.animate(mUserLayout).cancel();
                ViewPropertyAnimator.animate(mUserLayout).scaleX(0).scaleY(0).setDuration(100).start();
            } else {
                ViewHelper.setScaleX(mUserLayout, 0);
                ViewHelper.setScaleY(mUserLayout, 0);
            }
            mUserLayoutIsShown = false;
        }
//        else {
//            // Ensure that FAB is hidden
//            ViewHelper.setScaleX(mUserLayout, 0);
//            ViewHelper.setScaleY(mUserLayout, 0);
//        }
    }

    private void delFavComic(final int favTopicListPosition) {
        makeToast("删除中...");
        KKMHApp.getRestClient().delFavComic(mProfileAdapter.getComicObject(favTopicListPosition).getId(), new Callback<EmptyDataResponse>() {
            @Override
            public void success(EmptyDataResponse emptyDataResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                    return;
                }
                makeToast("删除成功");
                mProfileAdapter.deleteComic(favTopicListPosition);
            }

            @Override
            public void failure(RetrofitError error) {
                makeToast("删除失败");
                RetrofitErrorUtil.handleError(getActivity(), error);
            }
        });
    }

    private void delFavTopic(final int favTopicListPosition) {
        makeToast("删除中...");
        KKMHApp.getRestClient().delFavTopic(mProfileAdapter.getTopicObject(favTopicListPosition).getId(), new Callback<EmptyDataResponse>() {
            @Override
            public void success(EmptyDataResponse emptyDataResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                    return;
                }
                makeToast("删除成功");
                mProfileAdapter.deleteTopic(favTopicListPosition);
            }

            @Override
            public void failure(RetrofitError error) {
                makeToast("删除失败");
                RetrofitErrorUtil.handleError(getActivity(), error);
            }
        });
    }

    private void makeToast(String toast) {
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
    }

}
