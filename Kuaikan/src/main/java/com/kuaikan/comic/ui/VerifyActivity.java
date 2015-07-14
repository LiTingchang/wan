package com.kuaikan.comic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.API.EmptyDataResponse;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.UIUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class VerifyActivity extends BaseActivity {

    private static final int REFRESH_TIME_SECEND = 1001;

    private static final int TIME_TITLE_SECOND = 60;

    @InjectView(R.id.activity_verify_next)
    Button mNextBtn;
    @InjectView(R.id.activity_verify_tel)
    EditText mVerifyCode;
    @InjectView(R.id.activity_verify_resend_btn)
    Button mResendBtn;
    @InjectView(R.id.activity_verify_notice)
    TextView mVerifyNoticeTxt;

    private String mPhoneNum;
    private int mActionType;

    private int mCurrentSecondLeft = TIME_TITLE_SECOND;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(VerifyActivity.class.getSimpleName());
        setContentView(R.layout.activity_verify);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_nav_delete);
        TextView title = (TextView)getActionBarToolbar().findViewById(R.id.toolbar_center_title);
        title.setText("填写验证码");

        ButterKnife.inject(this);

        startCountDown();
        mNextBtn.setOnClickListener(nextOnClickListener);
        mResendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResendBtn.setEnabled(false);
                startCountDown();
            }
        });
        initIntent();

    }

    private void initIntent(){
        Intent intent = getIntent();
        if(intent != null){
            mPhoneNum = intent.getStringExtra(RegisterActivity.REGISTER_VERIFY_PHONE);
            mActionType = intent.getIntExtra(RegisterActivity.REGISTER_TYPE, RegisterActivity.ACTION_REGISTER);
            StringBuilder noticeSb = new StringBuilder();
            noticeSb.append("已发送验证码短信至" + mPhoneNum + ",请稍后");
            mVerifyNoticeTxt.setText(noticeSb.toString());
        }
    }

    private View.OnClickListener nextOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String verifyCode = mVerifyCode.getText().toString();
            if(!TextUtils.isEmpty(mPhoneNum) && !TextUtils.isEmpty(verifyCode)){
                KKMHApp.getRestClient().phoneVerify(mPhoneNum, verifyCode, new Callback<EmptyDataResponse>() {
                    @Override
                    public void success(final EmptyDataResponse emptyDataResponse, Response response) {
                        if(RetrofitErrorUtil.handleResponse(VerifyActivity.this, response)){
                            return;
                        }
                        if(mActionType == RegisterActivity.ACTION_REGISTER){
                            Intent intent = new Intent(VerifyActivity.this, SettingPwdActivity.class);
                            intent.putExtra(RegisterActivity.REGISTER_VERIFY_PHONE,mPhoneNum);
                            VerifyActivity.this.startActivity(intent);
                        }else{
                            Intent intent = new Intent(VerifyActivity.this, ForgetFwdActivity.class);
                            VerifyActivity.this.startActivity(intent);
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        UIUtil.showThost(VerifyActivity.this, error.getMessage());
                    }
                });
            }else{
                UIUtil.showThost(VerifyActivity.this, "请输入验证码");
            }
        }
    };

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REFRESH_TIME_SECEND:
                    if(mCurrentSecondLeft == 0){
                        mResendBtn.setEnabled(true);
                        mResendBtn.setText("重新发送");
                    }else{
                        minusSecond();
                        sendEmptyMessageDelayed(REFRESH_TIME_SECEND, 1000);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void startCountDown(){
        mResendBtn.setEnabled(false);
        mCurrentSecondLeft = TIME_TITLE_SECOND;
        mHandler.sendEmptyMessageDelayed(REFRESH_TIME_SECEND, 1000);
    }

    private void minusSecond(){
        if(mCurrentSecondLeft > 0){
            mCurrentSecondLeft --;
            mResendBtn.setText(mCurrentSecondLeft + "秒后重新获取");
        }
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
