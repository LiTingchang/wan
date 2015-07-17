package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.BaseModel;
import com.kuaikan.comic.rest.model.Comic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skyfishjy on 12/19/14.
 */
public class FavTimelineResponse extends BaseModel {
    private List<Comic> comics = new ArrayList<>();

    private int since;

    public FavTimelineResponse() {
    }

    public List<Comic> getComicList() {
        return comics;
    }

    public int getSince(){
        return since;
    }
}
