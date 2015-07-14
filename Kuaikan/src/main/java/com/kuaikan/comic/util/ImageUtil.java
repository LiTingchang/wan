package com.kuaikan.comic.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;

/**
 * Created by a on 2015/4/17.
 */
public class ImageUtil {

    private static final String KK_IMAGE_CACHE = "/images";


    public static String getImageSDPath(Context context, String url){
        if(FileUtil.isSDCardMounted()){
            RequestCreator rc = Picasso.with(context)
                    .load(url).centerCrop().resize(90,90);
            String filePath = getImageCachePath() + System.currentTimeMillis();
            try {
                FileUtil.saveBitmapAsFile(filePath, rc.get());
                return filePath;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";

    }

    public static String getImageSDPath(Context context, Bitmap bitmap){
        if(FileUtil.isSDCardMounted()){
//            RequestCreator rc = Picasso.with(context)
//                    .load(url).centerCrop().resize(100,100);
            String filePath = getImageCachePath() + System.currentTimeMillis();
            FileUtil.saveBitmapAsFile(filePath, bitmap);
            return filePath;
        }
        return "";

    }

    public static Bitmap getImageBitmap(Context context,String url){
        if(!TextUtils.isEmpty(url)){
            RequestCreator rc = Picasso.with(context)
                    .load(url).centerCrop().resize(200,200);
            try {
                return rc.get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable)
    {
        int width = drawable.getIntrinsicWidth();// 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;// 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);// 把drawable内容画到画布中
        return bitmap;
    }

    public static String getImageCachePath(){
        return FileUtil.SDPATH + KK_IMAGE_CACHE;
    }

}
