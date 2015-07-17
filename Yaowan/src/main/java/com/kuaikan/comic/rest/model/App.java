package com.kuaikan.comic.rest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a on 2015/4/24.
 */
public class App extends BaseModel implements Parcelable {
    public static final Parcelable.Creator<App> CREATOR = new Parcelable.Creator<App>() {
        public App createFromParcel(Parcel source) {
            return new App(source);
        }

        public App[] newArray(int size) {
            return new App[size];
        }
    };

    private String icon;
    private String name;
    private String desc;
    private String download;
    @SerializedName("package")
    private String packagename;

    public App() {
        icon = "";
        name = "";
        desc = "";
        download = "";
        packagename = "";
    }

    private App(Parcel in) {
        this.icon = in.readString();
        this.name = in.readString();
        this.desc = in.readString();
        this.download = in.readString();
        this.packagename = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.icon);
        dest.writeString(this.name);
        dest.writeString(this.desc);
        dest.writeString(this.download);
        dest.writeString(this.packagename);
    }

    public String getCoverUrl() {
        return icon;
    }

    public String getTitle() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getUrl() {
        return download;
    }

    public String getPackagename() {
        return packagename;
    }
}
