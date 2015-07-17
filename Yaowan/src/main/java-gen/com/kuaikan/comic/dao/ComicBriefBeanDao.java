package com.kuaikan.comic.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.kuaikan.comic.dao.bean.ComicBriefBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table COMIC_BRIEF.
*/
public class ComicBriefBeanDao extends AbstractDao<ComicBriefBean, Long> {

    public static final String TABLENAME = "COMIC_BRIEF";

    /**
     * Properties of entity ComicBriefBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "ID");
        public final static Property Cover_image_url = new Property(1, String.class, "cover_image_url", false, "COVER_IMAGE_URL");
        public final static Property Created_at = new Property(2, Long.class, "created_at", false, "CREATED_AT");
        public final static Property Title = new Property(3, String.class, "title", false, "TITLE");
        public final static Property Topic_id = new Property(4, Long.class, "topic_id", false, "TOPIC_ID");
        public final static Property Updated_at = new Property(5, Long.class, "updated_at", false, "UPDATED_AT");
        public final static Property Url = new Property(6, String.class, "url", false, "URL");
    };


    public ComicBriefBeanDao(DaoConfig config) {
        super(config);
    }
    
    public ComicBriefBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'COMIC_BRIEF' (" + //
                "'ID' INTEGER PRIMARY KEY ," + // 0: id
                "'COVER_IMAGE_URL' TEXT," + // 1: cover_image_url
                "'CREATED_AT' INTEGER," + // 2: created_at
                "'TITLE' TEXT," + // 3: title
                "'TOPIC_ID' INTEGER," + // 4: topic_id
                "'UPDATED_AT' INTEGER," + // 5: updated_at
                "'URL' TEXT);"); // 6: url
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_COMIC_BRIEF_ID ON COMIC_BRIEF" +
                " (ID);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'COMIC_BRIEF'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ComicBriefBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String cover_image_url = entity.getCover_image_url();
        if (cover_image_url != null) {
            stmt.bindString(2, cover_image_url);
        }
 
        Long created_at = entity.getCreated_at();
        if (created_at != null) {
            stmt.bindLong(3, created_at);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(4, title);
        }
 
        Long topic_id = entity.getTopic_id();
        if (topic_id != null) {
            stmt.bindLong(5, topic_id);
        }
 
        Long updated_at = entity.getUpdated_at();
        if (updated_at != null) {
            stmt.bindLong(6, updated_at);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(7, url);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ComicBriefBean readEntity(Cursor cursor, int offset) {
        ComicBriefBean entity = new ComicBriefBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // cover_image_url
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // created_at
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // title
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // topic_id
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // updated_at
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // url
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ComicBriefBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCover_image_url(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCreated_at(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTopic_id(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setUpdated_at(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setUrl(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ComicBriefBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ComicBriefBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}