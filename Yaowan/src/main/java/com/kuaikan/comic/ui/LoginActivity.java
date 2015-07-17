package com.kuaikan.comic.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.SignUserInfo;
import com.kuaikan.comic.service.PollingService;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.ServiceUtils;
import com.kuaikan.comic.util.UIUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

//import com.sina.weibo.sdk.auth.AuthInfo;
//import com.sina.weibo.sdk.auth.WeiboAuthListener;
//import com.sina.weibo.sdk.auth.sso.SsoHandler;
//import com.sina.weibo.sdk.exception.WeiboException;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.login_weibo)
    Button loginWeiboButton;
    @InjectView(R.id.activity_login_register)
    TextView mRegisterBtn;
    @InjectView(R.id.activity_login_forget_pwd)
    TextView mForgetPwd;
    @InjectView(R.id.login_login_btn)
    Button mLoginBtn;
    @InjectView(R.id.activity_login_account)
    EditText mAccount;
    @InjectView(R.id.activity_login_pwd)
    EditText mPwd;
    @InjectView(R.id.login_layout)
    RelativeLayout mLayout;

    //    private AuthInfo mAuthInfo;
//    private Oauth2AccessToken mAccessToken;
//    private SsoHandler mSsoHandler;
    private ProgressDialog progressDialog;

    InputMethodManager mInputManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(LoginActivity.class.getSimpleName());

        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_nav_delete);
        TextView title = (TextView) getActionBarToolbar().findViewById(R.id.toolbar_center_title);
        title.setText("登录");

        ButterKnife.inject(this);


        loginWeiboButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    ssoLogin();
                                                }
                                            }
        );

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在登录");
        progressDialog.setCancelable(false);

        mRegisterBtn.setOnClickListener(this);
        mForgetPwd.setOnClickListener(this);
        mLoginBtn.setOnClickListener(loginOnClickListener);
        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                    mInputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    private void ssoLogin() {
        progressDialog.show();
        ShareSDK.initSDK(this);
        Platform weibo = ShareSDK.getPlatform(this, SinaWeibo.NAME);

        weibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
                progressDialog.dismiss();
                initUser();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                progressDialog.dismiss();
//                showFailureToast();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                progressDialog.dismiss();
//                showFailureToast();
            }
        });
        weibo.authorize(new String[]{"follow_app_official_microblog"});
    }


    private void initUser() {
        Platform weibo = ShareSDK.getPlatform(this, SinaWeibo.NAME);
        weibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
                progressDialog.dismiss();

                PlatformDb platDB = platform.getDb();//获取数平台数据DB
                //通过DB获取各种数据
                KKMHApp.refreshRestClient(platDB.getToken(), null);
                postSignup(stringObjectHashMap.get("id").toString(), platDB.getToken(), stringObjectHashMap.get("name").toString(), stringObjectHashMap.get("profile_image_url").toString());
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                progressDialog.dismiss();
//                showFailureToast();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                progressDialog.dismiss();
//                showFailureToast();
            }
        });
        weibo.showUser(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_login_register:
//                Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
//                intent.putExtra(RegisterActivity.REGISTER_VERIFY_PHONE, "18611429611");
//                LoginActivity.this.startActivity(intent);
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                registerIntent.putExtra(RegisterActivity.REGISTER_TYPE, RegisterActivity.ACTION_REGISTER);
                LoginActivity.this.startActivity(registerIntent);
                break;
            case R.id.activity_login_forget_pwd:
                Intent forgetPwdIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                forgetPwdIntent.putExtra(RegisterActivity.REGISTER_TITLE, "忘记密码");
                forgetPwdIntent.putExtra(RegisterActivity.REGISTER_TYPE, RegisterActivity.ACTION_RESET);
                LoginActivity.this.startActivity(forgetPwdIntent);
                break;
//            case R.id.login_login_btn:

            default:
                break;
        }
    }

    private View.OnClickListener loginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String account = mAccount.getText().toString();
            final String pwd = mPwd.getText().toString();
            if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(pwd)) {
                if(pwd.length() < 8){
                    UIUtil.showThost(LoginActivity.this, "密码长度至少为8位");
                    return;
                }
                if(pwd.length() > 30){
                    UIUtil.showThost(LoginActivity.this, "密码长度不能超过30位");
                    return;
                }
                if(account.length() != 11){
                    UIUtil.showThost(LoginActivity.this, "您输入的手机号无效，请重新输入");
                    return;
                }

                KKMHApp.getRestClient().phoneSignin(account, pwd, new Callback<SignUserInfo>() {
                    @Override
                    public void success(final SignUserInfo userInfo, Response response) {
                        if(RetrofitErrorUtil.handleResponse(LoginActivity.this, response)){
                            return;
                        }
                        for (retrofit.client.Header header : response.getHeaders()) {
                            if (null != header.getName() && header.getName().equals("Set-Cookie")) {
                                PreferencesStorageUtil.writeKKCookie(LoginActivity.this, header.getValue());
                                KKMHApp.refreshRestClient("", header.getValue());
                                break;
                            }
                        }
                        showSuccessToast();
                        PreferencesStorageUtil.writeUserUserInfo(LoginActivity.this, userInfo);
                        finish();
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        LoginActivity.this.startActivity(intent);
                        ServiceUtils.startLocalPushService(LoginActivity.this, PreferencesStorageUtil.getLocalPushPollingInterval(LoginActivity.this), PollingService.class, PollingService.ACTION);
                    }

                    @Override
                    public void failure(RetrofitError error) {
//                                UIUtil.showThost(LoginActivity.this, error.getMessage());
                        RetrofitErrorUtil.handleError(LoginActivity.this, error, new RetrofitErrorUtil.IErrorCallback() {
                            @Override
                            public void onError(int error_type) {
                                switch (error_type) {
                                    case RetrofitErrorUtil.IERROR_TYPE.ERROR_NORMAL:
                                        UIUtil.showThost(LoginActivity.this, "无效账户/无效密码");
                                        break;
                                }
                            }
                        });

                    }
                });
            }else {
                UIUtil.showThost(LoginActivity.this, "密码账号不能为空");
            }
        }
    };

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showFailureToast() {
        Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
    }

    private void showSuccessToast() {
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
    }

    public void postSignup(final String oauth_uid,
                           final String oauth_token,
                           String nickname,
                           String avatar_url) {

        KKMHApp.getRestClient().postSignup("weibo",
                oauth_uid,
                oauth_token,
                nickname,
                avatar_url, new Callback<SignUserInfo>() {
                    @Override
                    public void success(SignUserInfo userInfo, Response response) {
                        progressDialog.dismiss();
                        if(RetrofitErrorUtil.handleResponse(LoginActivity.this, response)){
                            return;
                        }
                        showSuccessToast();
                        for (retrofit.client.Header header : response.getHeaders()) {
                            if (null != header.getName() && header.getName().equals("Set-Cookie")) {
                                PreferencesStorageUtil.writeKKCookie(LoginActivity.this, header.getValue());
                                KKMHApp.refreshRestClient(oauth_token, header.getValue());
                                break;
                            }
                        }
                        PreferencesStorageUtil.writeWeiboAccessTokenAndUserInfo(LoginActivity.this, oauth_uid, oauth_token, userInfo);
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        RetrofitErrorUtil.handleError(LoginActivity.this, error, new RetrofitErrorUtil.IErrorCallback() {
                            @Override
                            public void onError(int error_type) {

                            }
                        });
                    }
                });
    }


}

