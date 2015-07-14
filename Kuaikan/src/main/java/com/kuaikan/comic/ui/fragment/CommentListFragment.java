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
import com.kuaikan.comic.rest.model.API.CommentResponse;
import com.kuaikan.comic.rest.model.API.EmptyDataResponse;
import com.kuaikan.comic.ui.adapter.ComicDetailAdapter;
import com.kuaikan.comic.ui.adapter.CommentListAdapter;
import com.kuaikan.comic.util.RetrofitErrorUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentListFragment extends Fragment {

    public static final String TAG = CommentListFragment.class.getSimpleName();


    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    LinearLayoutManager mLayoutManager;

    CommentListAdapter commentListAdapter;

    private COMMENT_TAB mCommentTab;
    private long mComicId;

    public static enum COMMENT_TAB{
        NEWEST,
        HOTEST
    }

    public CommentListFragment(){
    }

    public void setCommentTab(COMMENT_TAB mCommentTab) {
        this.mCommentTab = mCommentTab;
    }

    public void setComicId(long mComicId) {
        this.mComicId = mComicId;
    }

    public static CommentListFragment newInstance(long comicId, COMMENT_TAB comment_tab) {
        CommentListFragment commentListFragment = new CommentListFragment();
        commentListFragment.setComicId(comicId);
        commentListFragment.setCommentTab(comment_tab);
        return commentListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_list, container, false);
        ButterKnife.inject(this, view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        commentListAdapter = new CommentListAdapter(getActivity(), new ComicDetailAdapter.CommentLikeListener() {
            @Override
            public void onCommentLike(long comment_id, boolean like) {
                likeComment(comment_id, like);
            }
        }, new CommentListAdapter.CommentRefreshListener() {
            @Override
            public void onLoadMoreComment(int newCurrentOffset) {
                loadData(newCurrentOffset, RestClient.DEFAULT_LIMIT, false);
            }
        });

        mRecyclerView.setAdapter(commentListAdapter);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData(RestClient.DEFAULT_OFFSET, RestClient.DEFAULT_LIMIT, true);
        Timber.tag(TAG);
    }

    public void refreshData(){
        loadData(RestClient.DEFAULT_OFFSET, RestClient.DEFAULT_LIMIT, true);
    }

    private void loadData( int offset, int limit,final boolean isRefresh) {
        String order = "";
        if(mCommentTab == COMMENT_TAB.HOTEST){
            order = "score";
        }
        KKMHApp.getRestClient().getComicComments(order,offset,limit, mComicId, new Callback<CommentResponse>() {
            @Override
            public void success(CommentResponse commentResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                    return;
                }
                if(isRefresh){
                    commentListAdapter.refreshList(commentResponse.getComments());
                }else{
                    commentListAdapter.addAll(commentResponse.getComments());
                }

            }

            @Override
            public void failure(RetrofitError error) {
                RetrofitErrorUtil.handleError(getActivity(), error);
            }
        });

    }


    private void likeComment(long comment_id, boolean like) {
        if(like){
            KKMHApp.getRestClient().likeComment(comment_id, new Callback<EmptyDataResponse>() {
                @Override
                public void success(EmptyDataResponse emptyDataResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                        return;
                    }
                    Toast.makeText(getActivity(), "赞成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    RetrofitErrorUtil.handleError(getActivity(), error);
                }
            });
        }else{
            KKMHApp.getRestClient().disLikeComment(comment_id, new Callback<EmptyDataResponse>() {
                @Override
                public void success(EmptyDataResponse emptyDataResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(getActivity(), response)){
                        return;
                    }
                    Toast.makeText(getActivity(), "取消赞成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    RetrofitErrorUtil.handleError(getActivity(), error);
                }
            });
        }

    }


}
