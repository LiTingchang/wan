package com.kuaikan.comic.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.API.ShareComicResponse;
import com.kuaikan.comic.ui.LoginActivity;
import com.kuaikan.comic.util.Constants;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.kuaikan.comic.Share.Oauth2AccessToken;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by skyfishjy on 1/6/15.
 */
public class ShareAppDialogFragment extends DialogFragment {
    Dialog dialog;
    Oauth2AccessToken accessToken;
    private IWXAPI wxapi;
    private SendMessageToWX.Req sendToWXReq;

    public static ShareAppDialogFragment newInstance() {
        ShareAppDialogFragment comicDetailFragment = new ShareAppDialogFragment();
        return comicDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wxapi = WXAPIFactory.createWXAPI(getActivity(), Constants.WECHAT_APP_KEY);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://www.kuaikanmanhua.com/m/";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "快看漫画，一分钟看完一个超赞的漫画";
        msg.description = "各种爆笑治愈脑洞故事，完美适配手机阅读，睡前必备神器，等你来看！";
        msg.setThumbImage(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_share_to_social_logo));
        sendToWXReq = new SendMessageToWX.Req();
        sendToWXReq.transaction = "webpage" + System.currentTimeMillis();
        sendToWXReq.message = msg;
        Timber.tag(ShareAppDialogFragment.class.getSimpleName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_share, null);
        builder.setView(view);
        dialog = builder.create();
        view.findViewById(R.id.share_to_weibo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accessToken = PreferencesStorageUtil.readWeiboAccessToken(getActivity());
                if (accessToken.isSessionValid()) {
                    KKMHApp.getRestClient().shareComic("@快看漫画 ，一分钟看完一个超赞的漫画！戳链接看更多温暖治愈重口味无节操小短漫！>>>http://www.kuaikanmanhua.com/m/", "http://kuaikan-data.qiniudn.com/image/share/weibo_share_app_image.png", new Callback<ShareComicResponse>() {
                        @Override
                        public void success(ShareComicResponse shareComicResponse, Response response) {
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "分享成功!", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "分享失败,请重试", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        view.findViewById(R.id.share_to_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!wxapi.isWXAppInstalled()) {
                    Toast.makeText(getActivity(), "您还未安装微信客户端",
                            Toast.LENGTH_SHORT).show();
                } else {
                    sendToWXReq.scene = SendMessageToWX.Req.WXSceneSession;
                    wxapi.sendReq(sendToWXReq);
                }
            }
        });

        view.findViewById(R.id.share_to_wechat_moment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!wxapi.isWXAppInstalled()) {
                    Toast.makeText(getActivity(), "您还未安装微信客户端",
                            Toast.LENGTH_SHORT).show();
                } else {
                    sendToWXReq.scene = SendMessageToWX.Req.WXSceneTimeline;
                    wxapi.sendReq(sendToWXReq);
                }
            }
        });
        return dialog;
    }


}
