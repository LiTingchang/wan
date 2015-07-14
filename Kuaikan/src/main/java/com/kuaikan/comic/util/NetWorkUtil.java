package com.kuaikan.comic.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by a on 2015/3/26.
 */
public class NetWorkUtil {


    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager == null){
            return false;
        }
        NetworkInfo info=manager.getActiveNetworkInfo();
        if(info==null||!info.isAvailable()){
            return false;
        }
        return true;
    }

    public static boolean isInWifiNetWork(Context context){
        ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=manager.getActiveNetworkInfo();
        if(info==null||!info.isAvailable()){
            return false;
        }else{
            if(info.getType() == ConnectivityManager.TYPE_WIFI){
                return true;
            }else{
                return false;
            }
        }
    }


}
