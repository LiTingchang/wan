package com.kuaikan.comic.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kuaikan.comic.R;
import com.kuaikan.comic.util.ImageUtil;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.tencent.qzone.QZone;
import timber.log.Timber;

public class WebViewActivity extends BaseActivity implements View.OnClickListener{

    public static final String WEBVIEW_TITLE_FLAG = "webview_title";

    public static final String WEBVIEW_URL_FLAG = "webview_url";

    public static final String COVER_IMAGE_URL = "cover_img_url";

    @InjectView(R.id.activity_webview)
    WebView mWebView;
    @InjectView(R.id.toolbar_webview_share_btn)
    TextView mShareBtn;
    @InjectView(R.id.activity_webview_progress)
    ProgressBar mProgressBar;

    private String mTitle;
    private String mUrl;
    private String mCoverImgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(WebViewActivity.class.getSimpleName());

        setContentView(R.layout.activity_web_view);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_nav_delete);
        TextView titleTv = (TextView)getActionBarToolbar().findViewById(R.id.toolbar_center_title);
        titleTv.setText("网页");

        ButterKnife.inject(this);

        Intent intent = getIntent();
        if(intent != null){
            mUrl = intent.getStringExtra(WEBVIEW_URL_FLAG);
            mTitle = intent.getStringExtra(WEBVIEW_TITLE_FLAG);
            mCoverImgUrl = intent.getStringExtra(COVER_IMAGE_URL);
            if (!TextUtils.isEmpty(mTitle)) {
                titleTv.setText(mTitle);
            }
            if (!TextUtils.isEmpty(mUrl)) {
                initWebView(mUrl);
                mWebView.loadUrl(mUrl);
            }
        }

        mShareBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_webview_share_btn:
                showShare();
                break;
        }
    }

    protected void initWebView(String url) {
        // 初始化网页设置
        WebSettings webSettings = mWebView.getSettings();
        synCookies(WebViewActivity.this, url);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);			  // 正常加载缓存
//        webSettings.setJavaScriptEnabled(true);						 // 支持JavaScript
        webSettings.setSupportZoom(true);							  // 不支持网页缩放
//        webSettings.setBuiltInZoomControls(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);  // 滚动条悬浮在网页上，不占页面宽度
        mWebView.setOnCreateContextMenuListener(this);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }
            public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed() ;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.equals("about:blank")) {
                    mWebView.loadUrl(url);
                }
                return false;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(WebViewActivity.this);
                b.setTitle(R.string.app_name);
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        });
                b.setCancelable(false);
                b.create();
                b.show();
                return true;
            };

            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(WebViewActivity.this);
                b.setTitle(R.string.app_name);
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        });
                b.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        });
                b.setCancelable(false);
                b.create();
                b.show();
                return true;
            };

            @Override
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, final JsPromptResult result) {

                return true;
            };
        });

    }

    public static void synCookies(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.setCookie(url, PreferencesStorageUtil.readKKCookie(context));//cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        mWebView.stopLoading();
        mWebView.destroy();
        mWebView = null;
        super.onDestroy();
    }

    private void  showShare() {
        OnekeyShare oks = new OnekeyShare();
        oks.setTitle(mTitle);
        oks.setText("快看！一分钟一个超赞故事！！");
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            //自定义分享的回调想要函数
            @Override
            public void onShare(final Platform platform,
                                final cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                if("Wechat".equals(platform.getName())){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            paramsToShare.setUrl(mUrl);
                            paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                            paramsToShare.setImagePath(ImageUtil.getImageSDPath(WebViewActivity.this, mCoverImgUrl));
                        }
                    }).start();
                }
                if("WechatMoments".equals(platform.getName())){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            paramsToShare.setUrl(mUrl);
                            paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                            paramsToShare.setImagePath(ImageUtil.getImageSDPath(WebViewActivity.this, mCoverImgUrl));


//                            paramsToShare.setShareType(Platform.SHARE_IMAGE);
//                            Bitmap imageData = ImageUtil.getImageBitmap(ComicDetailActivity.this, comicDetailResponse.getCover_image_url());
//                            paramsToShare.setImageData(imageData);
//                            paramsToShare.setImagePath(ImageUtil.getImageSDPath(ComicDetailActivity.this, comicDetailResponse.getCover_image_url()));
                        }
                    }).start();
                }
                if("SinaWeibo".equals(platform.getName())){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            paramsToShare.setImageUrl(mCoverImgUrl);
                            paramsToShare.setText(mTitle + "（来自@快看漫画 ）完整内容戳：" + platform.getShortLintk(mUrl, false));
                        }
                    }).start();
                }
                if(QZone.NAME.equals(platform.getName()) || "QQ".equals(platform.getName())){
                    //限制微博分享的文字不能超过20
                    paramsToShare.setTitleUrl(mUrl);
                    paramsToShare.setImageUrl(mCoverImgUrl);
                    paramsToShare.setSite("快看漫画");
                    paramsToShare.setSiteUrl("http://kuaikanmanhua.com/");
                }
            }
        });

        oks.show(WebViewActivity.this);
    }

}
