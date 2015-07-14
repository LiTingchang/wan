package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    /**
     * version1:初始化数据库
     */
    private static final int DB_VERSION = 1;

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(DB_VERSION, "com.kuaikan.comic.dao.bean");
        schema.setDefaultJavaPackageDao("com.kuaikan.comic.dao");
        initTables(schema);
        new DaoGenerator().generateAll(schema,args[0]);
    }

    private static void initTables(Schema schema) {
        Entity userBean = schema.addEntity("UserBean");
        userBean.setTableName("USER");
        userBean.addLongProperty("id").primaryKey().index();
        userBean.addStringProperty("avatar_url");
        userBean.addStringProperty("nickname");
        userBean.addStringProperty("intro");
        userBean.addStringProperty("weibo");
        userBean.addStringProperty("works");

        Entity topicBean = schema.addEntity("TopicBean");
        topicBean.setTableName("TOPIC");
        topicBean.addLongProperty("id").primaryKey().index();
        topicBean.addIntProperty("comics_count");
        topicBean.addStringProperty("cover_image_url");
        topicBean.addLongProperty("created_at");
        topicBean.addStringProperty("description");
        topicBean.addIntProperty("order");
        topicBean.addStringProperty("title");
        topicBean.addLongProperty("updated_at");
        Property userId = topicBean.addLongProperty("userId").notNull().getProperty();
        topicBean.addToOne(userBean, userId);


        Entity comicBriefBean = schema.addEntity("ComicBriefBean");
        comicBriefBean.setTableName("COMIC_BRIEF");
        comicBriefBean.addLongProperty("id").primaryKey().index();
        comicBriefBean.addStringProperty("cover_image_url");
        comicBriefBean.addLongProperty("created_at");
        comicBriefBean.addStringProperty("title");
        comicBriefBean.addLongProperty("topic_id");
        comicBriefBean.addLongProperty("updated_at");
        comicBriefBean.addStringProperty("url");

        Entity comicDetailBean = schema.addEntity("ComicDetailBean");
        comicDetailBean.setTableName("COMIC_DETAIL");
        comicDetailBean.addIntProperty("comments_count");
        comicDetailBean.addStringProperty("cover_image_url");
        comicDetailBean.addLongProperty("created_at");
        comicDetailBean.addLongProperty("id").primaryKey().index();
        comicDetailBean.addStringProperty("images");
        comicDetailBean.addBooleanProperty("is_favourite");
        comicDetailBean.addBooleanProperty("is_liked");
        comicDetailBean.addLongProperty("likes_count");
        comicDetailBean.addStringProperty("title");
        comicDetailBean.addBooleanProperty("have_read");
        Property comicDetailTopicId = comicDetailBean.addLongProperty("topicId").notNull().getProperty();
        comicDetailBean.addToOne(topicBean, comicDetailTopicId);
        comicDetailBean.addLongProperty("updated_at");
        comicDetailBean.addStringProperty("url");
        comicDetailBean.addLongProperty("recommend_count");

        Entity searchHistory = schema.addEntity("SearchHistory");
        searchHistory.setTableName("SEARCH_HISTORY");
        searchHistory.addStringProperty("search_content");

    }

}
