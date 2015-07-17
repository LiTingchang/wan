//package com.kuaikan.comic.ui.fragment;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.DialogFragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Toast;
//
//import com.kuaikan.comic.KKMHApp;
//import com.kuaikan.comic.R;
//import com.kuaikan.comic.rest.model.API.ComicDetailResponse;
//import com.kuaikan.comic.rest.model.API.ShareComicResponse;
//import com.kuaikan.comic.ui.LoginActivity;
//import com.kuaikan.comic.util.Constants;
//import com.kuaikan.comic.util.PreferencesStorageUtil;
//import com.kuaikan.comic.Share.Oauth2AccessToken;
//import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
//import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
//import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
//import com.tencent.mm.sdk.openapi.IWXAPI;
//import com.tencent.mm.sdk.openapi.WXAPIFactory;
//
//import retrofit.Callback;
//import retrofit.RetrofitError;
//import retrofit.client.Response;
//import timber.log.Timber;
//
///**
// * Created by skyfishjy on 1/6/15.
// */
//public class ShareComicDialogFragment extends DialogFragment {
//    private static final String KEY_COMIC_DETAIL = "comic_detail";
//    Dialog dialog;
//    Oauth2AccessToken accessToken;
//    private ComicDetailResponse comicDetailResponse;
//    private IWXAPI wxapi;
//    private SendMessageToWX.Req sendToWXReq;
//
//    private OnShareListener shareCallback;
//
//    public static ShareComicDialogFragment newInstance(ComicDetailResponse response) {
//        ShareComicDialogFragment comicDetailFragment = new ShareComicDialogFragment();
//        Bundle args = new Bundle();
//        args.putParcelable(KEY_COMIC_DETAIL, response);
//        comicDetailFragment.setArguments(args);
//        return comicDetailFragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        comicDetailResponse = getArguments().getParcelable(KEY_COMIC_DETAIL);
//        wxapi = WXAPIFactory.createWXAPI(getActivity(), Constants.WECHAT_APP_KEY);
//        WXWebpageObject webpage = new WXWebpageObject();
//        webpage.webpageUrl = comicDetailResponse.getUrl();
//        WXMediaMessage msg = new WXMediaMessage(webpage);
//        msg.title = comicDetailResponse.getTitle() + " 作者: " + comicDetailResponse.getTopic().getUser().getNickname();
//        msg.description = "快看漫画，一分钟一个超赞的漫画！";
//        msg.setThumbImage(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_share_to_social_logo));
//        sendToWXReq = new SendMessageToWX.Req();
//        sendToWXReq.transaction = "webpage" + System.currentTimeMillis();
//        sendToWXReq.message = msg;
//        Timber.tag(ShareComicDialogFragment.class.getSimpleName());
//    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.dialog_share, null);
//        builder.setView(view);
//        dialog = builder.create();
//        view.findViewById(R.id.share_to_weibo).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                accessToken = PreferencesStorageUtil.readWeiboAccessToken(getActivity());
//                if (accessToken.isSessionValid()) {
//                    KKMHApp.getRestClient().shareComic("《" + comicDetailResponse.getTitle() + "》"
//                            + "作者:" + comicDetailResponse.getTopic().getUser().getNickname()
//                            + " 分享自@快看漫画 ，看完整漫画点>>>" + comicDetailResponse.getUrl(), comicDetailResponse.getCover_image_url(), new Callback<ShareComicResponse>() {
//                        @Override
//                        public void success(ShareComicResponse shareComicResponse, Response response) {
//                            Toast.makeText(getActivity(), "分享成功!", Toast.LENGTH_SHORT).show();
//                            shareCallback.onShareResult(true);
//                            dialog.dismiss();
//                        }
//
//                        @Override
//                        public void failure(RetrofitError error) {
//                            Toast.makeText(getActivity(), "分享失败,请重试", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                        }
//                    });
//                } else {
//                    Intent intent = new Intent(getActivity(), LoginActivity.class);
//                    startActivity(intent);
//                }
//
//            }
//        });
//
//        view.findViewById(R.id.share_to_wechat).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!wxapi.isWXAppInstalled()) {
//                    Toast.makeText(getActivity(), "您还未安装微信客户端",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    sendToWXReq.scene = SendMessageToWX.Req.WXSceneSession;
//                    wxapi.sendReq(sendToWXReq);
//                }
//
//            }
//        });
//
//        view.findViewById(R.id.share_to_wechat_moment).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!wxapi.isWXAppInstalled()) {
//                    Toast.makeText(getActivity(), "您还未安装微信客户端",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    sendToWXReq.scene = SendMessageToWX.Req.WXSceneTimeline;
//                    wxapi.sendReq(sendToWXReq);
//                }
//
//            }
//        });
//        return dialog;
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//
//        try {
//            shareCallback = (OnShareListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement OnShareListener");
//        }
//    }
//
//    public interface OnShareListener {
//        public void onShareResult(boolean result);
//    }
//}
