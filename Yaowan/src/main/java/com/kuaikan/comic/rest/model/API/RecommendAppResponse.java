package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.App;
import com.kuaikan.comic.rest.model.BaseModel;

import java.util.List;

/**
 * Created by a on 2015/4/24.
 */
public class RecommendAppResponse  extends BaseModel {

    private List<App> apps;

    private boolean has_more = false;

    public List<App> getApps() {
        return apps;
    }

    public boolean isHasmore() {
        return has_more;
    }
}
