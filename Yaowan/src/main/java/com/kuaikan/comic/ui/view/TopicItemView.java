package com.kuaikan.comic.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.Topic;

/**
 * Created by liuchao1 on 15/4/29.
 */
public class TopicItemView extends LinearLayout{

    private Topic mTopic;

    public TopicItemView(Context context, Topic topic) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View myView = inflater.inflate(R.layout.listitem_fav_topic, null);
        addView(myView);
        this.mTopic = topic;
    }





}
