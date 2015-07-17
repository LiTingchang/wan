package com.kuaikan.comic.db;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.dao.TopicBeanDao;
import com.kuaikan.comic.dao.bean.TopicBean;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by a on 2015/3/27.
 */
public class TopicBeanDaoHelper implements IDaoHelper{

    private static TopicBeanDaoHelper mInstance;

    private TopicBeanDao topicBeanDao;

    private TopicBeanDaoHelper(){
        topicBeanDao = KKMHApp.getDaoSession().getTopicBeanDao();
    }

    public static TopicBeanDaoHelper getInstance() {
        if(mInstance == null) {
            mInstance = new TopicBeanDaoHelper();
        }
        return mInstance;
    }


    @Override
    public <T> void addData(T t) {
        if(topicBeanDao != null && t != null) {
            TopicBean tb = (TopicBean) t;
            topicBeanDao.insertOrReplace(tb);
            if(tb.getUserBean() != null){
                if(!UserBeanDaoHelper.getInstance().hasKey(tb.getUserBean().getId())){
                    UserBeanDaoHelper.getInstance().addData(tb.getUserBean());
                }
            }
        }
    }

    @Override
    public void deleteData(Long id) {
        if(topicBeanDao != null && id != null) {
            topicBeanDao.deleteByKey(id);
        }
    }

    public TopicBean getDataById(Long id) {
        if(topicBeanDao != null && id != null) {
            return (TopicBean)topicBeanDao.load(id);
        }
        return null;
    }

    @Override
    public List getAllData() {
        if(topicBeanDao != null) {
            return topicBeanDao.loadAll();
        }
        return null;
    }

    @Override
    public boolean hasKey(Long id) {
        if(topicBeanDao == null || id == null) {
            return false;
        }

        QueryBuilder<TopicBean> qb = topicBeanDao.queryBuilder();
        qb.where(TopicBeanDao.Properties.Id.eq(id));
        long count = qb.buildCount().count();
        return count > 0 ? true : false;
    }

    @Override
    public long getTotalCount() {
        if(topicBeanDao == null) {
            return 0;
        }

        QueryBuilder<TopicBean> qb = topicBeanDao.queryBuilder();
        return qb.buildCount().count();
    }

    @Override
    public void deleteAll() {
        if(topicBeanDao != null) {
            topicBeanDao.deleteAll();
        }
    }
}