package com.kuaikan.comic.rest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.kuaikan.comic.dao.bean.ComicBriefBean;

/**
 * Created by skyfishjy on 12/24/14.
 */
public class ComicBrief extends BaseModel implements Parcelable {
    public static final Parcelable.Creator<ComicBrief> CREATOR = new Parcelable.Creator<ComicBrief>() {
        public ComicBrief createFromParcel(Parcel source) {
            return new ComicBrief(source);
        }

        public ComicBrief[] newArray(int size) {
            return new ComicBrief[size];
        }
    };
    private String cover_image_url;
    private long created_at;
    private long id;
    private String title;
    private long topic_id;
    private long updated_at;
    private String url;
    private int likes_count;

    public ComicBrief() {
    }

    private ComicBrief(Parcel in) {
        this.cover_image_url = in.readString();
        this.created_at = in.readLong();
        this.id = in.readLong();
        this.title = in.readString();
        this.topic_id = in.readInt();
        this.updated_at = in.readLong();
        this.url = in.readString();
        this.likes_count = in.readInt();
    }

    public String getCover_image_url() {
        return cover_image_url;
    }

    public void setCover_image_url(String cover_image_url) {
        this.cover_image_url = cover_image_url;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(int topic_id) {
        this.topic_id = topic_id;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cover_image_url);
        dest.writeLong(this.created_at);
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeLong(this.topic_id);
        dest.writeLong(this.updated_at);
        dest.writeString(this.url);
        dest.writeInt(this.likes_count);
    }

    public ComicBriefBean toComicBriefBean(){

        ComicBriefBean comicBriefBean = new ComicBriefBean();
        comicBriefBean.setId(this.id);
        comicBriefBean.setCover_image_url(this.cover_image_url);
        comicBriefBean.setCreated_at(this.created_at);
        comicBriefBean.setTitle(this.title);
        comicBriefBean.setUpdated_at(this.updated_at);
        comicBriefBean.setUrl(this.url);
        return comicBriefBean;
    }

    public ComicBrief(ComicBriefBean comicBriefBean){

        this.cover_image_url = comicBriefBean.getCover_image_url();
        this.created_at = comicBriefBean.getCreated_at();
        this.id = comicBriefBean.getId();
        this.title = comicBriefBean.getTitle();
        this.topic_id = comicBriefBean.getTopic_id();
        this.updated_at = comicBriefBean.getUpdated_at();
        this.url = comicBriefBean.getUrl();
    }

}
