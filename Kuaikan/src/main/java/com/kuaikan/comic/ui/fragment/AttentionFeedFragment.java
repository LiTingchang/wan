package com.kuaikan.comic.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.db.JsonSD;
import com.kuaikan.comic.rest.RestClient;
import com.kuaikan.comic.rest.model.API.ComicResponse;
import com.kuaikan.comic.rest.model.API.EmptyDataResponse;
import com.kuaikan.comic.rest.model.API.FavTimelineResponse;
import com.kuaikan.comic.rest.model.Comic;
import com.kuaikan.comic.service.PollingService;
import com.kuaikan.comic.ui.CommentListActivity;
import com.kuaikan.comic.ui.MainActivity;
import com.kuaikan.comic.ui.adapter.AttentionFeedAdapter;
import com.kuaikan.comic.ui.view.PageLikeAnimation;
import com.kuaikan.comic.util.GsonUtil;
import com.kuaikan.comic.util.ImageUtil;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.UIUtil;
import com.kuaikan.comic.util.UserUtil;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.tencent.qzone.QZone;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by liuchao1 on 15/6/8.
 */
public class AttentionFeedFragment extends Fragment {

    public static final String TAG = AttentionFeedFragment.class.getSimpleName();
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    LinearLayoutManager mLayoutManager;
    AttentionFeedAdapter dayRecomComicListAdapter;
    List<Comic> comicList = new ArrayList<>();
    private int mSince = 0;
    private boolean mIsLoginBefore = true;

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
//            if(UserUtil.isUserLogin(getActivity())){
//                initData();
//            }else{
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
            loadData();
        }
    };

    public AttentionFeedFragment() {
    }

    public static AttentionFeedFragment newInstance() {
        AttentionFeedFragment fragment = new AttentionFeedFragment();
        return fragment;
    }

    public void scrollToFirst() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_attention, container, false);
        ButterKnife.inject(this, view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_primary));
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);


        try {
            Field f = null;
            f = SwipeRefreshLayout.class.getDeclaredField("mTouchSlop");
            f.setAccessible(true);
            f.set(mSwipeRefreshLayout, 150);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        dayRecomComicListAdapter = new AttentionFeedAdapter(getActivity(), comicList, new AttentionFeedAdapter.ComicRefreshListener() {
            @Override
            public void onLoadMoreComic() {
                loadMoreData();
            }
        }, new AttentionFeedAdapter.ComicOperationListener() {

            @Override
            public void onShareOperation(Comic comic) {
                showShare(comic);
            }

            @Override
            public void onLikeOperation(Comic comic, ImageView likeIcon, TextView likeCount) {
                doLikeOption(comic, likeIcon, likeCount);
            }

            @Override
            public void onCommentOperation(long comic_id) {
                if (!UserUtil.checkUserLogin(getActivity())) {
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(getActivity(), CommentListActivity.class);
                intent.putExtra(CommentListActivity.COMMENT_COMIC_ID, comic_id);
                getActivity().startActivity(intent);
            }

            @Override
            public void onAutoAttentionOperation() {
                autoAttention();
            }
        });
        mRecyclerView.setAdapter(dayRecomComicListAdapter);
//        mRecyclerView.

        return view;
    }

    private void autoAttention() {
        KKMHApp.getRestClient().autoAddAttention(new Callback<EmptyDataResponse>() {
            @Override
            public void success(EmptyDataResponse emptyDataResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                    return;
                }
                loadData();
                UIUtil.showThost(getActivity(), "已为你随机关注了一批专题!");
            }


            @Override
            public void failure(RetrofitError error) {
                RetrofitErrorUtil.handleError(getActivity(), error);
            }
        });


    }


    private void doLikeOption(final Comic comic, final ImageView likeIcon, final TextView likeCountTw) {
        if (!comic.is_liked()) {
            KKMHApp.getRestClient().likeComic(comic.getId(), new Callback<EmptyDataResponse>() {
                @Override
                public void success(EmptyDataResponse emptyDataResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                        return;
                    }
                    int likeCount = comic.getLikes_count() + 1;
                    comic.setIs_liked(true);
                    comic.setLikes_count(likeCount);
                    likeIcon.setImageResource(R.drawable.ic_home_praise_pressed);
                    likeIcon.startAnimation(new PageLikeAnimation(false, 1.8f, 0.8f, 1.0f));
                    likeCountTw.setText(likeCount + "");
                }

                @Override
                public void failure(RetrofitError error) {
                    UIUtil.showThost(getActivity(), "点赞失败");
                    RetrofitErrorUtil.handleError(getActivity(), error);
                }
            });
        } else {
            KKMHApp.getRestClient().disLikeComic(comic.getId(), new Callback<EmptyDataResponse>() {
                @Override
                public void success(EmptyDataResponse emptyDataResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                        return;
                    }
                    int likeCount = comic.getLikes_count() - 1;
                    comic.setIs_liked(false);
                    comic.setLikes_count(likeCount);
                    likeIcon.setImageResource(R.drawable.ic_home_praise_normal);
                    likeIcon.startAnimation(new PageLikeAnimation(false, 1.8f, 0.8f, 1.0f));
                    likeCountTw.setText(likeCount + "");
//                    likeCountTV.setText(String.valueOf(comicDetailResponse.getLikes_count()));
                }

                @Override
                public void failure(RetrofitError error) {
                    UIUtil.showThost(getActivity(), "取消点赞失败");
                    RetrofitErrorUtil.handleError(getActivity(), error);
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        initData();
        Timber.tag(TAG);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dayRecomComicListAdapter != null) {
            dayRecomComicListAdapter.notifyDataSetChanged();
        }
        if(UserUtil.isUserLogin(getActivity())){
            //如果第一次登陆进入关注列表
            if(!mIsLoginBefore ){
                loadData();
            }
        }else{
            mIsLoginBefore = false;
            if(dayRecomComicListAdapter != null){
                dayRecomComicListAdapter.notifyDataSetChanged();
            }
        }
        MobclickAgent.onPageStart("关注Tab");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("关注Tab");
    }

    //第一次切换到关注tab，调用该接口
    public void initDataTabFirstCheck() {
        if((getActivity() != null) && UserUtil.isUserLogin(getActivity()) && dayRecomComicListAdapter != null){
            String feedJson = JsonSD.getJsonFromFile(JsonSD.CATEGORY.MAIN_TAB_FEED_ATTENTION);
            if (!TextUtils.isEmpty(feedJson)) {
                dayRecomComicListAdapter.refresh(GsonUtil.fromJson(feedJson, ComicResponse.class).getComicList());
            }else{
                loadData();
            }
            dayRecomComicListAdapter.setCurrentComicOffset(RestClient.DEFAULT_OFFSET);
        }else{
            if(dayRecomComicListAdapter != null){
                dayRecomComicListAdapter.notifyDataSetChanged();
            }
        }
    }


    private void loadMoreData() {
        if (mSince > 0) {
            KKMHApp.getRestClient().getFavTimeline(mSince, new Callback<FavTimelineResponse>() {
                @Override
                public void success(FavTimelineResponse favTimelineResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                        return;
                    }
                    if (favTimelineResponse != null) {
                        dayRecomComicListAdapter.addAll(favTimelineResponse.getComicList(), false);
                        mSince = favTimelineResponse.getSince();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void failure(RetrofitError error) {
                    //读取缓存数据
                    RetrofitErrorUtil.handleError(getActivity(), error);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }


    }

    public void loadData() {
        KKMHApp.getRestClient().getFavTimeline(RestClient.DEFAULT_OFFSET, new Callback<FavTimelineResponse>() {
            @Override
            public void success(FavTimelineResponse favTimelineResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                    return;
                }
                if (favTimelineResponse != null && dayRecomComicListAdapter != null) {

                    dayRecomComicListAdapter.refresh(favTimelineResponse.getComicList());
                    JsonSD.writeJsonToFile(JsonSD.CATEGORY.MAIN_TAB_FEED_ATTENTION, favTimelineResponse.toJSON());

                    mSince = favTimelineResponse.getSince();
                }
                if(mSwipeRefreshLayout != null)
                mSwipeRefreshLayout.setRefreshing(false);
                PollingService.setsAttentionUnReadCount(0);
                //广播清楚未读数
                Intent unReadIntent = new Intent(MainActivity.UNREAD_ACTION);
                unReadIntent.putExtra(PollingService.UNREAD_COUNT_FLAG, 0);
                getActivity().sendBroadcast(unReadIntent);
            }

            @Override
            public void failure(RetrofitError error) {
                //读取缓存数据
                RetrofitErrorUtil.handleError(getActivity(), error);
                String feedJson = JsonSD.getJsonFromFile(JsonSD.CATEGORY.MAIN_TAB_FEED_ATTENTION);
                if (!TextUtils.isEmpty(feedJson) && dayRecomComicListAdapter != null) {
                    dayRecomComicListAdapter.refresh(GsonUtil.fromJson(feedJson, ComicResponse.class).getComicList());
                }
                if(mSwipeRefreshLayout != null)
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    // 在状态栏提示分享操作
    private void showNotification(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    private void showShare(final Comic comic) {
        OnekeyShare oks = new OnekeyShare();
        oks.setTitle("最近发现一部超好看的漫画：" + comic.getTopic().getTitle());
        oks.setText("快看！一分钟一个超赞故事！！");
//        oks.setUrl(comicDetailResponse.getUrl());
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            //自定义分享的回调想要函数
            @Override
            public void onShare(final Platform platform,
                                final cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                if ("Wechat".equals(platform.getName())) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            paramsToShare.setUrl(comic.getUrl());
                            paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                            paramsToShare.setImagePath(ImageUtil.getImageSDPath(getActivity(), comic.getCover_image_url()));
                        }
                    }).start();
                    statisticForward(comic, FORWARD_CHANNEL_WEIXIN);
                }
                if ("WechatMoments".equals(platform.getName())) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            paramsToShare.setUrl(comic.getUrl());
                            paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                            paramsToShare.setImagePath(ImageUtil.getImageSDPath(getActivity(), comic.getCover_image_url()));


//                            paramsToShare.setShareType(Platform.SHARE_IMAGE);
//                            Bitmap imageData = ImageUtil.getImageBitmap(ComicDetailActivity.this, comicDetailResponse.getCover_image_url());
//                            paramsToShare.setImageData(imageData);
//                            paramsToShare.setImagePath(ImageUtil.getImageSDPath(ComicDetailActivity.this, comicDetailResponse.getCover_image_url()));
                        }
                    }).start();
                    statisticForward(comic, FORWARD_CHANNEL_WEIXIN_MOMENTS);
                }
                if ("SinaWeibo".equals(platform.getName())) {
                    //限制微博分享的文字不能超过20
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            paramsToShare.setTitle(comic.getTitle());
                            paramsToShare.setImageUrl(comic.getCover_image_url());
                            paramsToShare.setText("最近发现一部超好看的漫画：" + comic.getTopic().getTitle()

                                    + "，和我一起来看吧～（来自@快看漫画 ） 完整内容戳：" + platform.getShortLintk(comic.getUrl(), false));
//                    if(paramsToShare.getText().length() > 20){
//                        Toast.makeText(context, "分享长度不能超过20个字", Toast.LENGTH_SHORT).show();
//                    }
                        }
                    }).start();
                    statisticForward(comic, FORWARD_CHANNEL_WEIBO);
                }
                if (QZone.NAME.equals(platform.getName()) || "QQ".equals(platform.getName())) {
                    //限制微博分享的文字不能超过20

//                    paramsToShare.setTitle(comic.getTitle());
//                    paramsToShare.setText(comic.getTitle() + "（来自@快看漫画 ）完整内容戳：" + comic.getUrl());
                    paramsToShare.setTitleUrl(comic.getUrl());
                    paramsToShare.setImageUrl(comic.getCover_image_url());
                    paramsToShare.setSite("快看漫画");
                    paramsToShare.setSiteUrl("http://kuaikanmanhua.com/");
//                    if(paramsToShare.getText().length() > 20){
//                        Toast.makeText(context, "分享长度不能超过20个字", Toast.LENGTH_SHORT).show();
//                    }
                    statisticForward(comic, FORWARD_CHANNEL_QZONE);
                }
            }
        });

        oks.show(getActivity());
    }


    public static final int FORWARD_CHANNEL_WEIBO = 1;
    public static final int FORWARD_CHANNEL_WEIXIN = 2;
    public static final int FORWARD_CHANNEL_WEIXIN_MOMENTS = 3;
    public static final int FORWARD_CHANNEL_QZONE = 5;

    private void statisticForward(Comic comic, int channel) {

        KKMHApp.getRestClient().statisticForward(comic.getId(), channel, new Callback<EmptyDataResponse>() {
            @Override
            public void success(EmptyDataResponse emptyDataResponse, Response response) {
//                UIUtil.showThost(getActivity(), "上传成功");
            }

            @Override
            public void failure(RetrofitError error) {
                RetrofitErrorUtil.handleError(getActivity(), error);
            }
        });
    }
}
