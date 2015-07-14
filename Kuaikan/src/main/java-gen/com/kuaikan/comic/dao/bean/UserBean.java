package com.kuaikan.comic.dao.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table USER.
 */
public class UserBean {

    private Long id;
    private String avatar_url;
    private String nickname;
    private String intro;
    private String weibo;
    private String works;

    public UserBean() {
    }

    public UserBean(Long id) {
        this.id = id;
    }

    public UserBean(Long id, String avatar_url, String nickname, String intro, String weibo, String works) {
        this.id = id;
        this.avatar_url = avatar_url;
        this.nickname = nickname;
        this.intro = intro;
        this.weibo = weibo;
        this.works = works;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    public String getWorks() {
        return works;
    }

    public void setWorks(String works) {
        this.works = works;
    }

}