package com.kuaikan.comic.rest.model;

import com.kuaikan.comic.dao.bean.ComicBriefBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skyfishjy on 12/24/14.
 */
public class TopicDetail extends BaseModel {
    private List<ComicBrief> comics;
    private int comics_count;
    private String cover_image_url;
    private long created_at;
    private String description;
    private long id;
    private int order;
    private String title;
    private long updated_at;
    private boolean is_favourite;
    private User user;
    private long likes_count;
    private long comments_count;
    private int sort;

    public TopicDetail() {
        comics = new ArrayList<>();
        description = "";
        title = "";
        user = new User();
    }

    public List<ComicBrief> getComics() {
        return comics;
    }

    public List<ComicBriefBean> getComicBriefBeans(){
        if(comics != null && comics.size() > 0){
            List<ComicBriefBean> comicBriefBeans = new ArrayList<>();
            for(ComicBrief comicBrief : comics){
                comicBriefBeans.add(comicBrief.toComicBriefBean());
            }
            return comicBriefBeans;
        }
        return null;
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

    public int getSort() { return sort; }

    public void setIs_favourite(boolean is_favourite) {
        this.is_favourite = is_favourite;
    }

    public boolean is_favourite() {
        return is_favourite;
    }

    public long getLikes_count() {
        return likes_count;
    }

    public long getComments_count() {
        return comments_count;
    }
}
