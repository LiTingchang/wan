package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.BaseModel;

/**
 * Created by a on 2015/4/13.
 */
public class ConfigResponse extends BaseModel {

    public int timeline_polling_interval;

    public int getTimeline_polling_interval(){
        return timeline_polling_interval;
    }
}
