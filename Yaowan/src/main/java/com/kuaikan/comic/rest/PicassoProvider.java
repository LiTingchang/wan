package com.kuaikan.comic.rest;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.sina.weibo.sdk.BuildConfig;
import com.squareup.picasso.Cache;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import com.kuaikan.comic.BuildConfig;

/**
 * Created by skyfishjy on 12/20/14.
 */
public class PicassoProvider {

    private static final String TAG = PicassoProvider.class.getSimpleName();

    private static ExecutorService executorService;
    private static Cache memoryCache;
    private static volatile boolean picassoSetup = false;

    private static synchronized ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(3);
        }
        return executorService;
    }

    private static synchronized Cache getMemoryCache(Context context) {
        if (memoryCache == null) {
            memoryCache = new LruCache(context);
        }
        return memoryCache;
    }

    public static void cleanMemoryCache() {
        memoryCache.clear();
    }

    public static synchronized void setupPicassoInstance(Context appContext) {
        if (picassoSetup) {
            return;
        }
        Picasso picasso = new Picasso.Builder(appContext)
//                .indicatorsEnabled(BuildConfig.DEBUG)
                .loggingEnabled(BuildConfig.DEBUG)
                .downloader(new OkHttpDownloader(appContext))
                .executor(getExecutorService())
                .memoryCache(getMemoryCache(appContext))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        Log.e(TAG, "Failed to load Uri:" + uri.toString());
                        e.printStackTrace();
                    }
                })
                .build();
        Picasso.setSingletonInstance(picasso);
        picassoSetup = true;
    }

}
