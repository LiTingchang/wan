package com.kuaikan.comic.ui;

import android.content.Intent;
import android.os.Bundle;
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

public class ForgetFwdActivity extends BaseActivity {

    @InjectView(R.id.activity_forget_pwd_et)
    EditText mPwdEdt;
    @InjectView(R.id.activity_forget_finish)
    Button mFinishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.tag(RegisterActivity.class.getSimpleName());

        setContentView(R.layout.activity_forget_fwd);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_nav_delete);
        TextView title = (TextView)getActionBarToolbar().findViewById(R.id.toolbar_center_title);
        title.setText("重置密码");

        ButterKnife.inject(this);

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPwd = mPwdEdt.getText().toString();
                if(!TextUtils.isEmpty(newPwd)){
                    resetPwd(newPwd);
                }
            }
        });
    }

    private void resetPwd( String pwd){
        if(pwd.length() < 8){
            UIUtil.showThost(ForgetFwdActivity.this, "密码长度至少为8位");
            return;
        }
        if(pwd.length() > 30){
            UIUtil.showThost(ForgetFwdActivity.this, "密码长度不能超过30位");
            return;
        }

        KKMHApp.getRestClient().resetPwd(pwd, new Callback<EmptyDataResponse>() {
            @Override
            public void success(final EmptyDataResponse emptyDataResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(ForgetFwdActivity.this, response)){
                    return;
                }
                Intent intent = new Intent(ForgetFwdActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ForgetFwdActivity.this.startActivity(intent);
            }

            @Override
            public void failure(RetrofitError error) {
                RetrofitErrorUtil.handleError(ForgetFwdActivity.this, error);
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
