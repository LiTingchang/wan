package com.kuaikan.comic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.RestClient;
import com.kuaikan.comic.rest.model.API.ComicResponse;
import com.kuaikan.comic.rest.model.API.HotTopicResponse;
import com.kuaikan.comic.rest.model.API.TopicListResponse;
import com.kuaikan.comic.ui.TopicListActivity;
import com.kuaikan.comic.ui.adapter.TopicListAdapter;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by skyfishjy on 12/28/14.
 */
public class TopicListFragment extends Fragment {

    private String mSearchStr;
    private int mType;
    private String mAction;

    public static final String TAG = TopicListFragment.class.getSimpleName();
    LinearLayoutManager mLayoutManager;
    TopicListAdapter mTopicListAdapter;
    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    public static TopicListFragment newInstance(int type, String searchTag, String action) {
        TopicListFragment topicListFragment = new TopicListFragment();
        topicListFragment.setType(type);
        topicListFragment.setSearchTag(searchTag);
        topicListFragment.setAction(action);
        return topicListFragment;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public void setSearchTag(String mSearchTag) {
        this.mSearchStr = mSearchTag;
    }

    public void setAction(String action) { this.mAction = action;};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic_detail_comic_list, container, false);
        ButterKnife.inject(this, view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mTopicListAdapter = new TopicListAdapter(getActivity(), new TopicListAdapter.TopicRefreshListener() {
            @Override
            public void onLoadMoreTopic(int newCurrentOffset) {
                loadData(newCurrentOffset, 20);
            }
        });
        mRecyclerView.setAdapter(mTopicListAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData(RestClient.DEFAULT_OFFSET, 20);
        Timber.tag(TopicListFragment.class.getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("专题列表");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("专题列表");
    }

    private void loadData(final int offset,final int limit) {

        switch(mType){
            case TopicListActivity.TOPIC_LIST_TYPE_COMIC:
                KKMHApp.getRestClient().getMixComics(mAction, offset, new Callback<ComicResponse>() {
                    @Override
                    public void success(ComicResponse comicResponse, Response response) {
                        if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                            return;
                        }
                        if (offset == 0) {
                            if (comicResponse != null && comicResponse.getComicList().size() > 0) {
                                mTopicListAdapter.initComicData(comicResponse.getComicList());
                            } else {
                                mTopicListAdapter.initEmptyData();
                            }

                        } else {
                            if (comicResponse != null) {
                                mTopicListAdapter.addAllComic(comicResponse.getComicList(), false);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mTopicListAdapter.initEmptyData();
                        RetrofitErrorUtil.handleError(getActivity(), error);
                    }
                });
                break;

            case TopicListActivity.TOPIC_LIST_TYPE_TOPIC:
                KKMHApp.getRestClient().getMixTopics(mAction, offset,new Callback<TopicListResponse>() {
                    @Override
                    public void success(TopicListResponse topicListResponse, Response response) {
                        if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                            return;
                        }
                        if (offset == 0) {
                            if (topicListResponse != null && topicListResponse.getTopics().size() > 0) {
                                mTopicListAdapter.initTopicData(topicListResponse.getTopics());
                            } else {
                                mTopicListAdapter.initEmptyData();
                            }

                        } else {
                            if (topicListResponse != null) {
                                mTopicListAdapter.addAllTopic(topicListResponse.getTopics(), false);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mTopicListAdapter.initEmptyData();
                        RetrofitErrorUtil.handleError(getActivity(), error);
                    }
                });
                break;
            case TopicListActivity.TOPIC_LIST_TYPE_TAG:
                KKMHApp.getRestClient().getTagTopics(offset, RestClient.DEFAULT_SEARCH_LIMIT, mSearchStr, new Callback <TopicListResponse> ()
                {
                    @Override
                    public void success(TopicListResponse topicListResponse, Response response) {
                        if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                            return;
                        }
                        if(offset == 0){
                            if(topicListResponse.getTopics() != null & topicListResponse.getTopics().size() > 0){
                                mTopicListAdapter.initTopicData(topicListResponse.getTopics());
                            }else{
                                mTopicListAdapter.initEmptyData();
                            }
                        }else{
                            if(topicListResponse != null){
                                mTopicListAdapter.addAllTopic(topicListResponse.getTopics(), false);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mTopicListAdapter.initEmptyData();
                        RetrofitErrorUtil.handleError(getActivity(), error);
                    }
                });
                break;

            case TopicListActivity.TOPIC_LIST_TYPE_SEARCH:
                KKMHApp.getRestClient().searchTopic(mSearchStr,offset, RestClient.DEFAULT_SEARCH_LIMIT, new Callback <TopicListResponse> ()
                {
                    @Override
                    public void success(TopicListResponse topicListResponse, Response response) {
                        if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                            return;
                        }
                        if(offset == 0){
                            if(topicListResponse.getTopics() != null & topicListResponse.getTopics().size() > 0){
                                mTopicListAdapter.initTopicData(topicListResponse.getTopics());
                            }else{
                                mTopicListAdapter.initEmptyData();
                            }
                        }else{
                            if(topicListResponse != null){
                                mTopicListAdapter.addAllTopic(topicListResponse.getTopics(), false);
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mTopicListAdapter.initEmptyData();
                        RetrofitErrorUtil.handleError(getActivity(), error);
                    }
                });
                break;
//            case TopicListActivity.TOPIC_LIST_TYPE_HOT:
            default:
                KKMHApp.getRestClient().getHotTopicLists(offset,limit,new Callback<HotTopicResponse>() {
                    @Override
                    public void success(HotTopicResponse hotTopicResponse, Response response) {
                        if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                            return;
                        }
                        if(offset == 0){
                            if(hotTopicResponse != null && hotTopicResponse.getTopics().size() > 0){
                                mTopicListAdapter.initTopicData(hotTopicResponse.getTopics());
                            }else{
                                mTopicListAdapter.initEmptyData();
                            }

                        }else{
                            if(hotTopicResponse != null){
                                mTopicListAdapter.addAllTopic(hotTopicResponse.getTopics(), false);
                            }
                        }
                    }

                    @Override
                    public void failure (RetrofitError error){
                        mTopicListAdapter.initEmptyData();
                        RetrofitErrorUtil.handleError(getActivity(), error);
                    }
                });
                break;
        }

    }


    private void makeToast(String toast) {
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
    }
}
