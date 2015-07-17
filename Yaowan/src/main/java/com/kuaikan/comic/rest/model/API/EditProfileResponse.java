package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.BaseModel;

/**
 * Created by a on 2015/4/19.
 */
public class EditProfileResponse extends BaseModel {

    private String avatar_url;

    private long id;

    private String nickname;

    private String reg_type;

    public String getAvatar_url() {
        return avatar_url;
    }

    public long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getReg_type() {
        return reg_type;
    }
}
