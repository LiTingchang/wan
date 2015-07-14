//package com.kuaikan.comic.ui.fragment;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.ActivityNotFoundException;
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.kuaikan.comic.KKMHApp;
//import com.kuaikan.comic.R;
//import com.kuaikan.comic.rest.model.User;
//import com.kuaikan.comic.ui.WebViewActivity;
//import com.kuaikan.comic.util.UIUtil;
//import com.makeramen.RoundedTransformationBuilder;
//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Transformation;
//
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//import retrofit.Callback;
//import retrofit.RetrofitError;
//import retrofit.client.Response;
//
///**
// * Created by skyfishjy on 2/10/15.
// */
//public class ShowComicAuthorDialogFragment extends DialogFragment implements View.OnClickListener{
//
//    private static final String KEY_AUTHOR_ID = "key_author_id";
//    Dialog dialog;
//    long authorId;
//    @InjectView(R.id.dialog_show_comic_avatar)
//    ImageView mAvatar;
//    @InjectView(R.id.nickname)
//    TextView nicknameTV;
//    @InjectView(R.id.intro)
//    TextView introTV;
//    @InjectView(R.id.dialog_show_comic_weibo_ly)
//    RelativeLayout mWeiboLayout;
//    @InjectView(R.id.dialog_show_comic_weibo_tv)
//    TextView mWeiboNickName;
//    @InjectView(R.id.dialog_show_comic_weixin_ly)
//    RelativeLayout mWechatLayout;
//    @InjectView(R.id.dialog_show_comic_weixin_tv)
//    TextView mWeChatNickName;
//    @InjectView(R.id.dialog_show_comic_link_ly)
//    RelativeLayout mLinkLayout;
//    @InjectView(R.id.dialog_show_comic_link_tv)
//    TextView mSiteTv;
//    @InjectView(R.id.dialog_show_comic_download_ly)
//    LinearLayout mDownloadLayout;
//
//    private String mApkPackage;
//
//    private ClipboardManager mClipboardManager;
//    Transformation roundedTransformation;
//
//    public static ShowComicAuthorDialogFragment newInstance(long authorId) {
//        ShowComicAuthorDialogFragment showComicAuthorDialogFragment = new ShowComicAuthorDialogFragment();
//        Bundle bundle = new Bundle();
//        bundle.putLong(KEY_AUTHOR_ID, authorId);
//        showComicAuthorDialogFragment.setArguments(bundle);
//        return showComicAuthorDialogFragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        authorId = getArguments().getLong(KEY_AUTHOR_ID, -1);
//        mClipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//        roundedTransformation = new RoundedTransformationBuilder()
//                .borderWidthDp(0)
//                .cornerRadius(getActivity().getResources().getDimensionPixelSize(R.dimen.dialog_show_comic_author_avatar) / 2)
//                .oval(false)
//                .build();
//
//    }
//
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.dialog_show_comic_author, null);
//        ButterKnife.inject(this, view);
//        mWeiboLayout.setOnClickListener(this);
//        mWechatLayout.setOnClickListener(this);
//        mLinkLayout.setOnClickListener(this);
//        mDownloadLayout.setOnClickListener(this);
//        builder.setView(view);
//        dialog = builder.create();
//        return dialog;
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.dialog_show_comic_weibo_ly:
//                mClipboardManager.setPrimaryClip(ClipData.newPlainText("Label", mWeiboNickName.getText().toString()));
//                UIUtil.showThost(getActivity(), "已复制");
//                break;
//            case R.id.dialog_show_comic_weixin_ly:
//                mClipboardManager.setPrimaryClip(ClipData.newPlainText("Label",mWeChatNickName.getText().toString()));
//                UIUtil.showThost(getActivity(), "已复制");
//                break;
//            case R.id.dialog_show_comic_link_ly:
////                mClipboardManager.setText(mSiteTv.getText().toString());
//                if(!TextUtils.isEmpty(mSiteTv.getText().toString().trim())){
//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(), WebViewActivity.class);
//                    intent.putExtra(WebViewActivity.WEBVIEW_URL_FLAG, "已复制");
//                    getActivity().startActivity(intent);
//                }
//                break;
//            case R.id.dialog_show_comic_download_ly:
//                startMarket2Download(mApkPackage);
//                break;
//
//        }
//    }
//
//    private void startMarket2Download(String apkpackage){
//        if(!TextUtils.isEmpty(apkpackage)){
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse("market://details?id=" + apkpackage));
//            try{
//                startActivity(intent);
//            } catch (ActivityNotFoundException ex) {
//                UIUtil.showThost(getActivity(), "未发现应用商店");
//            }
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        KKMHApp.getRestClient().getUserDetail(authorId, new Callback<User>() {
//            @Override
//            public void success(User user, Response response) {
//                nicknameTV.setText(user.getNickname());
//                introTV.setText(user.getIntro());
//                mApkPackage = user.getAndroid();
//                if(!TextUtils.isEmpty(user.getAvatar_url())) {
//                    Picasso.with(getActivity())
//                            .load(user.getAvatar_url())
//                            .resize(getActivity().getResources().getDimensionPixelSize(R.dimen.dialog_show_comic_author_avatar), getActivity().getResources().getDimensionPixelSize(R.dimen.dialog_show_comic_author_avatar))
//                            .transform(roundedTransformation)
//                            .into(mAvatar);
//                }
//                    if(!TextUtils.isEmpty(user.getWeibo_name())){
//                        mWeiboLayout.setVisibility(View.VISIBLE);
//                        mWeiboNickName.setText(user.getWeibo_name());
//                    }else{
//                        mWeiboLayout.setVisibility(View.GONE);
//                    }
//
//                    if(!TextUtils.isEmpty(user.getWechat())){
//                        mWechatLayout.setVisibility(View.VISIBLE);
//                        mWeChatNickName.setText(user.getWechat());
//                    }else{
//                        mWechatLayout.setVisibility(View.GONE);
//                    }
//
//                    if(!TextUtils.isEmpty(user.getSite())){
//                        mLinkLayout.setVisibility(View.VISIBLE);
//                        mSiteTv.setText(user.getSite());
//                    }else{
//                        mLinkLayout.setVisibility(View.GONE);
//                    }
//
//                    if(!TextUtils.isEmpty(user.getAndroid())){
//                        mDownloadLayout.setVisibility(View.VISIBLE);
////                        mSiteTv.setText(user.getSite());
//                    }else{
//                        mDownloadLayout.setVisibility(View.GONE);
//                    }
//
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//
//            }
//        });
//    }
//
//
//}
