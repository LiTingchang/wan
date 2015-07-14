package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.BaseModel;
import com.kuaikan.comic.rest.model.Comic;

import java.util.List;

/**
 * Created by skyfishjy on 12/19/14.
 */
public class ComicResponse extends BaseModel {
    private List<Comic> comics;

    public ComicResponse() {
    }

    public List<Comic> getComicList() {
        return comics;
    }
}
