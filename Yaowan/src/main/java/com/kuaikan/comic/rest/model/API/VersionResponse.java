package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.BaseModel;

/**
 * Created by a on 2015/4/13.
 */
public class VersionResponse extends BaseModel {

    public Version version;

    public static class Version {
        public String desc;
        public String version;
    }

    public String getVersion() {
        if(version == null){
            return null;
        }
        return version.version;
    }

    public String getDesc(){
        if(version == null){
            return null;
        }
        return version.desc;
    }
}
