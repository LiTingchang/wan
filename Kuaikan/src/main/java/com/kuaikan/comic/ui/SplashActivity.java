package com.kuaikan.comic.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.API.ConfigResponse;
import com.kuaikan.comic.rest.model.API.FavUnreadResponse;
import com.kuaikan.comic.service.PollingService;
import com.kuaikan.comic.util.NetWorkUtil;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.UIUtil;
import com.umeng.analytics.MobclickAgent;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SplashActivity extends Activity {

    Intent intent;
    long waitSeconds = 1200L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.openActivityDurationTrack(false);
        PreferencesStorageUtil.setLocalPushLastLogoutTime(this, System.currentTimeMillis());

        if(!NetWorkUtil.isNetworkAvailable(SplashActivity.this)){
            UIUtil.showThost(SplashActivity.this, R.string.error_network);
        }

        if (!PreferencesStorageUtil.isNewUser(SplashActivity.this)) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, GuideActivity.class);
        }
        PreferencesStorageUtil.setOldUser(SplashActivity.this);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, waitSeconds);

        initConfig();
        initUnRead();
    }

    public void initConfig(){
        KKMHApp.getRestClient().getConfig(new Callback<ConfigResponse>() {
            @Override
            public void success(ConfigResponse configResponse, Response response) {
                if(configResponse != null){
                    KKMHApp.getRestClient().getConfig(new Callback<ConfigResponse>() {
                        @Override
                        public void success(ConfigResponse configResponse, Response response) {
                            if(configResponse != null){
                                int interval = configResponse.getTimeline_polling_interval();
                                PreferencesStorageUtil.setLocalPushPollingInterval(SplashActivity.this, interval);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            RetrofitErrorUtil.handleError(SplashActivity.this, error);
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    public void initUnRead(){
        KKMHApp.getRestClient().getFavTimelineUnread(new Callback<FavUnreadResponse>() {
            @Override
            public void success(FavUnreadResponse favUnreadResponse, Response response) {
//                dayRecomComicListAdapter.refresh(bannerResponse.getBannerGroup());
                if(favUnreadResponse != null ){
                    PollingService.setsAttentionUnReadCount(favUnreadResponse.getUnreadCount());
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
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
}
