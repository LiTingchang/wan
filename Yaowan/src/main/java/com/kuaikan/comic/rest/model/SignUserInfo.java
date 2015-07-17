package com.kuaikan.comic.rest.model;

/**
 * Created by skyfishjy on 1/5/15.
 */
public class SignUserInfo extends BaseModel {
    String avatar_url;
    String id;
    String nickname;
    String reg_type;

    public SignUserInfo(String avatar_url, String id, String nickname) {
        this.avatar_url = avatar_url;
        this.id = id;
        this.nickname = nickname;
    }


    public String getAvatar_url() {
        return avatar_url;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getReg_type() {
        return reg_type;
    }
}
