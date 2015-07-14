package com.kuaikan.comic.db;

import java.util.List;

/**
 * Created by a on 2015/3/27.
 */
public interface IDaoHelper {
    public <T> void addData(T t);
    public void deleteData(Long id);
    public List getAllData();
    public boolean hasKey(Long id);
    public long getTotalCount();
    public void deleteAll();
}
