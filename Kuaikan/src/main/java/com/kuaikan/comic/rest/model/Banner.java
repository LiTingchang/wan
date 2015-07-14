package com.kuaikan.comic.rest.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by a on 2015/4/1.
 */
public class Banner extends BaseModel implements Parcelable {
    public static final Parcelable.Creator<Banner> CREATOR = new Parcelable.Creator<Banner>() {
        public Banner createFromParcel(Parcel source) {
            return new Banner(source);
        }

        public Banner[] newArray(int size) {
            return new Banner[size];
        }
    };

    public static final int TYPE_NONE = 0;
    public static final int TYPE_URL = 1;
    public static final int TYPE_TOPIC = 2;
    public static final int TYPE_COMIC = 3;
    public static final int TYPE_CATEGORY = 4;

    //图片url
    private String pic;
    //标题
    private String title;
    //动作类型
    private int type;
    //动作内容
    private String value;

    public Banner() {
    }

    private Banner(Parcel in) {
        this.pic = in.readString();
        this.title = in.readString();
        this.type = in.readInt();
        this.value = in.readString();
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pic);
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeString(this.value);
    }
}