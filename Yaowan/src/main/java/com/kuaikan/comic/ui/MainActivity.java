package com.kuaikan.comic.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.API.VersionResponse;
import com.kuaikan.comic.service.PollingService;
import com.kuaikan.comic.ui.fragment.MainTabKuaikanFragment;
import com.kuaikan.comic.ui.fragment.MainTabProfile2Fragment;
import com.kuaikan.comic.ui.fragment.MainTabTopicFragment;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.kuaikan.comic.util.ServiceUtils;
import com.kuaikan.comic.util.UIUtil;
import com.kuaikan.comic.util.UserUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends BaseActivity implements View.OnClickListener{

    private RadioGroup tabHost;
    private MainTabTopicFragment tabTopicFragment;
    private MainTabKuaikanFragment mainTabKuaikanFragment;
    private MainTabProfile2Fragment profileTabFragment;
//    private RadioGroup mFeedTabHost;
    private FrameLayout mAttentionFeedLayout;
    private FrameLayout mRecommendFeedLayout;
    private View mAttentionIndicator;
    private View mRecommendIndicator;
    private View mAttentionNotice;
    private UnReadReceiveBroadCast receiveBroadCast;
    public static final String UNREAD_ACTION = "com.kuaikan.comic.UNREADCOUNT";

    private int currentTabID;
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            FragmentTransaction fragmentTransaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
            getCurrentFragment().onPause();
            Fragment fragment = fragmentMap.get(checkedId);
            if (fragment.isAdded()) {
                fragment.onResume();
            } else {
                fragmentTransaction.add(R.id.content_main, fragment);
            }
            for (Map.Entry entry : fragmentMap.entrySet()) {
                Fragment iteratorFragment = (Fragment) entry.getValue();
                if (entry.getKey().equals(checkedId)) {
                    fragmentTransaction.show(iteratorFragment);
                } else {
                    fragmentTransaction.hide(iteratorFragment);
                }
            }
            fragmentTransaction.commit();
            currentTabID = checkedId;
            switch (currentTabID) {
                case R.id.tab_kuaikan:
                    showActionbarLogo();
                    break;
                case R.id.tab_topic:
                    showTopicTab();
                    break;
                case R.id.tab_profile:
                    showProfileTab();
                    break;
            }
        }
    };

    private View.OnClickListener moreButtonOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
//            if(UserUtil.isUserLogin(MainActivity.this)){
                Intent intent = new Intent(MainActivity.this, MoreActivity.class);
                startActivity(intent);
//            }else{
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//            }

        }
    };

    private Map<Integer, Fragment> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showActionbarLogo();
        tabHost = (RadioGroup) findViewById(R.id.tabhost);

        mainTabKuaikanFragment = MainTabKuaikanFragment.newInstance();
        profileTabFragment = MainTabProfile2Fragment.newInstance();
        tabTopicFragment = MainTabTopicFragment.newInstance();
//        moreTabFragment = MoreFragment.newInstance();
        fragmentMap.put(R.id.tab_kuaikan, mainTabKuaikanFragment);
        fragmentMap.put(R.id.tab_topic, tabTopicFragment);
        fragmentMap.put(R.id.tab_profile, profileTabFragment);
//        fragmentMap.put(R.id.tab_more, moreTabFragment);

        currentTabID = R.id.tab_kuaikan;
        tabHost.setOnCheckedChangeListener(onCheckedChangeListener);
        ((RadioButton) findViewById(R.id.tab_kuaikan)).setChecked(true);
        checkUpdate();
        if(UserUtil.isUserLogin(MainActivity.this)) {
            ServiceUtils.startLocalPushService(this, PreferencesStorageUtil.getLocalPushPollingInterval(MainActivity.this), PollingService.class, PollingService.ACTION);
        }
//        ServiceUtils.stopLocalPushService(this, LocalPushService.class, LocalPushService.ACTION);

        mAttentionFeedLayout = (FrameLayout)getActionBarToolbar().findViewById(R.id.feed_attention_layout);
        mRecommendFeedLayout = (FrameLayout)getActionBarToolbar().findViewById(R.id.feed_recommend_layout);
        mAttentionIndicator = getActionBarToolbar().findViewById(R.id.feed_attention_indicator);
        mRecommendIndicator = getActionBarToolbar().findViewById(R.id.feed_recommend_indicator);
        mAttentionFeedLayout.setOnClickListener(this);
        mRecommendFeedLayout.setOnClickListener(this);
        mAttentionFeedLayout.setSelected(true);
        mAttentionNotice = getActionBarToolbar().findViewById(R.id.feed_attention_notice);
        if(!PreferencesStorageUtil.isShowDot(MainActivity.this)){
            mAttentionNotice.setVisibility(View.VISIBLE);
            PreferencesStorageUtil.setShowDot(MainActivity.this);
        }
        mainTabKuaikanFragment.setKuaiKanFeedPageChangeListener(new MainTabKuaikanFragment.KuaiKanFeedPageChangeListener() {
            @Override
            public void OnPageChange(int position) {

                if(position == 0){
                    mAttentionFeedLayout.setSelected(true);
                    mRecommendFeedLayout.setSelected(false);
                    mAttentionIndicator.setVisibility(View.VISIBLE);
                    mRecommendIndicator.setVisibility(View.GONE);
                    if(mAttentionNotice.getVisibility() == View.VISIBLE){
                        mAttentionNotice.setVisibility(View.INVISIBLE);
                    }
                }else{
                    mAttentionFeedLayout.setSelected(false);
                    mRecommendFeedLayout.setSelected(true);
                    mAttentionIndicator.setVisibility(View.GONE);
                    mRecommendIndicator.setVisibility(View.VISIBLE);
                }
            }
        });
//        mFeedTabHost.setOnCheckedChangeListener(mFeedTabOnCheckedChangeListener);

        receiveBroadCast = new UnReadReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UNREAD_ACTION);    //只有持有相同的action的接受者才能接收此广播
        registerReceiver(receiveBroadCast, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.feed_attention_layout:
                if(mainTabKuaikanFragment != null){
                    mAttentionFeedLayout.setSelected(true);
                    mRecommendFeedLayout.setSelected(false);
                    mainTabKuaikanFragment.changeViewPager(0);
                    mAttentionIndicator.setVisibility(View.VISIBLE);
                    mRecommendIndicator.setVisibility(View.GONE);
                }
                break;
            case R.id.feed_recommend_layout:
                if(mainTabKuaikanFragment != null){
                    mAttentionFeedLayout.setSelected(false);
                    mRecommendFeedLayout.setSelected(true);
                    mainTabKuaikanFragment.changeViewPager(1);
                    mAttentionIndicator.setVisibility(View.GONE);
                    mRecommendIndicator.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        KKMHApp.setTitleBarHeight(getActionBarToolbar().getHeight());
        KKMHApp.setToolBarHeight(tabHost.getHeight());
        KKMHApp.setContentHeight(findViewById(R.id.content_main).getHeight());
        super.onWindowFocusChanged(hasFocus);
    }

    public Fragment getCurrentFragment() {
        return fragmentMap.get(currentTabID);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        int unReadCount = PollingService.getsAttentionUnReadCount();
        if(unReadCount > 0){
            if(mAttentionNotice != null){
                mAttentionNotice.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        PreferencesStorageUtil.setLocalPushLastLogoutTime(this, System.currentTimeMillis());
        unregisterReceiver(receiveBroadCast);
//        ServiceUtils.startLocalPushService(this, LocalPushService.class, LocalPushService.ACTION);
        super.onDestroy();
    }


    public class UnReadReceiveBroadCast extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            //得到广播中得到的数据，并显示出来
            if(intent != null){
                if (intent.getAction().equals(UNREAD_ACTION)){
                    int unReadCount = intent.getIntExtra(PollingService.UNREAD_COUNT_FLAG, 0);
                    if(unReadCount > 0){
                        if(mAttentionNotice != null){
                            mAttentionNotice.setVisibility(View.VISIBLE);
                        }
                        if(mainTabKuaikanFragment != null){
                            mainTabKuaikanFragment.setmHasUnreadMsg(true);
                        }
                    }else{
                        if(mAttentionNotice != null){
                            mAttentionNotice.setVisibility(View.INVISIBLE);
                        }
                        if(mainTabKuaikanFragment != null){
                            mainTabKuaikanFragment.setmHasUnreadMsg(false);
                        }
                    }
                }

            }
        }

    }

    private void checkUpdate(){
        KKMHApp.getRestClient().checkUpdate(new Callback<VersionResponse>() {
            @Override
            public void success(VersionResponse versionResponse, Response response) {
                if(versionResponse != null && !TextUtils.isEmpty(versionResponse.getVersion())){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(versionResponse.getDesc());
                builder.setTitle("新版提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UIUtil.startMarket(MainActivity.this);
                        MainActivity.this.finish();
                        }
                    });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }});
                builder.create().show();
                }
            }
            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void setActionBarCenterTitle(String title) {

        getActionBarToolbar().setBackgroundColor(Color.WHITE);
        getActionBarToolbar().findViewById(R.id.feed_tabhost).setVisibility(View.GONE);
        getActionBarToolbar().findViewById(R.id.feed_tabhost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainTabKuaikanFragment != null){
                    mainTabKuaikanFragment.scrollToFirst();
                }
            }
        });
        getSupportActionBar().setTitle("");
        TextView titleTV = (TextView) getActionBarToolbar().findViewById(R.id.toolbar_center_title);
        titleTV.setVisibility(View.VISIBLE);
        titleTV.setText(title);
    }

    private void showTopicTab() {
        setActionBarCenterTitle("发现");
        getActionBarToolbar().findViewById(R.id.toolbar_right_menu).setVisibility(View.GONE);
        TextView searchBtn = (TextView) findViewById(R.id.main_topic_search);
        searchBtn.setVisibility(View.VISIBLE);
        searchBtn.setBackgroundResource(R.drawable.ic_works_nav_search);
        searchBtn.setOnClickListener(searchOnClickListener);
    }

    private void showProfileTab(){
        setActionBarCenterTitle("我");
        getActionBarToolbar().findViewById(R.id.main_topic_search).setVisibility(View.GONE);
        TextView moreBtn = (TextView) findViewById(R.id.toolbar_right_menu);
        moreBtn.setVisibility(View.VISIBLE);
        moreBtn.setOnClickListener(moreButtonOnClickListener);
    }

    private void showActionbarLogo() {
        getActionBarToolbar().setBackgroundColor(getResources().getColor(R.color.color_K));
        getActionBarToolbar().findViewById(R.id.feed_tabhost).setVisibility(View.VISIBLE);
        getActionBarToolbar().findViewById(R.id.feed_tabhost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainTabKuaikanFragment != null){
                    mainTabKuaikanFragment.scrollToFirst();
                }
            }
        });
        getSupportActionBar().setTitle("");
        TextView titleTV = (TextView) getActionBarToolbar().findViewById(R.id.toolbar_center_title);
        titleTV.setVisibility(View.GONE);
        getActionBarToolbar().findViewById(R.id.toolbar_right_menu).setVisibility(View.GONE);
        getActionBarToolbar().findViewById(R.id.main_topic_search).setVisibility(View.VISIBLE);
        getActionBarToolbar().findViewById(R.id.main_topic_search).setBackgroundResource(R.drawable.ic_home_nav_search);
        getActionBarToolbar().findViewById(R.id.main_topic_search).setOnClickListener(searchOnClickListener);
    }

    private View.OnClickListener searchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SearchActivity.class);
            MainActivity.this.startActivity(intent);
        }
    };
}
