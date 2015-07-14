package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.BaseModel;

/**
 * Created by liuchao1 on 15/6/15.
 */
public class FavUnreadResponse extends BaseModel {

    private int unread;

    public int getUnreadCount(){
        return unread;
    }
}
