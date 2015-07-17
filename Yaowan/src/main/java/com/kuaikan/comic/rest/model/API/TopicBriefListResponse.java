package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.BaseModel;
import com.kuaikan.comic.rest.model.TopicBrief;

import java.util.List;

/**
 * Created by skyfishjy on 12/24/14.
 */
public class TopicBriefListResponse extends BaseModel {
    private List<TopicBrief> topics;

    public TopicBriefListResponse() {
    }

    public List<TopicBrief> getTopics() {
        return topics;
    }
}
