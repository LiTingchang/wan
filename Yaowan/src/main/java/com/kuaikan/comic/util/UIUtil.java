package com.kuaikan.comic.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;

/**
 * Created by skyfishjy on 12/20/14.
 */
public class UIUtil {

    public static Transformation roundedTransformation = new RoundedTransformationBuilder()
            .borderWidthDp(0)
            .cornerRadiusDp(2)
            .oval(false)
            .build();

    public static Transformation avatarRoundedTransformation = new RoundedTransformationBuilder()
    .borderWidthDp(0)
    .cornerRadius(72)
    .oval(false)
    .build();

    public static String getCalculatedCount(long count){
        String calculatedCount;
        if (count < 100000) {
            calculatedCount = " " + count;
        } else {
            calculatedCount = " " + count / 10000 + "万";
        }
        return calculatedCount;
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void showKeyBoard(Context context,View view){
        ((InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, 0);
    }

//    public static void hideSystemKeyBoard(Context context,View v) {
//        InputMethodManager imm = (InputMethodManager) (context
//                .getSystemService(Context.INPUT_METHOD_SERVICE));
//        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void showThost(Context context, String toast){
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    public static void showThost(Context context, int resId){
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    //进入应用市场
    public static void startMarket(Context context){
        startMarket(context, context.getPackageName());
    }

    public static void startMarket(Context context, String packageName){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + packageName));
        try{
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
//            UIUtils.showToast(AboutActivity.this, R.string.score_error, Toast.LENGTH_SHORT);
        }
    }

}
