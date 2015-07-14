package com.kuaikan.comic.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.rest.model.API.FavUnreadResponse;
import com.kuaikan.comic.ui.MainActivity;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by liuchao1 on 15/6/5.
 */
public class PollingService extends Service {

    public static final String ACTION = "com.kuaikan.comic.PollingService";
    public static final int POLLING_TIME_INTERVAL = 300;
    private static int sAttentionUnReadCount = 0;
    public static final String UNREAD_COUNT_FLAG = "unread_count";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {}

    public int onStartCommand(final Intent intent, int flags, int startId) {
        KKMHApp.getRestClient().getFavTimelineUnread(new Callback<FavUnreadResponse>() {
            @Override
            public void success(FavUnreadResponse favUnreadResponse, Response response) {
                if(favUnreadResponse != null ){
                    setsAttentionUnReadCount(favUnreadResponse.getUnreadCount());
                    Intent unReadIntent = new Intent(MainActivity.UNREAD_ACTION);
                    unReadIntent.putExtra(UNREAD_COUNT_FLAG, favUnreadResponse.getUnreadCount());
                    sendBroadcast(unReadIntent);
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public static int getsAttentionUnReadCount() {
        return sAttentionUnReadCount;
    }

    public static void setsAttentionUnReadCount(int attentionUnReadCount) {
        sAttentionUnReadCount = attentionUnReadCount;
    }
}