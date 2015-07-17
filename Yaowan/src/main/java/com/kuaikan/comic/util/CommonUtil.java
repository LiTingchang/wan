package com.kuaikan.comic.util;

import android.content.Context;
import android.provider.Settings.Secure;

/**
 * Created by skyfishjy on 2/13/15.
 */
public class CommonUtil {

    public static String getDeviceUuid(Context context) {
        final String androidId = Secure.getString(
                context.getContentResolver(), Secure.ANDROID_ID);
        return "A:" + androidId;
    }
}
