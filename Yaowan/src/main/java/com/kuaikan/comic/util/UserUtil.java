package com.kuaikan.comic.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.rest.model.SignUserInfo;
import com.kuaikan.comic.ui.LoginActivity;
import com.kuaikan.comic.Share.Oauth2AccessToken;

/**
 * Created by a on 2015/4/2.
 */
public class UserUtil {

    public static boolean isUserLogin(Context context){
        SignUserInfo signUser = PreferencesStorageUtil.readSignUserInfo(context);
        Oauth2AccessToken accessToken = PreferencesStorageUtil.readWeiboAccessToken(context);
        if ((signUser != null && !TextUtils.isEmpty(signUser.getId())) || (accessToken != null && accessToken.isSessionValid())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkUserLogin(Context context){
        if(!isUserLogin(context)){
            Intent intent = new Intent();
            intent.setClass(context, LoginActivity.class);
            context.startActivity(intent);
            return false;
        }
        return true;
    }

    public static void logout(Context context){
        if(context != null){
            PreferencesStorageUtil.clearAccessToken(context);
            PreferencesStorageUtil.clearSignUser(context);
            KKMHApp.refreshRestClient(null, null);
        }
    }
}
