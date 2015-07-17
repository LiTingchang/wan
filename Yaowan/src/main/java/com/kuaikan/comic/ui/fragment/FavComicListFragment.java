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
import com.kuaikan.comic.rest.model.API.FavComicResponse;
import com.kuaikan.comic.rest.model.Comic;
import com.kuaikan.comic.ui.ComicDetailActivity;
import com.kuaikan.comic.ui.adapter.FavComicListAdapter;
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
public class FavComicListFragment extends Fragment {
    public static final String TAG = FavComicListFragment.class.getSimpleName();
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    LinearLayoutManager mLayoutManager;
    FavComicListAdapter favComicListAdapter;
    List<Comic> comicList = new ArrayList<>();
    ShowDeleteFavDialogFragment showDelComicDialogFragment;

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            initData();
        }
    };

    public static FavComicListFragment newInstance() {
        FavComicListFragment favTabFragment = new FavComicListFragment();
        return favTabFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav_comic_list, container, false);
        ButterKnife.inject(this, view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
//
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_primary));
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        favComicListAdapter = new FavComicListAdapter(getActivity(), comicList, new ListRefreshListener() {
            @Override
            public void onLoadMoreItem(int newCurrentOffset) {
                loadData(newCurrentOffset, RestClient.DEFAULT_LIMIT);
            }
        }, new FavComicListAdapter.FavComicLongClickListener() {
            @Override
            public boolean onLongClick(final int position) {
                showDelComicDialogFragment = ShowDeleteFavDialogFragment.newInstance();
                showDelComicDialogFragment.setAlertDialogClickListener(new ShowDeleteFavDialogFragment.AlertDialogClickListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        delFavComic(position);
                    }

                    @Override
                    public void onNegativeButtonClick() {
                    }
                });
                showDelComicDialogFragment.show(getChildFragmentManager().beginTransaction(), "del_comic");
                return false;
            }
        }, new FavComicListAdapter.FavComicClickListener() {
            @Override
            public void onClick(int position) {
                Comic comic = favComicListAdapter.getObject(position);
                Intent intent = new Intent(getActivity(), ComicDetailActivity.class);
                intent.putExtra("comic_id", comic.getId());
                intent.putExtra("comic_title", comic.getTitle());
                getActivity().startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(favComicListAdapter);
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
            favComicListAdapter.clear();
        }
        favComicListAdapter.setCurrentListItemOffset(RestClient.DEFAULT_OFFSET);
    }

    private void loadData(final int offset, int limit) {
        KKMHApp.getRestClient().getFavComics(offset, limit, new Callback<FavComicResponse>() {
            @Override
            public void success(FavComicResponse favTopicResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                    return;
                }
                if(favTopicResponse != null && favTopicResponse.getFavComicList().size() > 0){
                    if (offset == RestClient.DEFAULT_OFFSET) {
                        favComicListAdapter.refreshList(favTopicResponse.getFavComicList());
                        JsonSD.writeJsonToFile(JsonSD.CATEGORY.FAV_COMIC, favTopicResponse.toJSON());
                    } else {
                        favComicListAdapter.addAll(favTopicResponse.getFavComicList());
                    }
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                //读取缓存数据
                RetrofitErrorUtil.handleError(getActivity(), error);
                String comicJson = JsonSD.getJsonFromFile(JsonSD.CATEGORY.FAV_COMIC);
                if(offset == RestClient.DEFAULT_OFFSET && !TextUtils.isEmpty(comicJson)){
                    favComicListAdapter.refreshList(GsonUtil.fromJson(comicJson, FavComicResponse.class).getFavComicList());
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void delFavComic(final int favTopicListPosition) {
        makeToast("删除中...");
        KKMHApp.getRestClient().delFavComic(favComicListAdapter.getObject(favTopicListPosition).getId(), new Callback<EmptyDataResponse>() {
            @Override
            public void success(EmptyDataResponse emptyDataResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                    return;
                }
                makeToast("删除成功");
                favComicListAdapter.delete(favTopicListPosition);
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
