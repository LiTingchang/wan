package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.BaseModel;

import java.util.List;

/**
 * Created by a on 2015/4/7.
 */
public class SuggestionListResponse extends BaseModel{

    private List<String> suggestion;

    public SuggestionListResponse(){
    }

    public List<String> getSuggestion(){
        return suggestion;
    }

}
