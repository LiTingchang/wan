package com.kuaikan.comic.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.Share.Oauth2AccessToken;
import com.kuaikan.comic.rest.PicassoProvider;
import com.kuaikan.comic.rest.model.API.RecommendAppResponse;
import com.kuaikan.comic.rest.model.API.VersionResponse;
import com.kuaikan.comic.rest.model.App;
import com.kuaikan.comic.service.PollingService;
import com.kuaikan.comic.ui.AboutActivity;
import com.kuaikan.comic.ui.MainActivity;
import com.kuaikan.comic.ui.RecommendAppActivity;
import com.kuaikan.comic.ui.SuggestionAppActivity;
import com.kuaikan.comic.util.ImageUtil;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.ServiceUtils;
import com.kuaikan.comic.util.UIUtil;
import com.kuaikan.comic.util.UserUtil;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by skyfishjy on 12/18/14.
 */
public class MoreFragment extends Fragment {

    public static final String TAG = MoreFragment.class.getSimpleName();

    @InjectView(R.id.home_about_kk)
    RelativeLayout aboutKKRL;
    @InjectView(R.id.more_logout)
    TextView loginOut;
    //    @InjectView(R.id.user_home_login_next)
//    TextView nameTV;
    @InjectView(R.id.clean_cache)
    LinearLayout cleanCacheLL;
    @InjectView(R.id.share_app)
    TextView shareAppTV;

    @InjectView(R.id.send_feedback)
    RelativeLayout sendFeedbackRL;
//
//    @InjectView(R.id.goto_liwushou)
//    RelativeLayout gotoLiwushuoRL;

    @InjectView(R.id.more_appraise)
    TextView mMoreAppreise;

    Oauth2AccessToken accessToken;
    Transformation roundedTransformation;

    @InjectView(R.id.more_check_update)
    TextView mCheckUpdate;

    @InjectView(R.id.more_recommend_app_recyclerview)
    RecyclerView mRecycleView;

    @InjectView(R.id.more_recommend_layout)
    LinearLayout mRecommendLayout;

    @InjectView(R.id.more_recommend_app_more_layout)
    LinearLayout mRecommendMoreLayout;

    public static MoreFragment newInstance() {
        MoreFragment moreFragment = new MoreFragment();
        return moreFragment;
    }

    GridLayoutManager mLayoutManager;

    private int mSinglelineHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        ButterKnife.inject(this, view);

        aboutKKRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("是否要登出").setTitle("").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                PreferencesStorageUtil.clearAccessToken(getActivity());
                PreferencesStorageUtil.clearSignUser(getActivity());
                accessToken = PreferencesStorageUtil.readWeiboAccessToken(getActivity());
                KKMHApp.refreshRestClient(null, null);
//                refreshUserView();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
                ServiceUtils.stopLocalPushService(getActivity(), PollingService.class, PollingService.ACTION);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
            }
        });
        final AlertDialog dialog = builder.create();

        if(UserUtil.isUserLogin(getActivity())){
            loginOut.setVisibility(View.VISIBLE);
        }else{
            loginOut.setVisibility(View.INVISIBLE);
        }

        loginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!accessToken.isSessionValid()) {
//                    Intent intent = new Intent(getActivity(), LoginActivity.class);
//                    startActivity(intent);
//                } else {
                dialog.show();
//                }
            }
        });

        roundedTransformation = new RoundedTransformationBuilder()
                .borderWidthDp(0)
                .cornerRadius(25)
                .oval(false)
                .build();

        cleanCacheLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PicassoProvider.cleanMemoryCache();
                Toast.makeText(getActivity(), "清理成功", Toast.LENGTH_SHORT).show();
            }
        });

        final ShareAppDialogFragment shareAppDialogFragment = ShareAppDialogFragment.newInstance();
        shareAppTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                shareAppDialogFragment.show(getChildFragmentManager().beginTransaction(), "share_app");
                showShare();
            }
        });

        sendFeedbackRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SuggestionAppActivity.class);
                startActivity(intent);
            }
        });

        mMoreAppreise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.startMarket(getActivity());
            }
        });

//        gotoLiwushuoRL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Uri uri = Uri.parse("http://www.liwushuo.com/m/");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
//            }
//        });

        mCheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpdate();
            }
        });
        ShareSDK.initSDK(getActivity());
        mRecycleView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecycleView.setLayoutManager(mLayoutManager);
        mSinglelineHeight = getActivity().getResources().getDimensionPixelSize(R.dimen.fragment_more_recyclerview_singleline_height) * 2;
        initRecycleView();
        return view;
    }


    private void initRecycleView(){
        KKMHApp.getRestClient().getRecommendAppTop(new Callback<RecommendAppResponse>() {
            @Override
            public void success(RecommendAppResponse recommendAppResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                    return;
                }
                if (recommendAppResponse != null && recommendAppResponse.getApps().size() > 0) {
                    mRecommendLayout.setVisibility(View.VISIBLE);
                    if(recommendAppResponse.getApps().size() > 2){
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mSinglelineHeight);
                        mRecycleView.setLayoutParams(layoutParams);
                    }
                    mRecycleView.setAdapter(new RecommendAppAdapter(recommendAppResponse.getApps()));
                    if (recommendAppResponse.isHasmore()) {
                        mRecommendMoreLayout.setVisibility(View.VISIBLE);
                        mRecommendMoreLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), RecommendAppActivity.class);
                                getActivity().startActivity(intent);
                            }
                        });
                    } else {
                        mRecommendMoreLayout.setVisibility(View.GONE);
                    }
                } else {
                    mRecommendLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("recommendAppResponse--->failure" );
                mRecommendLayout.setVisibility(View.GONE);
                RetrofitErrorUtil.handleError(getActivity(), error);
            }
        });
    }

    public class RecommendAppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<App> mList;

        public RecommendAppAdapter(List<App> apps){
            this.mList = apps;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecommendAppViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_recommend_app, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
            RecommendAppViewHolder recommendAppViewHolder = (RecommendAppViewHolder) holder;
            try {
                if(!TextUtils.isEmpty(mList.get(position).getCoverUrl())){
                    Picasso.with(getActivity())
                            .load(mList.get(position).getCoverUrl())
                            .into(recommendAppViewHolder.mRecommendAppImage);
                }
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
            recommendAppViewHolder.mRecommendAppName.setText(mList.get(position).getTitle());
            recommendAppViewHolder.mRecommendAppDesc.setText(mList.get(position).getDesc());
            recommendAppViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Map <String,String> maps = new HashMap<String, String>();
//                    maps.put(Constants.EVENT_MORE_RECOMMEND_APP_NAME, mList.get(position).getTitle());
//                    MobclickAgent.onEventValue(getActivity(), Constants.EVENT_MORE_RECOMMEND_APP_ONCLICK, maps,0);
                    if(!TextUtils.isEmpty(mList.get(position).getPackagename())){
                        UIUtil.startMarket(getActivity(), mList.get(position).getPackagename());
                    }else if(!TextUtils.isEmpty(mList.get(position).getUrl())){
                        Uri uri = Uri.parse(mList.get(position).getUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if(mList != null){
                return mList.size();
            }
            return 0;
        }
    }

    public class RecommendAppViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.view_recommend_app_image)
        ImageView mRecommendAppImage;
        @InjectView(R.id.view_recommend_app_name)
        TextView mRecommendAppName;
        @InjectView(R.id.view_recommend_app_desc)
        TextView mRecommendAppDesc;

        public RecommendAppViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    private void showShare() {

//        webpage.webpageUrl = "http://www.kuaikanmanhua.com/m/";
//        WXMediaMessage msg = new WXMediaMessage(webpage);
//        msg.title = "快看漫画，一分钟看完一个超赞的漫画";
//        msg.description = "各种爆笑治愈脑洞故事，完美适配手机阅读，睡前必备神器，等你来看！";
//        msg.setThumbImage(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_share_to_social_logo));
//        sendToWXReq = new SendMessageToWX.Req();
//        sendToWXReq.transaction = "webpage" + System.currentTimeMillis();
//        sendToWXReq.message = msg;

        OnekeyShare oks = new OnekeyShare();
        oks.setTitle("嘿！推荐下载快看漫画！");
//        oks.setText("快看！一分钟一个超赞故事！！");
        final Bitmap bitmap = ImageUtil.drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ic_launcher));
        final String path = ImageUtil.getImageSDPath(getActivity(),bitmap);
//        BitmapDrawable nitmapDrawable = new BitmapDrawable(getActivity().getResources().getDrawable(R.drawable.logo));
//        oks.setCallback(this);
        //此处为本demo关键为一键分享折子定义分享回调函数   shareContentCustomuzeCallback
        //自定义平台可以通过判断不同的平台来实现不同平台间的不同操作
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            //自定义分享的回调想要函数
            @Override
            public void onShare(Platform platform,
                                final cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                //点击微信好友
                if ("Wechat".equals(platform.getName())) {
                    paramsToShare.setUrl("http://www.kuaikanmanhua.com/m/");
                    paramsToShare.setText("一分钟一个超好玩图片！简直是等车、睡前、蹲坑必备神器！！");
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);

                    paramsToShare.setImagePath(path);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//                            paramsToShare.setImagePath(ImageUtil.getImageSDPath(ComicDetailActivity.this, comicDetailResponse.getCover_image_url()));
//                        }
//                    }).start();
                }
                if ("WechatMoments".equals(platform.getName())) {
                    paramsToShare.setUrl("http://www.kuaikanmanhua.com/m/");
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setText("发现一个蹲坑神器APP叫 快看漫画！建议大家下来看！");
                    paramsToShare.setImagePath(path);
                }

                //点击新浪微博
                if ("SinaWeibo".equals(platform.getName())) {
                    //限制微博分享的文字不能超过20
                    paramsToShare.setText("发现了@快看漫画 这个APP，一分钟一个超好玩图片！消磨无聊时光～简直是等车、睡前、蹲坑必备神器！！推荐大家下载快看！！" + "http://www.kuaikanmanhua.com/m/");
                    paramsToShare.setImagePath(path);
//                    if(paramsToShare.getText().length() > 20){
//                        Toast.makeText(context, "分享长度不能超过20个字", Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        });
        oks.show(getActivity());
    }

    private void checkUpdate(){
        KKMHApp.getRestClient().checkUpdate(new Callback<VersionResponse>() {
            @Override
            public void success(VersionResponse versionResponse, Response response) {
//                System.out.println("response--->" + response.toString());
                //TODO 打开应用商店
                if(versionResponse != null && !TextUtils.isEmpty(versionResponse.getVersion())){
                    UIUtil.startMarket(getActivity());
                }else{
                    UIUtil.showThost(getActivity(),"已是最新版");
                }

            }

            @Override
            public void failure(RetrofitError error) {
                RetrofitErrorUtil.handleError(getActivity(), error);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.tag(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("个人中心");
        refreshUserView();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("个人中心");
    }

    public void refreshUserView() {
        accessToken = PreferencesStorageUtil.readWeiboAccessToken(getActivity());
    }

}
