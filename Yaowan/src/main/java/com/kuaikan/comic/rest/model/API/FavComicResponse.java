package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.BaseModel;
import com.kuaikan.comic.rest.model.Comic;

import java.util.List;

/**
 * Created by skyfishjy on 2/11/15.
 */
public class FavComicResponse extends BaseModel {
    private List<Comic> comics;

    public FavComicResponse() {
    }

    public List<Comic> getFavComicList() {
        return comics;
    }
}
