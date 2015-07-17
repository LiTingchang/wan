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

public class RegisterActivity extends BaseActivity {

    public static final String REGISTER_VERIFY_PHONE = "verify_phone";

    public static final int ACTION_REGISTER = 1001;
    public static final int ACTION_RESET = 1002;
    public static final String REGISTER_TITLE = "register_title";
    public static final String REGISTER_TYPE = "register_type";
    private int mActionType = ACTION_REGISTER;

    @InjectView(R.id.activity_register_tel)
    EditText mTelEditText;
    @InjectView(R.id.activity_register_get_verify)
    Button mGetVerifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(RegisterActivity.class.getSimpleName());

        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_nav_delete);
        TextView title = (TextView)getActionBarToolbar().findViewById(R.id.toolbar_center_title);
        title.setText("注册");

        Intent intent = getIntent();
        if(intent != null){
            String titleStr = intent.getStringExtra(REGISTER_TITLE);
            if(!TextUtils.isEmpty(titleStr)){
                title.setText(titleStr);
            }
            mActionType = intent.getIntExtra(REGISTER_TYPE, ACTION_REGISTER);

        }

        ButterKnife.inject(this);



        mGetVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tel = mTelEditText.getText().toString();
                if(!TextUtils.isEmpty(tel)){
                    getVerifyCode(tel);
                }else{
                    Toast.makeText(RegisterActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getVerifyCode(final String phone){
        String reason = "register";
        if(mActionType == ACTION_REGISTER){
            reason = "register";
        }else{
            reason = "reset_password";
        }
        KKMHApp.getRestClient().sendCode(phone, reason, new Callback<EmptyDataResponse>() {
            @Override
            public void success(final EmptyDataResponse emptyDataResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(RegisterActivity.this, response)){
                    return;
                }
                for (retrofit.client.Header header : response.getHeaders()) {
                    if (null != header.getName() && header.getName().equals("Set-Cookie")) {
                        PreferencesStorageUtil.writeKKCookie(RegisterActivity.this, header.getValue());
                        KKMHApp.refreshRestClient("", header.getValue());
                        break;
                    }
                }
                switch (mActionType){
                    case ACTION_REGISTER:
                        Intent verifyIntent = new Intent(RegisterActivity.this, VerifyActivity.class);
                        verifyIntent.putExtra(REGISTER_VERIFY_PHONE, phone);
                        RegisterActivity.this.startActivity(verifyIntent);
                        break;
                    case ACTION_RESET:
                        Intent forgetPwdIntent = new Intent(RegisterActivity.this, VerifyActivity.class);
                        forgetPwdIntent.putExtra(REGISTER_VERIFY_PHONE, phone);
                        forgetPwdIntent.putExtra(REGISTER_TYPE, ACTION_RESET);
                        RegisterActivity.this.startActivity(forgetPwdIntent);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                UIUtil.showThost(RegisterActivity.this, "loginActivity-->error");
                RetrofitErrorUtil.handleError(RegisterActivity.this, error);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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


}
