package com.kuaikan.comic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.TopicDetail;
import com.kuaikan.comic.ui.adapter.TopicDetailComicListAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by liuchao1 on 15/5/12.
 */
public class TopicDetailComicListFragment extends Fragment {

    public static final String TAG = TopicDetailComicListFragment.class.getSimpleName();
    LinearLayoutManager mLayoutManager;
    TopicDetailComicListAdapter mTopicDetailAdapter;
    TopicDetail mTopicDetail = new TopicDetail();

    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    public TopicDetailComicListFragment(){}

    public TopicDetailComicListFragment(TopicDetail topicDetail){
        mTopicDetail = topicDetail;
    }

    public static TopicDetailComicListFragment newInstance(TopicDetail topicDetail) {
        TopicDetailComicListFragment topicDetailComicListFragment = new TopicDetailComicListFragment(topicDetail);
        return topicDetailComicListFragment;
    }

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
        mTopicDetailAdapter = new TopicDetailComicListAdapter(getActivity(), mTopicDetail);
        mRecyclerView.setAdapter(mTopicDetailAdapter);
        return view;
    }



}
