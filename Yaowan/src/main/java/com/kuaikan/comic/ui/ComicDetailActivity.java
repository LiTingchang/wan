package com.kuaikan.comic.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.Share.Oauth2AccessToken;
import com.kuaikan.comic.db.ComicDetailBeanDaoHelper;
import com.kuaikan.comic.rest.RestClient;
import com.kuaikan.comic.rest.model.API.ComicDetailResponse;
import com.kuaikan.comic.rest.model.API.CommentResponse;
import com.kuaikan.comic.rest.model.API.EmptyDataResponse;
import com.kuaikan.comic.rest.model.Comment;
import com.kuaikan.comic.ui.adapter.ComicDetailAdapter;
import com.kuaikan.comic.ui.fragment.RecommendFeedFragment;
import com.kuaikan.comic.ui.view.PageLikeAnimation;
import com.kuaikan.comic.ui.view.SoftKeyboard;
import com.kuaikan.comic.util.ImageUtil;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.UIUtil;
import com.kuaikan.comic.util.UserUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.tencent.qzone.QZone;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


public class ComicDetailActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    String comicTitle;
    long comicID;
    LinearLayoutManager mLayoutManager;
    ComicDetailAdapter mComicDetailAdapter;
    ComicDetailResponse comicDetailResponse = new ComicDetailResponse();
    List<Comment> commentList = new ArrayList<>();

    public static final String INTENT_COMIC_DETAIL_TITLE = "comic_title";
    public static final String INTENT_COMIC_DETAIL_ID = "comic_id";

    @InjectView(R.id.recyclerView)
    ObservableRecyclerView recyclerView;
    @InjectView(R.id.recyclerView_container)
    LinearLayout recyclerViewContainerLL;
    @InjectView(R.id.comic_below)
    LinearLayout belowLayout;
    @InjectView(R.id.comic_below_action_layout)
    LinearLayout actionLL;
    @InjectView(R.id.comic_detail_action_like)
    LinearLayout likeStatusLL;
    @InjectView(R.id.comic_detail_action_share)
    LinearLayout shareButton;
    @InjectView(R.id.comic_detail_action_comment)
    FrameLayout commentButton;
    @InjectView(R.id.comic_comment_count)
    TextView commentCountTV;
    @InjectView(R.id.comic_like_status)
    CheckBox likeStatusCB;
    @InjectView(R.id.toolbar_divider)
    View mDividerLine;

    InputMethodManager imm;
    SoftKeyboard softKeyboard;
    ProgressDialog progressDialog;

//    ShareComicDialogFragment shareDialogFragment;
    Oauth2AccessToken accessToken;
//    DialogFragment authorDialogFragment;

    boolean commentMode = false;
    float belowLayoutHeight;
    boolean fullscreenMode = false;
    private TextView mTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);
        ButterKnife.inject(this);
        hideStatusBar();
        Timber.tag(ComicDetailActivity.class.getSimpleName());
        comicTitle = getIntent().getStringExtra(INTENT_COMIC_DETAIL_TITLE);
        comicID = getIntent().getLongExtra(INTENT_COMIC_DETAIL_ID, -1);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleTv = (TextView)getActionBarToolbar().findViewById(R.id.toolbar_center_title);
        mTitleTv.setText(comicTitle);
        mTitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerView != null){
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_up_indicator);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在评论");
        progressDialog.setCancelable(false);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mComicDetailAdapter = new ComicDetailAdapter(this, comicDetailResponse, commentList,new ComicDetailAdapter.CommentLikeListener(){
            @Override
            public void onCommentLike(long comment_id, boolean like) {
                likeComment(comment_id, like);
            }
        }, new ComicDetailAdapter.TopicInfoViewHolderClickListener() {
            @Override
            public void onShowAuthorInfo() {
                Intent intent = new Intent();
                intent.setClass(ComicDetailActivity.this, AuthorActivity.class);
                if(comicDetailResponse != null){
                    intent.putExtra(AuthorActivity.KEY_AUTHOR_ID, comicDetailResponse.getTopic().getUser().getId());
                }
                ComicDetailActivity.this.startActivity(intent);
            }

            @Override
            public void onFavOption() {
                takeFavChangeAction();
            }
        }, new ComicDetailAdapter.TopicDescriptionViewHolderClickListener() {
            @Override
            public void onAddToLike() {
                doLikeOption();
            }

            @Override
            public void onAddToFav() {
                takeFavChangeAction();
            }

            @Override
            public void onPreComic() {
                if(comicDetailResponse.getPrevious_comic_id() != 0l){
                    Intent intent = new Intent();
                    intent.setClass(ComicDetailActivity.this, ComicDetailActivity.class);
                    intent.putExtra("comic_id", comicDetailResponse.getPrevious_comic_id());
                    ComicDetailActivity.this.startActivity(intent);
                    ComicDetailActivity.this.finish();
                }else{
                    UIUtil.showThost(ComicDetailActivity.this, "已是第一篇~");
                }
            }

            @Override
            public void onNextComic() {
                if(comicDetailResponse.getNext_comic_id() != 0l){
                    Intent intent = new Intent();
                    intent.setClass(ComicDetailActivity.this, ComicDetailActivity.class);
                    intent.putExtra("comic_id", comicDetailResponse.getNext_comic_id());
                    ComicDetailActivity.this.startActivity(intent);
                    ComicDetailActivity.this.finish();
                }else{
                    UIUtil.showThost(ComicDetailActivity.this, "已是最后一篇~");
                }
            }
        });

        recyclerView.setScrollViewCallbacks(this);
        recyclerView.setAdapter(mComicDetailAdapter);

        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(belowLayout, imm);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                commentMode = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        actionLL.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                commentMode = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        actionLL.setVisibility(View.GONE);
                    }
                });

            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(ComicDetailActivity.this, CommentListActivity.class);
                intent.putExtra(CommentListActivity.COMMENT_COMIC_ID, comicDetailResponse.getId());
                ComicDetailActivity.this.startActivity(intent);
//                if (!fullscreenMode) {
//                    softKeyboard.openSoftKeyboard();
//                }
            }
        });

//        commentCancelIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                softKeyboard.closeSoftKeyboard();
//            }
//        });
//
//        commentOKIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String commentContent = commentET.getText().toString();
//                if (!TextUtils.isEmpty(commentContent)) {
//                    if (UserUtil.isUserLogin(ComicDetailActivity.this)){//accessToken.isSessionValid()) {
//                        softKeyboard.closeSoftKeyboard();
//                        progressDialog.show();
//                        KKMHApp.getRestClient().postComment(comicID, commentContent, new Callback<Comment>() {
//                            @Override
//                            public void success(Comment comment, Response response) {
//                                loadNewComment(RestClient.DEFAULT_OFFSET);
//                            }
//
//                            @Override
//                            public void failure(RetrofitError error) {
//                                progressDialog.dismiss();
//                                RetrofitErrorUtil.handleError(ComicDetailActivity.this, error);
//                            }
//                        });
//                    } else {
//                        gotoLogin();
//                    }
//
//                } else {
//                    Toast.makeText(ComicDetailActivity.this, "请输入评论内容", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fullscreenMode) {
//                    shareDialogFragment.show(getSupportFragmentManager().beginTransaction(), "share");
                    //TODO shareSDk
                    showShare();


                }
            }
        });



        likeStatusLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fullscreenMode) {
                    doLikeOption();
                }
            }
        });

        loadComicDetail();
        belowLayoutHeight = UIUtil.dp2px(this, 48);
        ShareSDK.initSDK(ComicDetailActivity.this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    // 在状态栏提示分享操作
    private void showNotification(String text) {
        Toast.makeText(ComicDetailActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void  showShare() {
        OnekeyShare oks = new OnekeyShare();
        oks.setTitle("最近发现一部超好看的漫画：" + comicDetailResponse.getTopic().getTitle());
        oks.setText("快看！一分钟一个超赞故事！！");
//        oks.setUrl(comicDetailResponse.getUrl());
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            //自定义分享的回调想要函数
            @Override
            public void onShare(final Platform platform,
                                final cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                if("Wechat".equals(platform.getName())){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            paramsToShare.setUrl(comicDetailResponse.getUrl());
                            paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                            paramsToShare.setImagePath(ImageUtil.getImageSDPath(ComicDetailActivity.this, comicDetailResponse.getCover_image_url()));
                        }
                    }).start();
                    statisticForward(comicDetailResponse, RecommendFeedFragment.FORWARD_CHANNEL_WEIXIN);
                }
                if("WechatMoments".equals(platform.getName())){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            paramsToShare.setUrl(comicDetailResponse.getUrl());
                            paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                            paramsToShare.setImagePath(ImageUtil.getImageSDPath(ComicDetailActivity.this, comicDetailResponse.getCover_image_url()));


//                            paramsToShare.setShareType(Platform.SHARE_IMAGE);
//                            Bitmap imageData = ImageUtil.getImageBitmap(ComicDetailActivity.this, comicDetailResponse.getCover_image_url());
//                            paramsToShare.setImageData(imageData);
//                            paramsToShare.setImagePath(ImageUtil.getImageSDPath(ComicDetailActivity.this, comicDetailResponse.getCover_image_url()));
                        }
                    }).start();
                    statisticForward(comicDetailResponse, RecommendFeedFragment.FORWARD_CHANNEL_WEIXIN_MOMENTS);
                }
                if("SinaWeibo".equals(platform.getName())){
                    //限制微博分享的文字不能超过20
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            paramsToShare.setTitle(comicDetailResponse.getTitle());
                            paramsToShare.setImageUrl(comicDetailResponse.getCover_image_url());
                            paramsToShare.setText("最近发现一部超好看的漫画：" + comicDetailResponse.getTopic().getTitle()

                                    + "，和我一起来看吧～（来自@快看漫画 ） 完整内容戳：" + platform.getShortLintk(comicDetailResponse.getUrl(), false));
//                    if(paramsToShare.getText().length() > 20){
//                        Toast.makeText(context, "分享长度不能超过20个字", Toast.LENGTH_SHORT).show();
//                    }
                        }
                    }).start();
                    statisticForward(comicDetailResponse, RecommendFeedFragment.FORWARD_CHANNEL_WEIBO);
                }
                if(QZone.NAME.equals(platform.getName()) || "QQ".equals(platform.getName())){
                    //限制微博分享的文字不能超过20
//                    paramsToShare.setText(comicDetailResponse.getTitle() + "（来自@快看漫画 ）完整内容戳：" + comicDetailResponse.getUrl());
                    paramsToShare.setTitleUrl(comicDetailResponse.getUrl());
                    paramsToShare.setImageUrl(comicDetailResponse.getCover_image_url());
                    paramsToShare.setSite("快看漫画");
                    paramsToShare.setSiteUrl("http://kuaikanmanhua.com/");
//                    if(paramsToShare.getText().length() > 20){
//                        Toast.makeText(context, "分享长度不能超过20个字", Toast.LENGTH_SHORT).show();
//                    }
                    statisticForward(comicDetailResponse, RecommendFeedFragment.FORWARD_CHANNEL_QZONE);
                }
            }
        });
        oks.show(this);
    }

    private void statisticForward(ComicDetailResponse comicDetailResponse, int channel){

        KKMHApp.getRestClient().statisticForward(comicDetailResponse.getId(), channel, new Callback<EmptyDataResponse>() {
            @Override
            public void success(EmptyDataResponse emptyDataResponse, Response response) {
//                UIUtil.showThost(getActivity(), "上传成功");
            }

            @Override
            public void failure(RetrofitError error) {
                RetrofitErrorUtil.handleError(ComicDetailActivity.this, error);
            }
        });

    }

    public interface OnLikeBtnClickListener{
        public void onLikeBtnClick(boolean like);
    }

    private static OnLikeBtnClickListener mOnLikeBtnClickListener;

    public static void setOnLikeBtnClickListener(OnLikeBtnClickListener onLikeBtnClickListener){
        mOnLikeBtnClickListener = onLikeBtnClickListener;
    }

    private void doLikeOption(){
        if(!UserUtil.checkUserLogin(ComicDetailActivity.this)){
            return;
        }
        if (!comicDetailResponse.is_liked()) {
            KKMHApp.getRestClient().likeComic(comicDetailResponse.getId(), new Callback<EmptyDataResponse>() {
                @Override
                public void success(EmptyDataResponse emptyDataResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(ComicDetailActivity.this, response)){
                        return;
                    }
                    comicDetailResponse.setIs_liked(true);
                    comicDetailResponse.setLikes_count(comicDetailResponse.getLikes_count() + 1);
                    likeStatusCB.setChecked(true);
                    likeStatusCB.startAnimation(new PageLikeAnimation(false, 1.8f, 0.8f, 1.0f));
                    if(mOnLikeBtnClickListener != null){
                        mOnLikeBtnClickListener.onLikeBtnClick(true);
                    }
//                    likeCountTV.setText(String.valueOf(comicDetailResponse.getLikes_count()));
                }

                @Override
                public void failure(RetrofitError error) {
                    makeToast("点赞失败");
                    RetrofitErrorUtil.handleError(ComicDetailActivity.this, error);
                }
            });
        } else {
            KKMHApp.getRestClient().disLikeComic(comicDetailResponse.getId(), new Callback<EmptyDataResponse>() {
                @Override
                public void success(EmptyDataResponse emptyDataResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(ComicDetailActivity.this, response)){
                        return;
                    }
                    comicDetailResponse.setIs_liked(false);
                    comicDetailResponse.setLikes_count(comicDetailResponse.getLikes_count() - 1);
                    likeStatusCB.setChecked(false);
                    likeStatusCB.startAnimation(new PageLikeAnimation(false, 1.8f, 0.8f, 1.0f));
                    if(mOnLikeBtnClickListener != null){
                        mOnLikeBtnClickListener.onLikeBtnClick(false);
                    }
//                    likeCountTV.setText(String.valueOf(comicDetailResponse.getLikes_count()));
                }

                @Override
                public void failure(RetrofitError error) {
                    makeToast("取消点赞失败");
                    RetrofitErrorUtil.handleError(ComicDetailActivity.this, error);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_all:
                Intent intent = new Intent(ComicDetailActivity.this, TopicDetailActivity.class);
                intent.putExtra("topic_id", comicDetailResponse.getTopic().getId());
                startActivity(intent);
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("");
        accessToken = PreferencesStorageUtil.readWeiboAccessToken(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("浏览漫画");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!PreferencesStorageUtil.isRatingDialogShow(ComicDetailActivity.this)){
            Intent startRatingIntent = new Intent();
            startRatingIntent.setClass(ComicDetailActivity.this, RatingDialogActivity.class);
            startActivity(startRatingIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_comic_detail_actions, menu);

//        Intent intent = new Intent(ComicDetailActivity.this, TopicDetailActivity.class);
//        intent.putExtra("topic_id", comicDetailResponse.getTopic().getId());
//        startActivity(intent);

//        if (comicDetailResponse.is_favourite()) {
//            menu.findItem(R.id.action_fav).setIcon(R.drawable.ic_action_fav);
//        } else {
//            menu.findItem(R.id.action_fav).setIcon(R.drawable.ic_action_fav_normal);
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        softKeyboard.unRegisterSoftKeyboardCallback();
    }

    private void loadComicDetail() {
        KKMHApp.getRestClient().getComicDetail(comicID, new Callback<ComicDetailResponse>() {
            @Override
            public void success(final ComicDetailResponse comicResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(ComicDetailActivity.this, response)){
                    return;
                }
                if(!ComicDetailBeanDaoHelper.getInstance().hasKey(comicID)){
                    ComicDetailBeanDaoHelper.getInstance().addData(comicResponse.toComicDetailBean());
                }

                initializeViewAfterGetComicDetail(comicResponse);
                KKMHApp.getRestClient().getHotComments(comicID, new Callback<CommentResponse>() {
                    @Override
                    public void success(CommentResponse commentResponse, Response response) {
                        if(RetrofitErrorUtil.handleResponse(ComicDetailActivity.this, response)){
                            return;
                        }
                        mComicDetailAdapter.initData(comicResponse, commentResponse.getComments());
                        mTitleTv.setText(comicResponse.getTitle());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        RetrofitErrorUtil.handleError(ComicDetailActivity.this, error);
                    }
                });

            }

            @Override
            public void failure(RetrofitError error) {
                //无网缓存
                if(ComicDetailBeanDaoHelper.getInstance().hasKey(comicID)){
                    ComicDetailResponse comicResponse = new ComicDetailResponse(ComicDetailBeanDaoHelper.getInstance().getDataById(comicID));
                    initializeViewAfterGetComicDetail(comicResponse);
                    mComicDetailAdapter.initData(comicResponse, new ArrayList<Comment>());
                }
            }
        });
    }

    private void likeComment(long comment_id, boolean like) {
        if(like){
            KKMHApp.getRestClient().likeComment(comment_id, new Callback<EmptyDataResponse>() {
                @Override
                public void success(EmptyDataResponse emptyDataResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(ComicDetailActivity.this, response)){
                        return;
                    }
                    Toast.makeText(ComicDetailActivity.this, "赞成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    RetrofitErrorUtil.handleError(ComicDetailActivity.this, error);
                }
            });
        }else{
            KKMHApp.getRestClient().disLikeComment(comment_id, new Callback<EmptyDataResponse>() {
                @Override
                public void success(EmptyDataResponse emptyDataResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(ComicDetailActivity.this, response)){
                        return;
                    }
                    Toast.makeText(ComicDetailActivity.this, "取消赞成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    RetrofitErrorUtil.handleError(ComicDetailActivity.this, error);
                }
            });
        }

    }

    private void initializeViewAfterGetComicDetail(ComicDetailResponse comicResponse){
//        shareDialogFragment = ShareComicDialogFragment.newInstance(comicResponse);
//        authorDialogFragment = ShowComicAuthorDialogFragment.newInstance(comicResponse.getTopic().getUser().getId());
        if (comicResponse.getComments_count() < 100000) {
            commentCountTV.setText(String.valueOf(comicResponse.getComments_count()));
        } else {
            commentCountTV.setText("99999+");//String.valueOf(comicResponse.getComments_count() / 10000) + "W");
        }

//        if (comicResponse.getLikes_count() < 10000) {
//            likeCountTV.setText(String.valueOf(comicResponse.getLikes_count()));
//        } else {
//            likeCountTV.setText(String.valueOf(comicResponse.getLikes_count() / 10000) + "W");
//        }
//
//        if (comicResponse.getRecommend_count() < 10000) {
//            shareCountTV.setText(String.valueOf(comicResponse.getRecommend_count()));
//        } else {
//            shareCountTV.setText(String.valueOf(comicResponse.getRecommend_count() / 10000) + "W");
//        }

        likeStatusCB.setChecked(comicResponse.is_liked());

        comicDetailResponse = comicResponse;
        invalidateOptionsMenu();
    }

    private void loadNewComment(final int newCommentOffset) {
        KKMHApp.getRestClient().getComicComments("",newCommentOffset, RestClient.DEFAULT_LIMIT, comicID, new Callback<CommentResponse>() {
            @Override
            public void success(CommentResponse commentResponse, Response response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    mComicDetailAdapter.initData(comicDetailResponse, commentResponse.getComments());
                }
                mComicDetailAdapter.addCommentList(commentResponse.getComments());
            }

            @Override
            public void failure(RetrofitError error) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                RetrofitErrorUtil.handleError(ComicDetailActivity.this, error);
            }
        });
    }

    private void updateComicFavStatus() {
        if(!UserUtil.checkUserLogin(ComicDetailActivity.this)){
            return;
        }
        if (comicDetailResponse.is_favourite()) {
            makeToast("取消收藏中...");
            KKMHApp.getRestClient().delFavComic(comicDetailResponse.getId(), new Callback<EmptyDataResponse>() {
                @Override
                public void success(EmptyDataResponse emptyDataResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(ComicDetailActivity.this, response)){
                        return;
                    }
                    makeToast("取消收藏成功");
                    comicDetailResponse.setIs_favourite(false);
                    mComicDetailAdapter.updateFavStatus();
//                    invalidateOptionsMenu();
                }

                @Override
                public void failure(RetrofitError error) {
                    makeToast("取消收藏失败");
                    RetrofitErrorUtil.handleError(ComicDetailActivity.this, error);
                }
            });
        } else {
            makeToast("收藏中...");
            KKMHApp.getRestClient().addFavComic(comicDetailResponse.getId(), new Callback<EmptyDataResponse>() {
                @Override
                public void success(EmptyDataResponse emptyDataResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(ComicDetailActivity.this, response)){
                        return;
                    }
                    makeToast("收藏成功");
                    comicDetailResponse.setIs_favourite(true);
                    mComicDetailAdapter.updateFavStatus();
//                    invalidateOptionsMenu();
                }

                @Override
                public void failure(RetrofitError error) {
                    makeToast("收藏失败");
                    RetrofitErrorUtil.handleError(ComicDetailActivity.this, error);
                }
            });
        }
    }

    private void gotoLogin() {
        Intent intent = new Intent(ComicDetailActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void makeToast(String toastString) {
        Toast.makeText(ComicDetailActivity.this, toastString, Toast.LENGTH_SHORT).show();
    }

    private void takeFavChangeAction() {
        if(!UserUtil.checkUserLogin(ComicDetailActivity.this)){
            return;
        }
        updateComicFavStatus();
    }


    private int mLastScrolly;
    private int mScrollLegth;
    private int mCurrentSrolly;
    private long mLastTime;
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if(firstScroll){
            mScrollLegth = 0;
            mLastScrolly = scrollY;
        }
        if(dragging){
            mScrollLegth += (scrollY - mLastScrolly);
        }
        mLastTime = System.currentTimeMillis();
        mLastScrolly = scrollY;
        mCurrentSrolly = scrollY;
    }

    @Override
    public void onDownMotionEvent() {
        mScrollLegth = 0;
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        long timeSpace = System.currentTimeMillis() - mLastTime;
        if (!commentMode) {
            if(Math.abs(mScrollLegth) <= 1 && (mLayoutManager.findLastVisibleItemPosition() < mComicDetailAdapter.getCommentPosition() - 2) && timeSpace > 1000){
                if(isFullScreenModeOn()){
                    setFullScreenModeOff();
                }else{
                    if(mCurrentSrolly > 200 ){
                        setFullScreenModeOn();
                    }
                }
            }
            if (scrollState == ScrollState.UP) {
                if (mLayoutManager.findLastVisibleItemPosition() < mComicDetailAdapter.getCommentPosition() - 2) {
                    if (isFullScreenModeOff()) {
                        setFullScreenModeOn();
                    }
                } else {
                    if (isFullScreenModeOn()) {
                        setFullScreenModeOff();
                    }
                }
            } else if (scrollState == ScrollState.DOWN && mScrollLegth < -250) {
                if (isFullScreenModeOn()) {
                    setFullScreenModeOff();
                }
            }
        }
    }

    private boolean isFullScreenModeOff() {
        return getActionBarToolbar().getTranslationY() == 0;

    }

    private boolean isFullScreenModeOn() {
        return getActionBarToolbar().getTranslationY() == -getActionBarToolbar().getHeight();
    }

    private void setFullScreenModeOn() {
        setFullScreenMode(-getActionBarToolbar().getHeight(), true);
    }

    private void setFullScreenModeOff() {
        setFullScreenMode(0, false);
    }

    private void setFullScreenMode(final float toTranslationY, boolean fullscreenModeStatus) {

//        ObjectAnimator rvOA = ObjectAnimator.ofFloat(recyclerViewContainerLL, "translationY", toTranslationY);
        ObjectAnimator actionbarOA = ObjectAnimator.ofFloat(getActionBarToolbar(), "translationY", toTranslationY);

        float belowLayoutAlpha;
        if (!fullscreenModeStatus) {

            belowLayoutAlpha = 1;
        } else {
            belowLayoutAlpha = 0;
        }
        ObjectAnimator dividerLineAlphaOA = ObjectAnimator.ofFloat(mDividerLine, "alpha", belowLayoutAlpha);
        ObjectAnimator dividerLinetranslationYOA = ObjectAnimator.ofFloat(mDividerLine, "translationY", toTranslationY);

        ObjectAnimator belowLayoutOA = ObjectAnimator.ofFloat(belowLayout, "alpha", belowLayoutAlpha);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(actionbarOA, belowLayoutOA, dividerLineAlphaOA, dividerLinetranslationYOA);//rvOA, actionbarOA, belowLayoutOA);
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) (recyclerViewContainerLL).getLayoutParams();
                lp.height = (int) -toTranslationY + (int) belowLayoutHeight + getScreenHeight() - lp.topMargin - lp.bottomMargin;
                recyclerViewContainerLL.requestLayout();
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animSet.start();


    }


    private void hideStatusBar()
    {

            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


//            WindowManager.LayoutParams attrs = getWindow().getAttributes();
//            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().setAttributes(attrs);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//        m_contentView.requestLayout();
    }


    @Override
    protected boolean isOnGestureBack(MotionEvent event) {
        return true;
    }
}
