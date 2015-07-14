package com.kuaikan.comic.db;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.dao.ComicBriefBeanDao;
import com.kuaikan.comic.dao.bean.ComicBriefBean;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by a on 2015/3/27.
 */
public class ComicBriefBeanDaoHelper implements IDaoHelper{

    private static ComicBriefBeanDaoHelper mInstance;

    private ComicBriefBeanDao comicBriefBeanDao;

    private ComicBriefBeanDaoHelper(){
        comicBriefBeanDao = KKMHApp.getDaoSession().getComicBriefBeanDao();
    }

    public static ComicBriefBeanDaoHelper getInstance() {
        if(mInstance == null) {
            mInstance = new ComicBriefBeanDaoHelper();
        }
        return mInstance;
    }


    @Override
    public <T> void addData(T t) {
        if(comicBriefBeanDao != null && t != null) {
            ComicBriefBean comicBriefBean = (ComicBriefBean) t;
            if(!hasKey(comicBriefBean.getId())){
                comicBriefBeanDao.insertOrReplace(comicBriefBean);
            }
        }
    }

    public <T> void addDataAll(List<T> ts) {
        if(comicBriefBeanDao != null && ts != null && ts.size() > 0 ) {
            for(T t : ts){
                addData(t);
            }
        }
    }


    @Override
    public void deleteData(Long id) {
        if(comicBriefBeanDao != null && id != null) {
            comicBriefBeanDao.deleteByKey(id);
        }
    }

    public ComicBriefBean getDataById(Long id) {
        if(comicBriefBeanDao != null && id != null) {
            return comicBriefBeanDao.load(id);
        }
        return null;
    }

    @Override
    public List getAllData() {
        if(comicBriefBeanDao != null) {
            return comicBriefBeanDao.loadAll();
        }
        return null;
    }

    @Override
    public boolean hasKey(Long id) {
        if(comicBriefBeanDao == null || id == null) {
            return false;
        }

        QueryBuilder<ComicBriefBean> qb = comicBriefBeanDao.queryBuilder();
        qb.where(ComicBriefBeanDao.Properties.Id.eq(id));
        long count = qb.buildCount().count();
        return count > 0 ? true : false;
    }

    @Override
    public long getTotalCount() {
        if(comicBriefBeanDao == null) {
            return 0;
        }

        QueryBuilder<ComicBriefBean> qb = comicBriefBeanDao.queryBuilder();
        return qb.buildCount().count();
    }

    @Override
    public void deleteAll() {
        if(comicBriefBeanDao != null) {
            comicBriefBeanDao.deleteAll();
        }
    }
}
