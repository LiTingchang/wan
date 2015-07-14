package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.Banner;
import com.kuaikan.comic.rest.model.BaseModel;

import java.util.List;

/**
 * Created by a on 2015/4/1.
 */
public class BannerResponse extends BaseModel {
    private List<Banner> banner_group;

    public BannerResponse() {
    }

    public List<Banner> getBannerGroup() {
        return banner_group;
    }
}
