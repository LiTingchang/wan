package com.kuaikan.comic.rest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.kuaikan.comic.dao.bean.TopicBean;

/**
 * Created by skyfishjy on 12/18/14.
 */
public class Topic extends BaseModel implements Parcelable {
    public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {
        public Topic createFromParcel(Parcel source) {
            return new Topic(source);
        }

        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };
    private int comics_count;
    private String cover_image_url;
    private String vertical_image_url;
    private long created_at;
    private String description;
    private long id;
    private int order;
    private String title;
    private long updated_at;
    private User user;

    public Topic() {
        user = new User();
        title = "";
        description = "";
    }

    private Topic(Parcel in) {
        this.comics_count = in.readInt();
        this.cover_image_url = in.readString();
        this.vertical_image_url = in.readString();
        this.created_at = in.readLong();
        this.description = in.readString();
        this.id = in.readLong();
        this.order = in.readInt();
        this.title = in.readString();
        this.updated_at = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public int getComics_count() {
        return comics_count;
    }

    public String getCover_image_url() {
        return cover_image_url;
    }

    public long getCreated_at() {
        return created_at;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public int getOrder() {
        return order;
    }

    public String getTitle() {
        return title;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public User getUser() {
        return user;
    }

    public String getVerticalImageUrl() { return vertical_image_url; };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.comics_count);
        dest.writeString(this.cover_image_url);
        dest.writeString(this.vertical_image_url);
        dest.writeLong(this.created_at);
        dest.writeString(this.description);
        dest.writeLong(this.id);
        dest.writeInt(this.order);
        dest.writeString(this.title);
        dest.writeLong(this.updated_at);
        dest.writeParcelable(this.user, 0);
    }

    public TopicBean toTopicBean(){

        TopicBean topicBean = new TopicBean();
        topicBean.setComics_count(comics_count);
        topicBean.setCover_image_url(cover_image_url);
        topicBean.setCreated_at(created_at);
        topicBean.setDescription(description);
        topicBean.setId(id);
        topicBean.setOrder(order);
        topicBean.setTitle(title);
        topicBean.setUpdated_at(updated_at);
        topicBean.setUserBean(user.toUserBean());
        return topicBean;

    }

    public Topic(TopicBean topicBean){
        this.comics_count = topicBean.getComics_count();
        this.cover_image_url = topicBean.getCover_image_url();
        this.created_at = topicBean.getCreated_at();
        this.id = topicBean.getId();
        this.description = topicBean.getDescription();
        this.order = topicBean.getOrder();
        this.title = topicBean.getTitle();
        this.updated_at = topicBean.getUpdated_at();
        this.user = new User(topicBean.getUserBean());
    }
}
