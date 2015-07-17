package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.BaseModel;
import com.kuaikan.comic.rest.model.MixTopic;

import java.util.List;

/**
 * Created by a on 2015/4/16.
 */
public class MixTopicResponse extends BaseModel {

    private List<MixTopic> topics;

    public List<MixTopic> getTopics() {
        return topics;
    }
}