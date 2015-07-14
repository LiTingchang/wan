package com.kuaikan.comic.dao.bean;

import com.kuaikan.comic.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.kuaikan.comic.dao.TopicBeanDao;
import com.kuaikan.comic.dao.UserBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table TOPIC.
 */
public class TopicBean {

    private Long id;
    private Integer comics_count;
    private String cover_image_url;
    private Long created_at;
    private String description;
    private Integer order;
    private String title;
    private Long updated_at;
    private long userId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient TopicBeanDao myDao;

    private UserBean userBean;
    private Long userBean__resolvedKey;


    public TopicBean() {
    }

    public TopicBean(Long id) {
        this.id = id;
    }

    public TopicBean(Long id, Integer comics_count, String cover_image_url, Long created_at, String description, Integer order, String title, Long updated_at, long userId) {
        this.id = id;
        this.comics_count = comics_count;
        this.cover_image_url = cover_image_url;
        this.created_at = created_at;
        this.description = description;
        this.order = order;
        this.title = title;
        this.updated_at = updated_at;
        this.userId = userId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTopicBeanDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getComics_count() {
        return comics_count;
    }

    public void setComics_count(Integer comics_count) {
        this.comics_count = comics_count;
    }

    public String getCover_image_url() {
        return cover_image_url;
    }

    public void setCover_image_url(String cover_image_url) {
        this.cover_image_url = cover_image_url;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Long updated_at) {
        this.updated_at = updated_at;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    /** To-one relationship, resolved on first access. */
    public UserBean getUserBean() {
        long __key = this.userId;
        if (userBean__resolvedKey == null || !userBean__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserBeanDao targetDao = daoSession.getUserBeanDao();
            UserBean userBeanNew = targetDao.load(__key);
            synchronized (this) {
                userBean = userBeanNew;
            	userBean__resolvedKey = __key;
            }
        }
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        if (userBean == null) {
            throw new DaoException("To-one property 'userId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.userBean = userBean;
            userId = userBean.getId();
            userBean__resolvedKey = userId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
