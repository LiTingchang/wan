package com.kuaikan.comic.rest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.kuaikan.comic.dao.bean.UserBean;

import java.util.List;

/**
 * Created by skyfishjy on 12/23/14.
 */
public class User extends BaseModel implements Parcelable {
    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String avatar_url;
    private long id;
    private String nickname;
    private String intro;
    private String weibo;
    private String works;
    private String reg_type;
    private String phone;
    private String wechat;
    private String site;
    private String android;
    private String weibo_name;
    private List<Topic> topics;


    public User() {
        avatar_url = "";
        id = -1;
        nickname = "";
        intro = "";
        weibo = "http://weibo.com/kuaikanmanhua";
        works = "";
        reg_type = "";
        phone = "";
        wechat = "";
        site = "";
        android = "";
        weibo_name = "";
    }

    private User(Parcel in) {
        this.avatar_url = in.readString();
        this.id = in.readLong();
        this.nickname = in.readString();
        this.intro = in.readString();
        this.weibo = in.readString();
        this.works = in.readString();
        this.reg_type = in.readString();
        this.phone = in.readString();
        this.wechat = in.readString();
        this.site = in.readString();
        this.android = in.readString();
        this.weibo_name = in.readString();
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getIntro() {
        return intro;
    }

    public String getWeibo() {
        return weibo;
    }

    public String getWorks() {
        return works;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getWechat() {
        return wechat;
    }

    public String getSite() {
        return site;
    }

    public String getAndroid() {
        return android;
    }

    public String getWeibo_name() {
        return weibo_name;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar_url);
        dest.writeLong(this.id);
        dest.writeString(this.nickname);
        dest.writeString(this.intro);
        dest.writeString(this.weibo);
        dest.writeString(this.works);
        dest.writeString(this.reg_type);
        dest.writeString(this.phone);
        dest.writeString(this.wechat);
        dest.writeString(this.site);
        dest.writeString(this.android);
        dest.writeString(this.weibo_name);
    }

    public UserBean toUserBean(){
        UserBean userBean = new UserBean();
        userBean.setAvatar_url(avatar_url);
        userBean.setId(id);
        userBean.setNickname(nickname);
        userBean.setIntro(intro);
        userBean.setWeibo(weibo);
        userBean.setWorks(works);
        return userBean;
    }

    public User(UserBean userBean){
        this.avatar_url = userBean.getAvatar_url();
        this.nickname = userBean.getNickname();
        this.intro = userBean.getIntro();
        this.id = userBean.getId();
        this.weibo = userBean.getWeibo();
        this.works = userBean.getWorks();
    }
}
