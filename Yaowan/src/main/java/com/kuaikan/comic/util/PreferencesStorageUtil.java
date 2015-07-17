package com.kuaikan.comic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.Share.Oauth2AccessToken;
import com.kuaikan.comic.rest.model.SignUserInfo;
import com.kuaikan.comic.service.PollingService;

/**
 * Created by skyfishjy on 1/5/15.
 */
public class PreferencesStorageUtil {
    private static final String PREFERENCES_NAME = "com_kuaikan_comic_android";

    private static final String WEIBO_KEY_UID = "weibo_uid";
    private static final String WEIBO_KEY_ACCESS_TOKEN = "weibo_access_token";
    private static final String WEIBO_KEY_EXPIRES_IN = "weibo_expires_in";
    private static final String WEIBO_USER_AVATAR_URL = "weibo_user_avatar_url";
    private static final String WEIBO_USER_NICKNAME = "weibo_user_nickname";
    private static final String COOKIE = "cookie";
    private static final String IS_RATING_DIALOG_SHOW = "rating_dialog_show";
    public static final String LOCAL_PUSH_LAST_LOGOUT_TIME = "last_logout_time";
    private static final String LOCAL_PUSH_POLLING_INTERVAL = "polling_interval";
    private static final String LOCAL_PUSH_UNREAD = "unread";
    private static final String SHOW_DOT_BEFORE = "show_dot";

    private static final String IS_NEW_USER = "is_new_user" + KKMHApp.mVersionCode;


    public static void writeWeiboAccessTokenAndUserInfo(Context context,String uid, String token, SignUserInfo userInfo) {
        if (null == context || null == token || null == userInfo) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putString(WEIBO_KEY_UID, uid);
        editor.putString(WEIBO_KEY_ACCESS_TOKEN, token);
//        editor.putLong(WEIBO_KEY_EXPIRES_IN, token.getExpiresTime());
        editor.putString(WEIBO_USER_AVATAR_URL, userInfo.getAvatar_url());
        editor.putString(WEIBO_USER_NICKNAME, userInfo.getNickname());
        editor.apply();
    }

    public static void writeUserUserInfo(Context context, SignUserInfo userInfo){
        if(null == userInfo) {
            return ;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putString(WEIBO_USER_AVATAR_URL, userInfo.getAvatar_url());
        editor.putString(WEIBO_USER_NICKNAME, userInfo.getNickname());
        editor.putString(WEIBO_KEY_UID, userInfo.getId());
        editor.apply();
    }

    public static void writeUserUserInfo(Context context, String avatarUrl, String nickName){

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putString(WEIBO_USER_AVATAR_URL, avatarUrl);
        editor.putString(WEIBO_USER_NICKNAME, nickName);
        editor.apply();
    }

    public static void writeKKCookie(Context context, String cookie) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putString(COOKIE, cookie);
        editor.apply();
    }

    public static boolean isRatingDialogShow(Context context) {
        if (null == context) {
            return true;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(IS_RATING_DIALOG_SHOW, false);
    }

    public static void setRatingDialogShow(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putBoolean(IS_RATING_DIALOG_SHOW, true);
        editor.apply();
    }
//
//    public static boolean getLocalPushUnRead(Context context){
//        if(null == context){
//            System.out.println("context==null");
//            return false;
//        }
//        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
//        return pref.getBoolean(LOCAL_PUSH_UNREAD, false);
//    }
//
//    public static void setLocalPushUnRead(Context context, int unReadCount) {
//        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
//        Editor editor = pref.edit();
//        if(unReadCount > 0){
//            editor.putBoolean(LOCAL_PUSH_UNREAD, true);
//            System.out.println("LOCAL_PUSH_UNREAD--->true");
//        }else{
//            editor.putBoolean(LOCAL_PUSH_UNREAD, false);
//            System.out.println("LOCAL_PUSH_UNREAD--->false");
//        }
//        editor.apply();
//    }

    public static boolean isShowDot(Context context) {
        if (null == context) {
            return true;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(SHOW_DOT_BEFORE, false);
    }

    public static void setShowDot(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putBoolean(SHOW_DOT_BEFORE, true);
        editor.apply();
    }

    public static boolean isNewUser(Context context) {
        if (null == context) {
            return true;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(IS_NEW_USER, true);
    }

    public static void setOldUser(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putBoolean(IS_NEW_USER, false);
        editor.apply();
    }

    public static void setLocalPushPollingInterval(Context context, int pollingInterval){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putInt(LOCAL_PUSH_POLLING_INTERVAL, pollingInterval);
        editor.apply();
    }

    public static int getLocalPushPollingInterval(Context context){
        if (null == context) {
            return PollingService.POLLING_TIME_INTERVAL;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return (pref.getInt(LOCAL_PUSH_POLLING_INTERVAL, PollingService.POLLING_TIME_INTERVAL) * 1000);
    }

    public static void setLocalPushLastLogoutTime(Context context, long lastLogoutTime){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putLong(LOCAL_PUSH_LAST_LOGOUT_TIME, lastLogoutTime);
        editor.apply();
    }

    public static long getLocalPushLastLogoutTime(Context context){
        if (null == context) {
            return 0l;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return pref.getLong(LOCAL_PUSH_LAST_LOGOUT_TIME, 0l);
    }

    public static String readKKCookie(Context context) {
        if (null == context) {
            return null;
        }

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return pref.getString(COOKIE, "");
    }

    public static Oauth2AccessToken readWeiboAccessToken(Context context) {
        if (null == context) {
            return null;
        }
        Oauth2AccessToken token = new Oauth2AccessToken();
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        token.setUid(pref.getString(WEIBO_KEY_UID, ""));
        token.setToken(pref.getString(WEIBO_KEY_ACCESS_TOKEN, ""));
        token.setExpiresTime(pref.getLong(WEIBO_KEY_EXPIRES_IN, 0));
        return token;
    }

    public static SignUserInfo readSignUserInfo(Context context) {
        if (null == context) {
            return null;
        }

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        return new SignUserInfo(pref.getString(WEIBO_USER_AVATAR_URL, ""),
                pref.getString(WEIBO_KEY_UID, ""),
                pref.getString(WEIBO_USER_NICKNAME, ""));
    }

    public static void clear(Context context) {
        if (null == context) {
            return;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearAccessToken(Context context) {
        if (null == context) {
            return;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.remove(WEIBO_KEY_UID);
        editor.remove(WEIBO_KEY_ACCESS_TOKEN);
        editor.remove(WEIBO_KEY_EXPIRES_IN);
        editor.apply();
    }

    public static void clearSignUser(Context context) {
        if (null == context) {
            return;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.remove(WEIBO_USER_AVATAR_URL);
        editor.remove(COOKIE);
        editor.remove(WEIBO_USER_NICKNAME);
        editor.apply();
    }
}
