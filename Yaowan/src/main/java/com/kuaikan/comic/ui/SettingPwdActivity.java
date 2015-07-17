package com.kuaikan.comic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.API.EmptyDataResponse;
import com.kuaikan.comic.rest.model.SignUserInfo;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.UIUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class SettingPwdActivity extends BaseActivity {

    @InjectView(R.id.activity_setting_pwd_account)
    EditText mAccountEdt;
    @InjectView(R.id.activity_setting_pwd_pwd)
    EditText mPwdEdt;
    @InjectView(R.id.activity_setting_pwd_finish)
    Button mFinishBtn;

    private String mPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(SettingPwdActivity.class.getSimpleName());

        setContentView(R.layout.activity_setting_pwd);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_nav_delete);
        TextView title = (TextView)getActionBarToolbar().findViewById(R.id.toolbar_center_title);
        title.setText("设置密码");

        ButterKnife.inject(this);

        mFinishBtn.setOnClickListener(finishOnClickListener);
        initIntent();

    }

    private void initIntent(){
        Intent intent = getIntent();
        if(intent != null){
            mPhoneNum = intent.getStringExtra(RegisterActivity.REGISTER_VERIFY_PHONE);
        }
    }

    private View.OnClickListener finishOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String account = mAccountEdt.getText().toString();
            final String pwd = mPwdEdt.getText().toString();
            if((!TextUtils.isEmpty(account) && account.length() >= 3 && account.length() <= 50) && (!TextUtils.isEmpty(pwd) && pwd.length() >= 8 && pwd.length() <= 30)){
                KKMHApp.getRestClient().phoneSignup(account, pwd, new Callback<EmptyDataResponse>() {
                    @Override
                    public void success(final EmptyDataResponse emptyDataResponse, Response response) {
                        if(RetrofitErrorUtil.handleResponse(SettingPwdActivity.this, response)){
                            return;
                        }
                        KKMHApp.getRestClient().phoneSignin(mPhoneNum, pwd, new Callback<SignUserInfo>() {
                            @Override
                            public void success(final SignUserInfo userInfo, Response response) {
                                if(RetrofitErrorUtil.handleResponse(SettingPwdActivity.this, response)){
                                    return;
                                }
                                for (retrofit.client.Header header : response.getHeaders()) {
                                    if (null != header.getName() && header.getName().equals("Set-Cookie")) {
                                        PreferencesStorageUtil.writeKKCookie(SettingPwdActivity.this, header.getValue());
                                        KKMHApp.refreshRestClient("", header.getValue());
                                        break;
                                    }
                                }
                                showSuccessToast();
                                PreferencesStorageUtil.writeUserUserInfo(SettingPwdActivity.this, userInfo);
                                finish();
                                Intent intent = new Intent(SettingPwdActivity.this, MainActivity.class);
                                SettingPwdActivity.this.startActivity(intent);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                UIUtil.showThost(SettingPwdActivity.this, error.getMessage());
                            }
                        });

                        Intent intent = new Intent(SettingPwdActivity.this, MainActivity.class);
                        SettingPwdActivity.this.startActivity(intent);
                    }

                    @Override
                    public void failure(RetrofitError error) {
//                        UIUtil.showThost(SettingPwdActivity.this, error.getMessage());
                        //                                UIUtil.showThost(LoginActivity.this, error.getMessage());
                        RetrofitErrorUtil.handleError(SettingPwdActivity.this, error);
                    }
                });
            }else{
                if(TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)){
                    UIUtil.showThost(SettingPwdActivity.this, "请输入用户名或密码");
                }else{

                    if(pwd.length() < 8){
                        UIUtil.showThost(SettingPwdActivity.this, "密码长度至少为8位");
                    }
                    if(pwd.length() > 30){
                        UIUtil.showThost(SettingPwdActivity.this, "密码长度不能超过30位");
                    }
                    if(account.length() < 3){
                        UIUtil.showThost(SettingPwdActivity.this, "昵称至少为3个字符");
                    }
                    if(account.length() > 50){
                        UIUtil.showThost(SettingPwdActivity.this, "昵称长度不能超过50个字");
                    }
                }


            }
        }
    };

    private void showSuccessToast() {
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
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

}
