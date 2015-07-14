package com.kuaikan.comic.rest.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by skyfishjy on 12/18/14.
 */
public class Comic extends BaseModel implements Parcelable {
    public static final Parcelable.Creator<Comic> CREATOR = new Parcelable.Creator<Comic>() {
        public Comic createFromParcel(Parcel source) {
            return new Comic(source);
        }

        public Comic[] newArray(int size) {
            return new Comic[size];
        }
    };
    private long comments_count;
    private String cover_image_url;
    private long created_at;
    private long id;
    private int likes_count;
    private String title;
    private Topic topic;
    private long updated_at;
    private String url;
    private boolean is_liked;
    private int shared_count;

    public Comic() {
    }

    private Comic(Parcel in) {
        this.comments_count = in.readLong();
        this.cover_image_url = in.readString();
        this.created_at = in.readLong();
        this.id = in.readLong();
        this.likes_count = in.readInt();
        this.title = in.readString();
        this.topic = in.readParcelable(Topic.class.getClassLoader());
        this.updated_at = in.readLong();
        this.url = in.readString();
        this.is_liked = in.readByte() != 0;
        this.shared_count = in.readInt();
    }

    public long getComments_count() {
        return comments_count;
    }

    public String getCover_image_url() {
        return cover_image_url;
    }

    public long getCreated_at() {
        return created_at;
    }

    public long getId() {
        return id;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public String getTitle() {
        return title;
    }

    public Topic getTopic() {
        return topic;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public String getUrl() {
        return url;
    }

    public boolean is_liked() {
        return is_liked;
    }

    public void setIs_liked(boolean is_liked) {
        this.is_liked = is_liked;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public void setShare_count(int share_count) { this.shared_count = share_count; }

    public int getShare_count() { return shared_count; }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.comments_count);
        dest.writeString(this.cover_image_url);
        dest.writeLong(this.created_at);
        dest.writeLong(this.id);
        dest.writeInt(this.likes_count);
        dest.writeString(this.title);
        dest.writeParcelable(this.topic, 0);
        dest.writeLong(this.updated_at);
        dest.writeString(this.url);
        dest.writeByte(is_liked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.shared_count);
    }
}
