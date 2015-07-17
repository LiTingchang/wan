package com.kuaikan.comic.db;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.dao.UserBeanDao;
import com.kuaikan.comic.dao.bean.UserBean;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by a on 2015/3/27.
 */
public class UserBeanDaoHelper implements IDaoHelper{

    private static UserBeanDaoHelper mInstance;

    private UserBeanDao userBeanDao;

    private UserBeanDaoHelper(){
        userBeanDao = KKMHApp.getDaoSession().getUserBeanDao();
    }

    public static UserBeanDaoHelper getInstance() {
        if(mInstance == null) {
            mInstance = new UserBeanDaoHelper();
        }
        return mInstance;
    }


    @Override
    public <T> void addData(T t) {
        if(userBeanDao != null && t != null) {
            userBeanDao.insertOrReplace((UserBean) t);
        }
    }

    @Override
    public void deleteData(Long id) {
        if(userBeanDao != null && id != null) {
            userBeanDao.deleteByKey(id);
        }
    }

    public UserBean getDataById(Long id) {
        if(userBeanDao != null && id != null) {
            return userBeanDao.load(id);
        }
        return null;
    }

    @Override
    public List getAllData() {
        if(userBeanDao != null) {
            return userBeanDao.loadAll();
        }
        return null;
    }

    @Override
    public boolean hasKey(Long id) {
        if(userBeanDao == null || id == null) {
            return false;
        }

        QueryBuilder<UserBean> qb = userBeanDao.queryBuilder();
        qb.where(UserBeanDao.Properties.Id.eq(id));
        long count = qb.buildCount().count();
        return count > 0 ? true : false;
    }

    @Override
    public long getTotalCount() {
        if(userBeanDao == null) {
            return 0;
        }

        QueryBuilder<UserBean> qb = userBeanDao.queryBuilder();
        return qb.buildCount().count();
    }

    @Override
    public void deleteAll() {
        if(userBeanDao != null) {
            userBeanDao.deleteAll();
        }
    }
}