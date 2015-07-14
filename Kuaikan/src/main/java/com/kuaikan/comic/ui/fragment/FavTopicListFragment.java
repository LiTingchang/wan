package com.kuaikan.comic.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.db.JsonSD;
import com.kuaikan.comic.rest.RestClient;
import com.kuaikan.comic.rest.model.API.EmptyDataResponse;
import com.kuaikan.comic.rest.model.API.TopicListResponse;
import com.kuaikan.comic.rest.model.Topic;
import com.kuaikan.comic.ui.TopicDetailActivity;
import com.kuaikan.comic.ui.adapter.FavTopicListAdapter;
import com.kuaikan.comic.ui.listener.ListRefreshListener;
import com.kuaikan.comic.util.GsonUtil;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.UserUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by skyfishjy on 2/11/15.
 */
public class FavTopicListFragment extends Fragment {
    public static final String TAG = FavTopicListFragment.class.getSimpleName();
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    LinearLayoutManager mLayoutManager;
    FavTopicListAdapter favTopicListAdapter;
    List<Topic> topicBriefList = new ArrayList<>();
    ShowDeleteFavDialogFragment showDelTopicDialogFragment;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            initData();
        }
    };

    public static FavTopicListFragment newInstance() {
        FavTopicListFragment favTabFragment = new FavTopicListFragment();
        return favTabFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav_topic_list, container, false);
        ButterKnife.inject(this, view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_primary));
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        favTopicListAdapter = new FavTopicListAdapter(getActivity(), topicBriefList, new ListRefreshListener() {
            @Override
            public void onLoadMoreItem(int newCurrentOffset) {
                loadData(newCurrentOffset, RestClient.DEFAULT_LIMIT);
            }
        }, new FavTopicListAdapter.FavTopicLongClickListener() {
            @Override
            public boolean onLongClick(final int position) {
                showDelTopicDialogFragment = ShowDeleteFavDialogFragment.newInstance();
                showDelTopicDialogFragment.setAlertDialogClickListener(new ShowDeleteFavDialogFragment.AlertDialogClickListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        delFavTopic(position);
                    }

                    @Override
                    public void onNegativeButtonClick() {
                    }
                });
                showDelTopicDialogFragment.show(getChildFragmentManager().beginTransaction(), "del_topic");
                return false;
            }
        }, new FavTopicListAdapter.FavTopicClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), TopicDetailActivity.class);
                intent.putExtra("topic_id", favTopicListAdapter.getObject(position).getId());
                startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(favTopicListAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        Timber.tag(TAG);
    }

    private void initData() {
        if(UserUtil.isUserLogin(getActivity())){
            loadData(RestClient.DEFAULT_OFFSET, RestClient.DEFAULT_LIMIT);
        }else{
            favTopicListAdapter.clear();
        }
        favTopicListAdapter.setCurrentListItemOffset(RestClient.DEFAULT_OFFSET);
    }

    private void loadData(final int offset, int limit) {
        KKMHApp.getRestClient().getFavTopics(offset, limit, new Callback<TopicListResponse>() {
            @Override
            public void success(TopicListResponse topicListResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                    return;
                }
                if(topicListResponse != null && topicListResponse.getTopics().size() > 0){
                    if (offset == RestClient.DEFAULT_OFFSET) {
                        favTopicListAdapter.refreshList(topicListResponse.getTopics());
                        JsonSD.writeJsonToFile(JsonSD.CATEGORY.FAV_TOPIC, topicListResponse.toJSON());
                    } else {
                        favTopicListAdapter.addAll(topicListResponse.getTopics());
                    }
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                //读取缓存数据
                RetrofitErrorUtil.handleError(getActivity(), error);
                String topicJson = JsonSD.getJsonFromFile(JsonSD.CATEGORY.FAV_TOPIC);
                if(offset == RestClient.DEFAULT_OFFSET && !TextUtils.isEmpty(topicJson)){
                    favTopicListAdapter.refreshList(GsonUtil.fromJson(topicJson,TopicListResponse.class).getTopics());
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void delFavTopic(final int favTopicListPosition) {
        makeToast("删除中...");
        KKMHApp.getRestClient().delFavTopic(favTopicListAdapter.getObject(favTopicListPosition).getId(), new Callback<EmptyDataResponse>() {
            @Override
            public void success(EmptyDataResponse emptyDataResponse, Response response) {
                makeToast("删除成功");
                favTopicListAdapter.delete(favTopicListPosition);
            }

            @Override
            public void failure(RetrofitError error) {
                makeToast("删除失败");
                RetrofitErrorUtil.handleError(getActivity(), error);
            }
        });
    }

    private void makeToast(String toast) {
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
    }


}
