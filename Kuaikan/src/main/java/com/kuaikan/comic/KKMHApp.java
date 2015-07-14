package com.kuaikan.comic;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;

import com.crashlytics.android.Crashlytics;
import com.kuaikan.comic.dao.DaoMaster;
import com.kuaikan.comic.dao.DaoSession;
import com.kuaikan.comic.rest.PicassoProvider;
import com.kuaikan.comic.rest.RestClient;
import com.kuaikan.comic.util.CommonUtil;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.sina.weibo.sdk.BuildConfig;

import cn.jpush.android.api.JPushInterface;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by skyfishjy on 12/18/14.
 */
public class KKMHApp extends Application {

    private static KKMHApp singleton;
    private static RestClient restClient;
    private static String uuid;
    private static DaoSession daoSession;
    private static String user_agent;
    public static int mVersionCode;
    private static int mScreenWidth;
    private static int mScreenHeight;
    private static int mTitleBarHeight;
    private static int mToolBarHeight;
    private static int mContentHeight;

    public static KKMHApp getInstance() {
        return singleton;
    }

    public static RestClient getRestClient() {
        return restClient;
    }

    public static void refreshRestClient(String accessToken, String cookieSession) {
        restClient = new RestClient(user_agent, accessToken, cookieSession, uuid);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        singleton = this;
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        uuid = CommonUtil.getDeviceUuid(getApplicationContext());
        String cookie = PreferencesStorageUtil.readKKCookie(getApplicationContext());
        String accessToken = PreferencesStorageUtil.readWeiboAccessToken(getApplicationContext()).getToken();

        //获取user-agent
        StringBuilder sb = new StringBuilder();
        sb.append("Kuaikan/");
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            sb.append(pInfo.versionName);
            mVersionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        sb.append("(Android;");
        sb.append(android.os.Build.VERSION.RELEASE + ";");
        sb.append(android.os.Build.MODEL + ";");
        sb.append(getChannelName(KKMHApp.this) + ")");
        user_agent = sb.toString();
        restClient = new RestClient(user_agent, accessToken, cookie, uuid);
        PicassoProvider.setupPicassoInstance(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        mScreenWidth = this.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = this.getResources().getDisplayMetrics().heightPixels;
        setupDatabase();
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "kk_comic_db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static int getScreenWidth(){
        return mScreenWidth;
    }

    public static int getScreenHeight(){
        return mScreenHeight;
    }

    public static int getTitleBarHeight() { return mTitleBarHeight; }

    public static void setTitleBarHeight(int titleBarHeight) { mTitleBarHeight = titleBarHeight;
    }

    public static int getToolBarHeight() { return mToolBarHeight; }

    public static void setToolBarHeight(int toolBarHeight) { mToolBarHeight = toolBarHeight; }

    public static int getContentHeight() { return mContentHeight; }

    public static void setContentHeight(int contentHeight) { mContentHeight = contentHeight;}

    public static String getChannelName(Context ctx) {
        String channelName = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelName = applicationInfo.metaData.getString("UMENG_CHANNEL");
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channelName;
    }

}
