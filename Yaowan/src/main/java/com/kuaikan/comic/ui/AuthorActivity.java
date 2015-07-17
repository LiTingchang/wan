package com.kuaikan.comic.ui;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.User;
import com.kuaikan.comic.ui.adapter.AuthorAdapter;
import com.kuaikan.comic.ui.view.PullToZoomListView;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.UIUtil;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AuthorActivity extends BaseActivity implements View.OnClickListener{

    public static final String KEY_AUTHOR_ID = "key_author_id";
    long authorId;
    @InjectView(R.id.author_pull2zoomlistview)
    PullToZoomListView mPullToZoomListView;
    @InjectView(R.id.activity_author_back_layout)
    FrameLayout mAuthorBackLayout;

//    @InjectView(R.id.recyclerView)
//    RecyclerView mRecyclerView;

    private ClipboardManager mClipboardManager;
    Transformation roundedTransformation;
    private String mApkPackage;
    private AuthorAdapter mAuthorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        if(intent != null){
            authorId = intent.getLongExtra(KEY_AUTHOR_ID , 0l);
            if(authorId == 0l){
                authorId = 357234l;
//                return;
            }
        }
        getData();

        mClipboardManager = (ClipboardManager) AuthorActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
        roundedTransformation = new RoundedTransformationBuilder()
                .borderWidthDp(0)
                .cornerRadius(AuthorActivity.this.getResources().getDimensionPixelSize(R.dimen.dialog_show_comic_author_avatar) / 2)
                .oval(false)
                .build();
        mAuthorBackLayout.setOnClickListener(this);

    }

    public class AuthorHeaderViewHolder{
        @InjectView(R.id.author_comic_avatar)
        ImageView mAvatar;
        @InjectView(R.id.author_nick_name)
        TextView nicknameTV;
        @InjectView(R.id.author_intro)
        TextView introTV;
        public AuthorHeaderViewHolder(View itemView) {
            ButterKnife.inject(this, itemView);
        }

    }

    public static class ThirdPartViewHolder{
        @InjectView(R.id.author_show_comic_weibo_ly)
        RelativeLayout mWeiboLayout;
        @InjectView(R.id.author_show_comic_weibo_tv)
        TextView mWeiboNickName;
        @InjectView(R.id.author_show_comic_weixin_ly)
        RelativeLayout mWechatLayout;
        @InjectView(R.id.author_show_comic_weixin_tv)
        TextView mWeChatNickName;
        @InjectView(R.id.author_show_comic_link_ly)
        RelativeLayout mLinkLayout;
        @InjectView(R.id.author_show_comic_link_tv)
        TextView mSiteTv;
        @InjectView(R.id.author_show_comic_download_ly)
        LinearLayout mDownloadLayout;
        public ThirdPartViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }

    private AuthorHeaderViewHolder mAuthorHeaderViewHolder;
    private ThirdPartViewHolder mThirdPartViewHolder;


    private void getData(){
        KKMHApp.getRestClient().getUserDetail(authorId, new Callback<User>() {
            @Override
            public void success(User user, Response response) {

//                mPullToZoomListView.addHeaderScraleView();
                View headerViewLayout = LayoutInflater.from(AuthorActivity.this).inflate(R.layout.view_author_header_layout, null);
                mAuthorHeaderViewHolder = new AuthorHeaderViewHolder(headerViewLayout);
                mPullToZoomListView.addHeaderScraleView(headerViewLayout);

//                mPullToZoomListView.setAdapter(new ArrayAdapter<String>(MainActivity.this,
//                        android.R.layout.simple_list_item_1, adapterData));
//                mPullToZoomListView.getHeaderView().setImageResource(R.drawable.splash01);
//                mPullToZoomListView.getHeaderView().setScaleType(ImageView.ScaleType.CENTER_CROP);

                mAuthorHeaderViewHolder.nicknameTV.setText(user.getNickname());
                mAuthorHeaderViewHolder.introTV.setText(user.getIntro());
                View thirdPartLayout = LayoutInflater.from(AuthorActivity.this).inflate(R.layout.view_author_thirdpart_layout, null);
                mThirdPartViewHolder = new ThirdPartViewHolder(thirdPartLayout);
                mPullToZoomListView.addHeaderView(thirdPartLayout);

                mApkPackage = user.getAndroid();
                if(!TextUtils.isEmpty(user.getAvatar_url())) {
                    Picasso.with(AuthorActivity.this)
                            .load(user.getAvatar_url())
                            .resize(AuthorActivity.this.getResources().getDimensionPixelSize(R.dimen.dialog_show_comic_author_avatar), AuthorActivity.this.getResources().getDimensionPixelSize(R.dimen.dialog_show_comic_author_avatar))
                            .transform(roundedTransformation)
                            .into(mAuthorHeaderViewHolder.mAvatar);
                }
                if(!TextUtils.isEmpty(user.getWeibo_name())){
                    mThirdPartViewHolder.mWeiboLayout.setVisibility(View.VISIBLE);
                    mThirdPartViewHolder.mWeiboNickName.setText(user.getWeibo_name());
                }else{
                    mThirdPartViewHolder.mWeiboLayout.setVisibility(View.GONE);
                }

                if(!TextUtils.isEmpty(user.getWechat())){
                    mThirdPartViewHolder.mWechatLayout.setVisibility(View.VISIBLE);
                    mThirdPartViewHolder.mWeChatNickName.setText(user.getWechat());
                }else{
                    mThirdPartViewHolder.mWechatLayout.setVisibility(View.GONE);
                }

                if(!TextUtils.isEmpty(user.getSite())){
                    mThirdPartViewHolder.mLinkLayout.setVisibility(View.VISIBLE);
                    mThirdPartViewHolder.mSiteTv.setText(user.getSite());
                }else{
                    mThirdPartViewHolder.mLinkLayout.setVisibility(View.GONE);
                }

                if(!TextUtils.isEmpty(user.getAndroid())){
                    mThirdPartViewHolder.mDownloadLayout.setVisibility(View.VISIBLE);
                }else{
                    mThirdPartViewHolder.mDownloadLayout.setVisibility(View.GONE);
                }
                mThirdPartViewHolder.mWeiboLayout.setOnClickListener(AuthorActivity.this);
                mThirdPartViewHolder.mWechatLayout.setOnClickListener(AuthorActivity.this);
                mThirdPartViewHolder.mLinkLayout.setOnClickListener(AuthorActivity.this);
                mThirdPartViewHolder.mDownloadLayout.setOnClickListener(AuthorActivity.this);
                mAuthorAdapter = new AuthorAdapter(AuthorActivity.this, user.getTopics(), new AuthorAdapter.LoadMoreListener() {
                    @Override
                    public void onLoadMoreTopic(int newCurrentOffset) {

                    }
                });
                mPullToZoomListView.setAdapter(mAuthorAdapter);
                mAuthorAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(RetrofitError error) {
                RetrofitErrorUtil.handleError(AuthorActivity.this, error);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.author_show_comic_weibo_ly:
                mClipboardManager.setPrimaryClip(ClipData.newPlainText("Label", mThirdPartViewHolder.mWeiboNickName.getText().toString()));
                UIUtil.showThost(AuthorActivity.this, "已复制");
                break;
            case R.id.author_show_comic_weixin_ly:
                mClipboardManager.setPrimaryClip(ClipData.newPlainText("Label",mThirdPartViewHolder.mWeChatNickName.getText().toString()));
                UIUtil.showThost(AuthorActivity.this, "已复制");
                break;
            case R.id.author_show_comic_link_ly:
//                mClipboardManager.setText(mSiteTv.getText().toString());
                if(!TextUtils.isEmpty(mThirdPartViewHolder.mSiteTv.getText().toString().trim())){
                    Intent intent = new Intent();
                    intent.setClass(AuthorActivity.this, WebViewActivity.class);
                    intent.putExtra(WebViewActivity.WEBVIEW_URL_FLAG, "已复制");
                    AuthorActivity.this.startActivity(intent);
                }
                break;
            case R.id.author_show_comic_download_ly:
                startMarket2Download(mApkPackage);
                break;
            case R.id.activity_author_back_layout:
                AuthorActivity.this.finish();
                break;

        }
    }


    private void startMarket2Download(String apkpackage){
        if(!TextUtils.isEmpty(apkpackage)){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + apkpackage));
            try{
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                UIUtil.showThost(AuthorActivity.this, "未发现应用商店");
            }
        }
    }


}
