package com.kuaikan.comic.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.Share.Oauth2AccessToken;
import com.kuaikan.comic.rest.PicassoProvider;
import com.kuaikan.comic.rest.model.API.VersionResponse;
import com.kuaikan.comic.service.PollingService;
import com.kuaikan.comic.ui.AboutActivity;
import com.kuaikan.comic.ui.MainActivity;
import com.kuaikan.comic.ui.SuggestionAppActivity;
import com.kuaikan.comic.util.ImageUtil;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.ServiceUtils;
import com.kuaikan.comic.util.UIUtil;
import com.kuaikan.comic.util.UserUtil;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;
import com.umeng.analytics.MobclickAgent;

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

    public static MoreFragment newInstance() {
        MoreFragment moreFragment = new MoreFragment();
        return moreFragment;
    }

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
        mSinglelineHeight = getActivity().getResources().getDimensionPixelSize(R.dimen.fragment_more_recyclerview_singleline_height) * 2;
        return view;
    }

    private void showShare() {


        OnekeyShare oks = new OnekeyShare();
        oks.setTitle("嘿！推荐下载快看漫画！");
        final Bitmap bitmap = ImageUtil.drawableToBitmap(getActivity().getResources().getDrawable(R.drawable.ic_launcher));
        final String path = ImageUtil.getImageSDPath(getActivity(),bitmap);
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
                }
            }
        });
        oks.show(getActivity());
    }

    private void checkUpdate(){
        KKMHApp.getRestClient().checkUpdate(new Callback<VersionResponse>() {
            @Override
            public void success(VersionResponse versionResponse, Response response) {
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
