package com.kuaikan.comic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.db.JsonSD;
import com.kuaikan.comic.rest.model.API.BannerResponse;
import com.kuaikan.comic.rest.model.API.MixTopicResponse;
import com.kuaikan.comic.rest.model.Banner;
import com.kuaikan.comic.rest.model.MixTopic;
import com.kuaikan.comic.ui.TopicListActivity;
import com.kuaikan.comic.ui.adapter.TopicTabListAdapter;
import com.kuaikan.comic.util.GsonUtil;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by skyfishjy on 12/18/14.
 */
public class MainTabTopicFragment extends Fragment {

    public static final String TAG = MainTabTopicFragment.class.getSimpleName();

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    LinearLayoutManager linearLayoutManager;
    TopicTabListAdapter topicTabListAdapter;
    List<MixTopic> mMixTopics;
    List<Banner> banners = new ArrayList<>();
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            initData();
        }
    };

    public static MainTabTopicFragment newInstance() {
        MainTabTopicFragment tabTopicFragment = new MainTabTopicFragment();
        return tabTopicFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_topic, container, false);
        ButterKnife.inject(this, view);

        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_primary));
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        try {
            Field f = null;
            f = SwipeRefreshLayout.class.getDeclaredField("mTouchSlop");
            f.setAccessible(true);
            f.set(mSwipeRefreshLayout,150);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        topicTabListAdapter = new TopicTabListAdapter(getActivity(),mMixTopics,banners);

        mRecyclerView.setAdapter(topicTabListAdapter);
//        mRecyclerView.addItemDecoration(new FindTabDivider(getActivity(),
//                FindTabDivider.VERTICAL_LIST));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        Timber.tag(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("所有专题Tab");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("所有专题Tab");
    }

    private void initData() {

        KKMHApp.getRestClient().getMixedTopicLists(new Callback<MixTopicResponse>() {
            @Override
            public void success(MixTopicResponse mixTopicResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                    return;
                }
                mMixTopics = removeInvalidValues(mixTopicResponse.getTopics());
                JsonSD.writeJsonToFile(JsonSD.CATEGORY.MAIN_TAB_TOPIC_LIST, mixTopicResponse.toJSON());
                loadBanners();
            }

            @Override
            public void failure(RetrofitError error) {
                //读取缓存数据
                RetrofitErrorUtil.handleError(getActivity(), error);
                String mixTopicJson = JsonSD.getJsonFromFile(JsonSD.CATEGORY.MAIN_TAB_TOPIC_LIST);
                if (!TextUtils.isEmpty(mixTopicJson)) {
                    mMixTopics = removeInvalidValues(GsonUtil.fromJson(mixTopicJson, MixTopicResponse.class).getTopics());
                    loadBanners();
                }
            }
        });
    }


    private List<MixTopic> removeInvalidValues(List<MixTopic> topics){
        List<MixTopic> result = new ArrayList<>();
        for(MixTopic mixTopic : topics){
            if(mixTopic.getType() == TopicListActivity.TOPIC_LIST_TYPE_TOPIC){
                if(mixTopic.getTopics() != null && mixTopic.getTopics().size() > 0){
                    result.add(mixTopic);
                }
            }else if(mixTopic.getType() == TopicListActivity.TOPIC_LIST_TYPE_COMIC) {
                if(mixTopic.getComics() != null && mixTopic.getComics().size() > 0){
                    result.add(mixTopic);
                }
            }
        }
        return result;
    }

    private void loadBanners() {
        KKMHApp.getRestClient().getBanners(new Callback<BannerResponse>() {
            @Override
            public void success(BannerResponse bannerResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                    return;
                }
//                dayRecomComicListAdapter.refresh(bannerResponse.getBannerGroup());
                JsonSD.writeJsonToFile(JsonSD.CATEGORY.MAIN_TAB_BANNERS, bannerResponse.toJSON());
                topicTabListAdapter.refreshDataList(mMixTopics, bannerResponse.getBannerGroup());
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                //读取缓存数据
                RetrofitErrorUtil.handleError(getActivity(), error);
                String bannersJson = JsonSD.getJsonFromFile(JsonSD.CATEGORY.MAIN_TAB_BANNERS);
                if(!TextUtils.isEmpty(bannersJson)){
                    topicTabListAdapter.refreshDataList(mMixTopics, GsonUtil.fromJson(bannersJson, BannerResponse.class).getBannerGroup());
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}
