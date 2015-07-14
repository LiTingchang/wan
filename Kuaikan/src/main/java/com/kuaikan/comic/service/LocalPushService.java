package com.kuaikan.comic.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.kuaikan.comic.R;
import com.kuaikan.comic.ui.MainActivity;
import com.kuaikan.comic.util.PreferencesStorageUtil;

import java.util.Timer;

/**
 * Created by liuchao1 on 15/6/3.
 */
public class LocalPushService extends Service {

    public static final String ACTION = "com.kuaikan.comic.LocalPushService";
    public static final String LOCAL_PUSH_TITLE = "local_push_title";
    public static final String LOCAL_PUSH_CONTENT = "local_push_content";
    public static final int POLLING_CALLBACK_TIME_INTERVAL = 3600000;//60 * 60 * 1000;
    private static final long TIME_INTERVAL = 604800000l;//7 * 24 * 60 * 60 * 1000;
    private static final String NOTIFY_MESSAGE = "好多天都没来快看了,······-_-#你喜欢的漫画又更新了很多哦~^_^";
    private Notification mNotification;
    private NotificationManager mManager;
    static Timer timer = null;
    private PendingIntent mPendingIntent;
    private AlarmManager mAlarmManager;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        initNotifiManager();
    }

    //初始化通知栏配置
    private void initNotifiManager() {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotification = new Notification();
        mNotification.icon = R.drawable.ic_launcher;
        mNotification.tickerText = NOTIFY_MESSAGE;
        mNotification.defaults |= Notification.DEFAULT_SOUND;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
    }

    public int onStartCommand(final Intent intent, int flags, int startId) {

        long lastLogoutTime = PreferencesStorageUtil.getLocalPushLastLogoutTime(this);
        long currentTime = System.currentTimeMillis();

        if(mNotification != null && mManager != null){
            if(currentTime - lastLogoutTime > TIME_INTERVAL){
                mNotification.when = System.currentTimeMillis();
                Intent i = new Intent(this,MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);//Intent.FLAG_ACTIVITY_NEW_TASK);
                mNotification.setLatestEventInfo(this,
                        getResources().getString(R.string.app_name), NOTIFY_MESSAGE, pendingIntent);
                mManager.notify(0, mNotification);
                PreferencesStorageUtil.setLocalPushLastLogoutTime(this, currentTime);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}