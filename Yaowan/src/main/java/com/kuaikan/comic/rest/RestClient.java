package com.kuaikan.comic.rest;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.kuaikan.comic.rest.model.API.BannerResponse;
import com.kuaikan.comic.rest.model.API.ComicDetailResponse;
import com.kuaikan.comic.rest.model.API.ComicResponse;
import com.kuaikan.comic.rest.model.API.CommentResponse;
import com.kuaikan.comic.rest.model.API.ConfigResponse;
import com.kuaikan.comic.rest.model.API.EditProfileResponse;
import com.kuaikan.comic.rest.model.API.EmptyDataResponse;
import com.kuaikan.comic.rest.model.API.FavComicResponse;
import com.kuaikan.comic.rest.model.API.FavTimelineResponse;
import com.kuaikan.comic.rest.model.API.FavUnreadResponse;
import com.kuaikan.comic.rest.model.API.FeedbackResponse;
import com.kuaikan.comic.rest.model.API.HotTopicResponse;
import com.kuaikan.comic.rest.model.API.MixTopicResponse;
import com.kuaikan.comic.rest.model.API.RecommendAppResponse;
import com.kuaikan.comic.rest.model.API.ShareComicResponse;
import com.kuaikan.comic.rest.model.API.SuggestionListResponse;
import com.kuaikan.comic.rest.model.API.TopicListResponse;
import com.kuaikan.comic.rest.model.API.VersionResponse;
import com.kuaikan.comic.rest.model.Comment;
import com.kuaikan.comic.rest.model.SignUserInfo;
import com.kuaikan.comic.rest.model.TopicDetail;
import com.kuaikan.comic.rest.model.User;
import com.kuaikan.comic.rest.model.WeiboUser;

import java.io.File;
import java.io.IOException;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import timber.log.Timber;

/**
 * Created by skyfishjy on 12/18/14.
 */
public class RestClient {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_LIMIT = 20;
    public static final int DEFAULT_SEARCH_LIMIT = 20;
    public static final int DEFAULT_HOT_TOPIC_LIMIT = 20;
//    private static final String BASE_KUAIKAN_URL = "http://sandbox.kuaikanmanhua.com/v1/";
    private static final String BASE_KUAIKAN_URL = "http://api.kuaikanmanhua.com/v1/";
    private static final String BASE_WEIBO_URL = "https://api.weibo.com/2/";
    private KKService kkService;
    private WBService wbService;

    public RestClient(String userAgent, String accessToken, String cookieSession, String uuid) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .create();
        OkClient okClient = new OkClient();
        RestAdapter kkRestAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_KUAIKAN_URL)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new KKRequestInterceptor(userAgent, cookieSession, uuid))
                .setClient(okClient)
                .build();

        RestAdapter wbRestAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_WEIBO_URL)
                .setRequestInterceptor(new WeiboOauthRequestInterceptor(accessToken))
                .setClient(okClient)
                .build();

        kkService = kkRestAdapter.create(KKService.class);
        wbService = wbRestAdapter.create(WBService.class);
        Timber.tag(RestClient.class.getSimpleName());
    }


    public RestClient() {
        this(null, null, null, null);
    }

    public void getComicLists(int offset, int limit, Callback<ComicResponse> callback) {
        kkService.getComicLists(offset, limit, callback);
    }

    public void getFavTimeline(int since, Callback<FavTimelineResponse> callback) {
        kkService.getFavTimeline(since, callback);
    }

    public void autoAddAttention(Callback<EmptyDataResponse> callback){
        kkService.autoAddAttention(callback);
    }

    public void getComicDetail(long id, Callback<ComicDetailResponse> callback) {
        kkService.getComicDetail(id, callback);
    }

    public void getHotTopicLists(int offset,int limit, Callback<HotTopicResponse> callback) {
        kkService.getHotTopics(offset, limit, callback);
    }

    public void getRecentTopicLists(Callback<TopicListResponse> callback) {
        kkService.getRecentTopics(DEFAULT_OFFSET, DEFAULT_HOT_TOPIC_LIMIT, callback);
    }

    public void getRecommendTopicLists(Callback<TopicListResponse> callback) {
        kkService.getRecommendTopics(DEFAULT_OFFSET, DEFAULT_HOT_TOPIC_LIMIT, callback);
    }

    public void getTopicList(int offset, int limit, Callback<TopicListResponse> callback) {
        kkService.getTopics(offset, limit, callback);
    }

    public void getTagTopics(int offset, int limit, String tag , Callback<TopicListResponse> callback){
        kkService.getTagTopics(offset, limit, tag, callback);
    }

    public void getTopicDetail(long topicID,int sort, Callback<TopicDetail> callback) {
        kkService.getTopicDetail(topicID, sort, callback);
    }

    public void getComicComments(String order, int offset, int limit, long comicID, Callback<CommentResponse> callback) {
        kkService.getComicComments(order, offset, limit, comicID, callback);
    }

    public void getComicComments(String order, long comicID, Callback<CommentResponse> callback) {
        getComicComments(order, DEFAULT_OFFSET, DEFAULT_LIMIT, comicID, callback);
    }

    public void getHotComments(long id, Callback<CommentResponse> callback){
        kkService.getHotComments(id, callback);
    }

    public void likeComic(long id, Callback<EmptyDataResponse> callback) {
        kkService.likeComic(id, callback);
    }

    public void disLikeComic(long id, Callback<EmptyDataResponse> callback) {
        kkService.disLikeComic(id, callback);
    }

    public void getFavComics(int offset, int limit, Callback<FavComicResponse> callback) {
        kkService.getFavComics(offset, limit, callback);
    }

    public void getFavTopics(int offset, int limit, Callback<TopicListResponse> callback) {
        kkService.getFavTopics(offset, limit, callback);
    }

    public void addFavComic(long id, Callback<EmptyDataResponse> callback) {
        kkService.addFavComic(id, callback);
    }

    public void delFavComic(long id, Callback<EmptyDataResponse> callback) {
        kkService.delFavComic(id, callback);
    }

    public void addFavTopic(long id, Callback<EmptyDataResponse> callback) {
        kkService.addFavTopic(id, callback);
    }

    public void delFavTopic(long id, Callback<EmptyDataResponse> callback) {
        kkService.delFavTopic(id, callback);
    }

    public void getWeiboUserInfo(String uid, Callback<WeiboUser> callback) {
        wbService.getUserInfo(uid, callback);
    }

    public void postSignup(String oauth_provider,
                           String oauth_uid,
                           String oauth_token,
                           String nickname,
                           String avatar_url,
                           Callback<SignUserInfo> callback) {
        kkService.signup(oauth_provider, oauth_uid, oauth_token, nickname, avatar_url, callback);
    }

    public void postSignin(String oauth_provider,
                           String oauth_uid,
                           String oauth_token,
                           Callback<SignUserInfo> callback) {
        kkService.signin(oauth_provider, oauth_uid, oauth_token, callback);
    }

    public void postComment(long comic_id, String content, Callback<Comment> callback) {
        kkService.postComment(comic_id, content, callback);
    }

    public void shareComic(String status, String url, Callback<ShareComicResponse> callback) {
        wbService.shareComic(status, url, callback);
    }

    public void sendFeedback(String content, String device, String os, String resolution, String version, String contact, Callback<FeedbackResponse> callback) {
        kkService.postFeedback(content, device, os, resolution, version, contact, callback);
    }

    public void getUserDetail(long id, Callback<User> callback) {
        kkService.getUserDetail(id, callback);
    }

    public void recComic(long id, Callback<EmptyDataResponse> callback) {
        kkService.recComic(id, callback);
    }

    public void getRecentTopics(int offset, int limit , Callback<TopicListResponse> callback) {
        kkService.getRecentTopics(offset, limit, callback);
    }

    public void getRecommendTopics(int offset, int limit , Callback<TopicListResponse> callback) {
        kkService.getRecommendTopics(offset, limit, callback);
    }

    public void sendCode( String phone, String reason,
                         Callback<EmptyDataResponse> callback){
        kkService.sendCode(phone,reason, callback);
    }

    public void phoneVerify( String phone, String code,
                          Callback<EmptyDataResponse> callback){
        kkService.phoneVerify(phone,code, callback);
    }
    public void phoneSignup( String nickname, String password,
                          Callback<EmptyDataResponse> callback){
        kkService.phoneSignup(nickname,password, callback);
    }
    public void phoneSignin( String phone, String password,
                          Callback<SignUserInfo> callback){
        kkService.phoneSignin(phone,password, callback);
    }

    public void search( String keyword, int offset, int limit,
                             Callback<EmptyDataResponse> callback){
        kkService.search(keyword,offset,limit, callback);
    }

    public void resetPwd(String password, Callback<EmptyDataResponse> callback){
        kkService.resetPwd(password, callback);
    }

    public void getBanners( Callback<BannerResponse> callback){
        kkService.getBanners( callback);
    }

    public void getSuggestionTag(Callback<SuggestionListResponse> callback){
        kkService.getSuggestionTag(callback);
    }

    public void searchTopic(String keyword, int offset, int limit, Callback<TopicListResponse> callback){
        kkService.searchTopic(keyword, offset, limit, callback);
    }

    public void likeComment(long id, Callback<EmptyDataResponse> emptyDataResponse){
        kkService.likeComment(id, emptyDataResponse);
    }

    public void disLikeComment(long id, Callback<EmptyDataResponse> emptyDataResponse){
        kkService.disLikeComment(id, emptyDataResponse);
    }

    public void checkUpdate(Callback<VersionResponse> versionResponse){
        kkService.checkUpdate(versionResponse);
    }

    public void getMixedTopicLists(Callback<MixTopicResponse> callback){
        kkService.getMixedTopicLists(callback);
    }

    public void getMixTopics(String action, int offset,Callback<TopicListResponse> callback){
        kkService.getMixTopics(action, offset, callback);
    }

    public void getMixComics(String action, int offset, Callback<ComicResponse> callback){
        kkService.getMixComics(action, offset, callback);
    }

    public void getConfig(Callback<ConfigResponse> callback){
        kkService.getConfig(callback);
    }

    public void getFavTimelineUnread(Callback<FavUnreadResponse> callback){
        kkService.getFavTimelineUnread(callback);
    }


    /**
     * @param channel
     * 1：微博
     2：微信好友
     3：微信朋友圈
     4：QQ
     5：Qzone
     * @param id
     * @param callback
     */
    public void statisticForward(long id, int channel, Callback<EmptyDataResponse> callback){
        kkService.statisticForward(id, channel, callback);
    }

    public void getRecommendAppTop(Callback<RecommendAppResponse> callback){
        kkService.getRecommendAppTop(callback);
    }

    public void getRecommendAppList(Callback<RecommendAppResponse> callback){
        kkService.getRecommendAppList(callback);
    }

    public void updateAccount(String nickname, File file,
                              Callback<EditProfileResponse> callback){
        String mimeType = "image/jpeg";
        if (file != null) {
            kkService.updateAccount(nickname, new TypedFile(mimeType, file), callback );
        }else{
            kkService.updateAccount(nickname, null, callback );
        }

    }

    interface KKService {
        @GET("/comic_lists/1")
        public void getComicLists(@Query("offset") int offset, @Query("limit") int limit, Callback<ComicResponse> callback);

        @GET("/fav/timeline")
        public void getFavTimeline(@Query("since") int since, Callback<FavTimelineResponse> callback);

        @GET("/comics/{comic_id}")
        public void getComicDetail(@Path("comic_id") long id, Callback<ComicDetailResponse> callback);

        @GET("/comics/{comic_id}/comments")
        public void getComicComments(@Query("order") String order, @Query("offset") int offset, @Query("limit") int limit, @Path("comic_id") long id, Callback<CommentResponse> callback);

        @GET("/comics/{comic_id}/hot_comments")
        public void getHotComments(@Path("comic_id") long id, Callback<CommentResponse> callback);

        @GET("/fav/topics/autoadd")
        public void autoAddAttention(Callback<EmptyDataResponse> callback);

        @GET("/{action}")
        public void getMixTopics(@Path("action") String action, @Query("offset") int offset, Callback<TopicListResponse> callback);

        @GET("/{action}")
        public void getMixComics(@Path("action") String action,@Query("offset") int offset,  Callback<ComicResponse> callback);

        @GET("/topics")
        public void getTopics(@Query("offset") int offset, @Query("limit") int limit, Callback<TopicListResponse> callback);

        @GET("/topics")
        public void getTagTopics(@Query("offset") int offset, @Query("limit") int limit,@Query("tag") String tag , Callback<TopicListResponse> callback);

        @GET("/topic_lists/1")
        public void  getHotTopics(@Query("offset") int offset, @Query("limit") int limit, Callback<HotTopicResponse> callback);

        @GET("/topic_lists/2")
        public void getRecentTopics(@Query("offset") int offset, @Query("limit") int limit, Callback<TopicListResponse> callback);

        @GET("/topic_lists/3")
        public void getRecommendTopics(@Query("offset") int offset, @Query("limit") int limit, Callback<TopicListResponse> callback);

        @GET("/topics/{topic_id}")
        public void getTopicDetail(@Path("topic_id") long id, @Query("sort") int sort, Callback<TopicDetail> callback);

        @GET("/users/{user_id}")
        public void getUserDetail(@Path("user_id") long id, Callback<User> callback);

        @GET("/fav/comics")
        public void getFavComics(@Query("offset") int offset, @Query("limit") int limit, Callback<FavComicResponse> callback);

        @GET("/fav/topics")
        public void getFavTopics(@Query("offset") int offset, @Query("limit") int limit, Callback<TopicListResponse> callback);

        @GET("/topics/search")
        public void search(@Query("keyword") String keyword, @Query("offset") int offset, @Query("limit") int limit, Callback<EmptyDataResponse> callback);

        @GET("/banners")
        public void getBanners( Callback<BannerResponse> callback);

        @POST("/comics/{comic_id}/like")
        public void likeComic(@Path("comic_id") long id, Callback<EmptyDataResponse> callback);

        @DELETE("/comics/{comic_id}/like")
        public void disLikeComic(@Path("comic_id") long id, Callback<EmptyDataResponse> callback);

        @POST("/comics/{comic_id}/fav")
        public void addFavComic(@Path("comic_id") long id, Callback<EmptyDataResponse> callback);

        @DELETE("/comics/{comic_id}/fav")
        public void delFavComic(@Path("comic_id") long id, Callback<EmptyDataResponse> callback);

        @POST("/topics/{topic_id}/fav")
        public void addFavTopic(@Path("topic_id") long id, Callback<EmptyDataResponse> callback);

        @DELETE("/topics/{topic_id}/fav")
        public void delFavTopic(@Path("topic_id") long id, Callback<EmptyDataResponse> callback);

        @Multipart
        @POST("/comics/{comic_id}/rec")
        public void recComic(@Path("comic_id") long id, Callback<EmptyDataResponse> callback);

        @GET("/tag_suggestion")
        public void getSuggestionTag(Callback<SuggestionListResponse> callback);

        @Multipart
        @POST("/comics/{comic_id}/shared")
        public void statisticForward(@Path("comic_id") long id, @Part("channel") int channel, Callback<EmptyDataResponse> callback);

        @GET("/topics/search")
        public void searchTopic(@Query("keyword") String keyword, @Query("offset") int offset, @Query("limit") int limit, Callback<TopicListResponse> callback);

        @Multipart
        @POST("/oauth/signup")
        public void signup(@Part("oauth_provider") String oauth_provider,
                           @Part("oauth_uid") String oauth_uid,
                           @Part("oauth_token") String oauth_token,
                           @Part("nickname") String nickname,
                           @Part("avatar_url") String avatar_url,
                           Callback<SignUserInfo> callback);

        @Multipart
        @POST("/oauth/signin")
        public void signin(@Part("oauth_provider") String oauth_provider,
                           @Part("oauth_uid") String oauth_uid,
                           @Part("oauth_token") String oauth_token,
                           Callback<SignUserInfo> callback);

        @Multipart
        @POST("/comics/{comic_id}/comments")
        public void postComment(@Path("comic_id") long id, @Part("content") String content, Callback<Comment> commentCallback);

        @POST("/comments/{comment_id}/like")
        public void likeComment(@Path("comment_id") long id, Callback<EmptyDataResponse> emptyDataResponse);

        @DELETE("/comments/{comment_id}/like")
        public void disLikeComment(@Path("comment_id") long id, Callback<EmptyDataResponse> emptyDataResponse);

        @Multipart
        @POST("/feedbacks")
        public void postFeedback(@Part("content") String content,
                                 @Part("device") String device,
                                 @Part("os") String os,
                                 @Part("resolution") String resolution,
                                 @Part("version") String version,
                                 @Part("contact") String contact,
                                 Callback<FeedbackResponse> callback);

        @Multipart
        @POST("/phone/send_code")
        public void sendCode(@Part("phone") String phone,
                                 @Part("reason") String reason,
                                 Callback<EmptyDataResponse> callback);

        @Multipart
        @POST("/phone/verify")
        public void phoneVerify(@Part("phone") String phone,
                             @Part("code") String code,
                             Callback<EmptyDataResponse> callback);

        @Multipart
        @POST("/phone/signup")
        public void phoneSignup(@Part("nickname") String nickname,
                                @Part("password") String password,
                                Callback<EmptyDataResponse> callback);

        @Multipart
        @POST("/phone/signin")
        public void phoneSignin(@Part("phone") String phone,
                                @Part("password") String password,
                                Callback<SignUserInfo> callback);
        @Multipart
        @POST("/account/reset_password/by_phone")
        public void resetPwd(@Part("password") String password,
                                Callback<EmptyDataResponse> callback);

        @Multipart
        @POST("/account/update")
        public void updateAccount(@Part("nickname") String nickname,@Part("avatar") TypedFile avatar,
                             Callback<EditProfileResponse> callback);

        @GET("/chkv")
        public void checkUpdate(Callback<VersionResponse> callback);

        @GET("/topic_lists/mixed")
        public void getMixedTopicLists(Callback<MixTopicResponse> callback);

        @GET("/apprec/top")
        public void getRecommendAppTop(Callback<RecommendAppResponse> callback);

        @GET("/apprec/list")
        public void getRecommendAppList(Callback<RecommendAppResponse> callback);

        @GET("/config")
        public void getConfig(Callback<ConfigResponse> callback);

        @GET("/fav/timeline/unread")
        public void getFavTimelineUnread(Callback<FavUnreadResponse> callback);

    }

    interface WBService {
        @GET("/users/show.json")
        public void getUserInfo(@Query("uid") String uid, Callback<WeiboUser> callback);

        @FormUrlEncoded
        @POST("/statuses/upload_url_text.json")
        public void shareComic(@Field("status") String statue,
                               @Field("url") String imageURL,
                               Callback<ShareComicResponse> callback);
    }

    class ItemTypeAdapterFactory implements TypeAdapterFactory {
        public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
            return new TypeAdapter<T>() {
                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                public T read(JsonReader in) throws IOException {
                    JsonElement jsonElement = elementAdapter.read(in);
                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        if (jsonObject.has("data") && jsonObject.get("data").isJsonObject()) {
                            jsonElement = jsonObject.get("data");
                        }
                    }
                    return delegate.fromJsonTree(jsonElement);
                }
            }.nullSafe();
        }
    }

    class WeiboOauthRequestInterceptor implements RequestInterceptor {
        String accessToken;

        public WeiboOauthRequestInterceptor(String token) {
            this.accessToken = token;
        }

        @Override
        public void intercept(RequestFacade request) {
            if (accessToken != null) {
                request.addQueryParam("access_token", accessToken);
            }
        }
    }

    class KKRequestInterceptor implements RequestInterceptor {
        String session;
        String uuid;
        String user_agent;

        public KKRequestInterceptor(String userAgent, String cookieSession, String uuid) {
            this.session = cookieSession;
            this.uuid = uuid;
            this.user_agent = userAgent;
        }

        @Override
        public void intercept(RequestFacade request) {
            if (session != null) {
                request.addHeader("Cookie", session);
            }
            if (uuid != null) {
                request.addHeader("X-Device", uuid);
            }
            if(!TextUtils.isEmpty(user_agent)){
                request.addHeader("User-Agent", user_agent);
            }
        }
    }
}
