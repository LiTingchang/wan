package com.kuaikan.comic.rest.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by skyfishjy on 12/23/14.
 */
public class Comment extends BaseModel implements Parcelable {
    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
    private long comic_id;
    private String content;
    private long created_at;
    private long id;
    private User user;
    private boolean is_liked;
    private int likes_count;

    public Comment() {
    }

    private Comment(Parcel in) {
        this.comic_id = in.readLong();
        this.content = in.readString();
        this.created_at = in.readLong();
        this.id = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.is_liked = in.readByte() != 0;
        this.likes_count = in.readInt();
    }

    public long getComic_id() {
        return comic_id;
    }

    public String getContent() {
        return content;
    }

    public long getCreated_at() {
        return created_at;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public boolean isIs_liked() {
        return is_liked;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public void setIs_liked(boolean is_liked) {
        this.is_liked = is_liked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.comic_id);
        dest.writeString(this.content);
        dest.writeLong(this.created_at);
        dest.writeLong(this.id);
        dest.writeParcelable(this.user, 0);
        dest.writeByte(is_liked ? (byte) 1 : (byte) 0);
        dest.writeInt(likes_count);
    }
}
