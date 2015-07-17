package com.kuaikan.comic.db;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.dao.SearchHistoryDao;
import com.kuaikan.comic.dao.bean.SearchHistory;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by a on 2015/3/27.
 */
public class HistoryDaoHelper implements IDaoHelper{

    private static HistoryDaoHelper mInstance;

    private SearchHistoryDao searchHistoryDao;

    private HistoryDaoHelper(){
        searchHistoryDao = KKMHApp.getDaoSession().getSearchHistoryDao();
    }

    public static HistoryDaoHelper getInstance() {
        if(mInstance == null) {
            mInstance = new HistoryDaoHelper();
        }
        return mInstance;
    }


    @Override
    public <T> void addData(T t) {
        if(searchHistoryDao != null && t != null) {
            SearchHistory s = (SearchHistory) t;
            searchHistoryDao.insert(s);
        }
    }

    @Override
    public void deleteData(Long id) {
    }

    public String getDataById(Long id) {
        return "";
    }

    @Override
    public List getAllData() {
        if(searchHistoryDao != null) {
            return searchHistoryDao.loadAll();
        }
        return null;
    }

    @Override
    public boolean hasKey(Long id) {
        return false;
    }

    @Override
    public long getTotalCount() {
        if(searchHistoryDao == null) {
            return 0;
        }

        QueryBuilder<SearchHistory> qb = searchHistoryDao.queryBuilder();
        return qb.buildCount().count();
    }

    @Override
    public void deleteAll() {
        if(searchHistoryDao != null) {
            searchHistoryDao.deleteAll();
        }
    }
}