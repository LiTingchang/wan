package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.BaseModel;
import com.kuaikan.comic.rest.model.Topic;

import java.util.List;

/**
 * Created by skyfishjy on 12/24/14.
 */
public class HotTopicResponse extends BaseModel {
    private List<Topic> topics;

    public HotTopicResponse() {
    }

    public List<Topic> getTopics() {
        return topics;
    }
}
