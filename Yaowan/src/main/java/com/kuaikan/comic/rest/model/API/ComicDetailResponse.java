package com.kuaikan.comic.rest.model.API;

import android.os.Parcel;
import android.os.Parcelable;

import com.kuaikan.comic.dao.bean.ComicDetailBean;
import com.kuaikan.comic.rest.model.BaseModel;
import com.kuaikan.comic.rest.model.Topic;
import com.kuaikan.comic.util.GsonUtil;

/**
 * Created by skyfishjy on 12/23/14.
 */
public class ComicDetailResponse extends BaseModel implements Parcelable {
    public static final Creator<ComicDetailResponse> CREATOR = new Creator<ComicDetailResponse>() {
        public ComicDetailResponse createFromParcel(Parcel source) {
            return new ComicDetailResponse(source);
        }

        public ComicDetailResponse[] newArray(int size) {
            return new ComicDetailResponse[size];
        }
    };
    private int comments_count;
    private String cover_image_url;
    private long created_at;
    private long id;
    private String[] images;
    private boolean is_favourite;
    private boolean is_liked;
    private long likes_count;
    private String title;
    private Topic topic;
    private long updated_at;
    private String url;
    private long recommend_count;
    private long next_comic_id;
    private long previous_comic_id;

    public ComicDetailResponse() {
        images = new String[]{};
        topic = new Topic();
    }

    private ComicDetailResponse(Parcel in) {
        this.comments_count = in.readInt();
        this.cover_image_url = in.readString();
        this.created_at = in.readLong();
        this.id = in.readLong();
        this.images = in.createStringArray();
        this.is_favourite = in.readByte() != 0;
        this.is_liked = in.readByte() != 0;
        this.likes_count = in.readLong();
        this.title = in.readString();
        this.topic = in.readParcelable(Topic.class.getClassLoader());
        this.updated_at = in.readLong();
        this.url = in.readString();
        this.recommend_count = in.readLong();
        this.next_comic_id = in.readLong();
        this.previous_comic_id = in.readLong();
    }

    public int getComments_count() {
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

    public String[] getImages() {
        return images;
    }

    public void setIs_favourite(boolean is_favourite) {
        this.is_favourite = is_favourite;
    }

    public boolean is_favourite() {
        return is_favourite;
    }

    public boolean is_liked() {
        return is_liked;
    }

    public void setIs_liked(boolean is_liked) {
        this.is_liked = is_liked;
    }

    public long getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(long likes_count) {
        this.likes_count = likes_count;
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

    public long getRecommend_count() {
        return recommend_count;
    }

    public void setRecommend_count(long recommend_count) {
        this.recommend_count = recommend_count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getNext_comic_id() {
        return next_comic_id;
    }

    public long getPrevious_comic_id() {
        return previous_comic_id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.comments_count);
        dest.writeString(this.cover_image_url);
        dest.writeLong(this.created_at);
        dest.writeLong(this.id);
        dest.writeStringArray(this.images);
        dest.writeByte(is_favourite ? (byte) 1 : (byte) 0);
        dest.writeByte(is_liked ? (byte) 1 : (byte) 0);
        dest.writeLong(this.likes_count);
        dest.writeString(this.title);
        dest.writeParcelable(this.topic, 0);
        dest.writeLong(this.updated_at);
        dest.writeString(this.url);
        dest.writeLong(this.recommend_count);
        dest.writeLong(this.next_comic_id);
        dest.writeLong(this.previous_comic_id);
    }

    public ComicDetailBean toComicDetailBean(){

        ComicDetailBean comicDetailBean = new ComicDetailBean();
        comicDetailBean.setComments_count(comments_count);
        comicDetailBean.setCover_image_url(cover_image_url);
        comicDetailBean.setCreated_at(created_at);
        comicDetailBean.setId(id);
        comicDetailBean.setImages(GsonUtil.toJson(images));
        comicDetailBean.setIs_favourite(is_favourite);
        comicDetailBean.setIs_liked(is_liked);
        comicDetailBean.setLikes_count(likes_count);
        comicDetailBean.setTitle(title);
        comicDetailBean.setTopicBean(topic.toTopicBean());
        comicDetailBean.setUpdated_at(updated_at);
        comicDetailBean.setUrl(url);
        comicDetailBean.setRecommend_count(recommend_count);
        return comicDetailBean;

    }

    public ComicDetailResponse(ComicDetailBean comicDetailBean){
        this.comments_count = comicDetailBean.getComments_count();
        this.cover_image_url = comicDetailBean.getCover_image_url();
        this.created_at = comicDetailBean.getCreated_at();
        this.id = comicDetailBean.getId();
        this.images = GsonUtil.fromJson(comicDetailBean.getImages(), String[].class);
        this.is_favourite = comicDetailBean.getIs_favourite();
        this.is_liked = comicDetailBean.getIs_liked();
        this.likes_count = comicDetailBean.getLikes_count();
        this.title = comicDetailBean.getTitle();
        this.topic = new Topic(comicDetailBean.getTopicBean());
        this.updated_at = comicDetailBean.getUpdated_at();
        this.url = comicDetailBean.getUrl();
        this.recommend_count = comicDetailBean.getRecommend_count();
    }
}
