package com.kuaikan.comic.db;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.dao.ComicDetailBeanDao;
import com.kuaikan.comic.dao.bean.ComicDetailBean;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by a on 2015/3/27.
 */
public class ComicDetailBeanDaoHelper implements IDaoHelper{

    private static ComicDetailBeanDaoHelper mInstance;

    private ComicDetailBeanDao comicDetailBeanDao;

    private ComicDetailBeanDaoHelper(){
        comicDetailBeanDao = KKMHApp.getDaoSession().getComicDetailBeanDao();
    }

    public static ComicDetailBeanDaoHelper getInstance() {
        if(mInstance == null) {
            mInstance = new ComicDetailBeanDaoHelper();
        }
        return mInstance;
    }


    @Override
    public <T> void addData(T t) {
        if(comicDetailBeanDao != null && t != null) {
            ComicDetailBean cb = (ComicDetailBean) t;
            comicDetailBeanDao.insertOrReplace(cb);
            if(cb.getTopicBean() != null){
                if(!TopicBeanDaoHelper.getInstance().hasKey(cb.getTopicBean().getId())){
                    TopicBeanDaoHelper.getInstance().addData(cb.getTopicBean());
                }
            }
        }
    }

    @Override
    public void deleteData(Long id) {
        if(comicDetailBeanDao != null && id != null) {
            comicDetailBeanDao.deleteByKey(id);
        }
    }

    public ComicDetailBean getDataById(Long id) {
        if(comicDetailBeanDao != null && id != null) {
            return comicDetailBeanDao.load(id);
        }
        return null;
    }

    @Override
    public List getAllData() {
        if(comicDetailBeanDao != null) {
            return comicDetailBeanDao.loadAll();
        }
        return null;
    }

    @Override
    public boolean hasKey(Long id) {
        if(comicDetailBeanDao == null || id == null) {
            return false;
        }

        QueryBuilder<ComicDetailBean> qb = comicDetailBeanDao.queryBuilder();
        qb.where(ComicDetailBeanDao.Properties.Id.eq(id));
        long count = qb.buildCount().count();
        return count > 0 ? true : false;
    }

    @Override
    public long getTotalCount() {
        if(comicDetailBeanDao == null) {
            return 0;
        }

        QueryBuilder<ComicDetailBean> qb = comicDetailBeanDao.queryBuilder();
        return qb.buildCount().count();
    }

    @Override
    public void deleteAll() {
        if(comicDetailBeanDao != null) {
            comicDetailBeanDao.deleteAll();
        }
    }
}
