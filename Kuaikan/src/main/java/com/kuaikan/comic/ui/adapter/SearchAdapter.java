package com.kuaikan.comic.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.Topic;
import com.kuaikan.comic.ui.TopicDetailActivity;
import com.kuaikan.comic.ui.TopicListActivity;
import com.kuaikan.comic.ui.view.FlowLayout;
import com.kuaikan.comic.util.UIUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by a on 2015/4/8.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<String> mHistorys;

    List<String> mCategorys;

    List<Topic> mTopics;

    private static final int VIEW_TYPE_HEADER = 1001;
    private static final int VIEW_TYPE_HISTORY = 1002;
    private static final int VIEW_TYPE_RESULT = 1003;
    private static final int VIEW_TYPE_CLEAR = 1004;
    private static final int VIEW_TYPE_EMPTY = 1005;

    private VIEW_MODE mCurrentMode = VIEW_MODE.HISTORY_MODE;

    public Context mContext;

    public SearchAdapter(Context context) {
        this.mContext = context;
    }

    private enum VIEW_MODE {
        HISTORY_MODE,
        SEARCH_RESULT_MODE,
        EMPTY_MODE
    }

    public void initData(List<String> categorys, List<String> historys) {
        mCurrentMode = VIEW_MODE.HISTORY_MODE;
        this.mCategorys = categorys;
        this.mHistorys = historys;
        notifyDataSetChanged();
    }

    public void initEmptyData(){
        mCurrentMode = VIEW_MODE.EMPTY_MODE;
        notifyDataSetChanged();
    }

    public void initResultData(List<Topic> topics) {
        mCurrentMode = VIEW_MODE.SEARCH_RESULT_MODE;
        this.mTopics = topics;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new CategoryViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.view_search_category, parent, false));
            case VIEW_TYPE_HISTORY:
                return new HistoryViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.search_history_item_view, parent, false));
            case VIEW_TYPE_CLEAR:
                return new ClearViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.view_clear, parent, false));
            case VIEW_TYPE_EMPTY:
                return new TopicListAdapter.EmptyContentViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.view_empty_content, parent, false));
            case VIEW_TYPE_RESULT:
            default:
                return new ResultViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.listitem_fav_topic, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (mCurrentMode == VIEW_MODE.HISTORY_MODE) {
            if (position == 0) {
                CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
//                categoryViewHolder.mCategoryRV.setHasFixedSize(true);
//                categoryViewHolder.mCategoryRV.setLayoutManager(new GridLayoutManager(mContext, 5));
//                categoryViewHolder.mCategoryRV.setAdapter(new CategoryAdapter());
                if(mCategorys == null || mCategorys.size() == 0){
                    categoryViewHolder.mCategoryRV.setVisibility(View.GONE);
                }else{
                    initGridViewView(categoryViewHolder.mCategoryRV);
                    categoryViewHolder.mCategoryRV.setVisibility(View.VISIBLE);
//                    int height = mContext.getResources().getDimensionPixelSize(R.dimen.view_search_item_height) * ((mCategorys.size()/5) + 1);
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
//                    categoryViewHolder.mCategoryRV.setLayoutParams(layoutParams);
                }
            }else if( position > 0 && position <= mHistorys.size()){
                HistoryViewHolder historyViewHolder = (HistoryViewHolder) holder;
                historyViewHolder.mHistoryItemTv.setText(mHistorys.get(position - 1));
                historyViewHolder.mHistoryDelTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHistorys.remove(position -1);
                        notifyDataSetChanged();
                    }
                });
                historyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, TopicListActivity.class);
                        intent.putExtra(TopicListActivity.TOPIC_LIST_TYPE, TopicListActivity.TOPIC_LIST_TYPE_SEARCH);
                        intent.putExtra(TopicListActivity.TOPIC_LIST_TYPE_SEARCH_STR, mHistorys.get(position - 1));
                        mContext.startActivity(intent);
                    }
                });
            }else{
                ClearViewHolder clearViewHolder =  (ClearViewHolder) holder;
                clearViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHistorys.clear();
                        notifyDataSetChanged();
                    }
                });
            }
        }else if(mCurrentMode == VIEW_MODE.SEARCH_RESULT_MODE){
            ResultViewHolder resultViewHolder = (ResultViewHolder) holder;
            resultViewHolder.mTopicAuthor.setText(mTopics.get(position).getUser().getNickname());
            resultViewHolder.mTopicName.setText(mTopics.get(position).getTitle());
            Picasso.with(mContext)
                    .load(mTopics.get(position).getCover_image_url())
                    .fit()
                    .centerCrop()
                    .transform(UIUtil.roundedTransformation)
                    .into(resultViewHolder.mTopicCover);
            resultViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, TopicDetailActivity.class);
                    intent.putExtra("topic_id", mTopics.get(position).getId());
                    mContext.startActivity(intent);
                }
            });
        }else if(mCurrentMode == VIEW_MODE.EMPTY_MODE){
            TopicListAdapter.EmptyContentViewHolder emptyContentViewHolder = (TopicListAdapter.EmptyContentViewHolder) holder;
        }

    }

    private void initGridViewView(FlowLayout parentLL){
        parentLL.removeAllViews();
        int size = mCategorys.size(); // 添加Button的个数
        for(int i = 0; i < size; i++){
            String item = mCategorys.get(i);
            TextView childBtn = (TextView) LayoutInflater.from(mContext).inflate(R.layout.view_search_category_item, parentLL, false);
            childBtn.setText(item);
            final int position = i;
            childBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, TopicListActivity.class);
                        intent.putExtra(TopicListActivity.TOPIC_LIST_TYPE, TopicListActivity.TOPIC_LIST_TYPE_TAG);
                        intent.putExtra(TopicListActivity.TOPIC_LIST_TYPE_SEARCH_STR, mCategorys.get(position));
                        mContext.startActivity(intent);
                }
            });
            parentLL.addView(childBtn);
        }
        parentLL.invalidate();
    }

    @Override
    public int getItemViewType(int position) {
        switch (mCurrentMode) {
            case HISTORY_MODE:
                if (position == 0) {
                    return VIEW_TYPE_HEADER;
                } else if( position > 0 && position <= mHistorys.size()){
                    return VIEW_TYPE_HISTORY;
                }else {
                    return VIEW_TYPE_CLEAR;
                }
            case SEARCH_RESULT_MODE:
                return VIEW_TYPE_RESULT;
            case EMPTY_MODE:
                return VIEW_TYPE_EMPTY;
            default:
                break;
        }


        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        switch (mCurrentMode) {
            case HISTORY_MODE:
                if(mHistorys != null){
                    if(mHistorys.size() > 0){
                        return mHistorys.size() + 2;
                    }else{
                        return mHistorys.size() + 1;
                    }
                }
                return 1;
            case SEARCH_RESULT_MODE:
                if (mTopics != null) {
                    return mTopics.size();
                } else {
                    return 0;
                }
            case EMPTY_MODE:
                return 1;
        }
        return 0;

    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.search_category_view)
        FlowLayout mCategoryRV;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.search_history_item_tv)
        TextView mHistoryItemTv;
        @InjectView(R.id.search_history_item_del)
        ImageView mHistoryDelTv;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }


    public class ResultViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.topic_cover)
        ImageView mTopicCover;
        @InjectView(R.id.topic_name)
        TextView mTopicName;
        @InjectView(R.id.topic_author)
        TextView mTopicAuthor;

        public ResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public class ClearViewHolder extends RecyclerView.ViewHolder {

        public ClearViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
