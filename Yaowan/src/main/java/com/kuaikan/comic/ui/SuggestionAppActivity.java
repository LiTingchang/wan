package com.kuaikan.comic.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.API.FeedbackResponse;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by skyfishjy on 1/6/15.
 */
public class SuggestionAppActivity extends BaseActivity {

    @InjectView(R.id.suggestion_content_edittext)
    EditText contentET;

    @InjectView(R.id.suggestion_contact_edittext)
    EditText contactET;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_app);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_up_indicator);
        TextView title = (TextView)getActionBarToolbar().findViewById(R.id.toolbar_center_title);
        title.setText("意见反馈");
        ButterKnife.inject(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在登录");
        progressDialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.suggestion, menu);
        return true;
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
            case R.id.action_suggestion_commit:
                commitSuggestion();
        }
        return super.onOptionsItemSelected(item);
    }

    private void commitSuggestion() {
        String content = contentET.getText().toString();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        String version = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "先写个反馈嘛", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            KKMHApp.getRestClient().sendFeedback(content,
                    android.os.Build.MANUFACTURER + "-" + android.os.Build.PRODUCT,
                    "" + android.os.Build.VERSION.SDK_INT,
                    screenWidth + "-" + screenHeight,
                    version, contactET.getText().toString(),
                    new Callback<FeedbackResponse>() {
                        @Override
                        public void success(FeedbackResponse feedbackResponse, Response response) {
                            progressDialog.dismiss();
                            Toast.makeText(SuggestionAppActivity.this, "谢谢反馈", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            progressDialog.dismiss();
                            RetrofitErrorUtil.handleError(SuggestionAppActivity.this, error);
                            Toast.makeText(SuggestionAppActivity.this, "谢谢", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
