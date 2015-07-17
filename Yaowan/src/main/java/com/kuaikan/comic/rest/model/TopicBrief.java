package com.kuaikan.comic.rest.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by skyfishjy on 12/24/14.
 */
public class TopicBrief extends BaseModel implements Parcelable {
    public static final Parcelable.Creator<TopicBrief> CREATOR = new Parcelable.Creator<TopicBrief>() {
        public TopicBrief createFromParcel(Parcel source) {
            return new TopicBrief(source);
        }

        public TopicBrief[] newArray(int size) {
            return new TopicBrief[size];
        }
    };
    private int comics_count;
    private String cover_image_url;
    private long created_at;
    private String description;
    private long id;
    private int order;
    private String title;
    private long updated_at;
    private int user_id;

    public TopicBrief() {
    }

    private TopicBrief(Parcel in) {
        this.comics_count = in.readInt();
        this.cover_image_url = in.readString();
        this.created_at = in.readLong();
        this.description = in.readString();
        this.id = in.readInt();
        this.order = in.readInt();
        this.title = in.readString();
        this.updated_at = in.readLong();
        this.user_id = in.readInt();
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

    public int getUser_id() {
        return user_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.comics_count);
        dest.writeString(this.cover_image_url);
        dest.writeLong(this.created_at);
        dest.writeString(this.description);
        dest.writeLong(this.id);
        dest.writeInt(this.order);
        dest.writeString(this.title);
        dest.writeLong(this.updated_at);
        dest.writeInt(this.user_id);
    }
}
