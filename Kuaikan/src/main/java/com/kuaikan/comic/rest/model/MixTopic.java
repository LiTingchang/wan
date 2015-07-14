package com.kuaikan.comic.rest.model;

import java.util.List;

/**
 * Created by a on 2015/4/16.
 */
public class MixTopic extends BaseModel{

//    public static final int TYPE_TOPIC = 0;
//    public static final int TYPE_COMIC = 1;

    private String action;
    private String title;
    private int type;

    private List<Topic> topics;

    private List<Comic> comics;

    public String getAction() {
        return action;
    }

    public String getTitle() {
        return title;
    }

    public int getType(){ return type; }

    public List<Topic> getTopics() {
        return topics;
    }

    public List<Comic> getComics() {
        return comics;
    }
}
