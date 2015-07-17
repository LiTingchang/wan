package com.kuaikan.comic.rest.model;

import com.kuaikan.comic.util.GsonUtil;

/**
 * Created by skyfishjy on 12/19/14.
 */
public class BaseModel {
    public String toJSON() {
        return GsonUtil.toJson(this);
    }
}
