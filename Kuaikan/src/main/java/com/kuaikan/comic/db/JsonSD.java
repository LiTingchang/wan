package com.kuaikan.comic.db;

import com.kuaikan.comic.util.FileUtil;

/**
 * Created by a on 2015/3/26.
 * @author liuchao
 * 列表json数据保存这里，只缓存一屏，详细数据缓存数据库
 */
public class JsonSD {

    private static final String JSON_FILE_PATH = "/.json";

    public static enum CATEGORY{
        FAV_TOPIC,
        FAV_COMIC,
        MAIN_TAB_TOPIC_LIST,
        MAIN_TAB_FEED,
        MAIN_TAB_FEED_ATTENTION,
        MAIN_TAB_BANNERS;
    }

    private static String getFilePath(CATEGORY category){
        if(FileUtil.isSDCardMounted()){
            return FileUtil.SDPATH + JSON_FILE_PATH + "/" + category.name();
        }
        return "";
    }

    public static void writeJsonToFile(CATEGORY category, String json){
        if(FileUtil.isSDCardMounted()){
            FileUtil.writeToFileFromString(getFilePath(category), json);
        }
    }

    public static String getJsonFromFile(CATEGORY category){
        if(FileUtil.isFileExists(getFilePath(category))){
            return FileUtil.readStringFromFile(getFilePath(category));
        }else{
            return "";
        }
    }

}
