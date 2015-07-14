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
import com.kuaikan.comic.rest.RestClient;
import com.kuaikan.comic.rest.model.Comic;
import com.kuaikan.comic.rest.model.Topic;
import com.kuaikan.comic.ui.ComicDetailActivity;
import com.kuaikan.comic.ui.TopicDetailActivity;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by skyfishjy on 12/28/14.
 */
public class TopicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Topic> mTopicList = new ArrayList<>();
    private List<Comic> mComicList = new ArrayList<>();
    Context mContext;

    Transformation roundedTransformation;
    TopicRefreshListener mTopicRefreshListener;
    int currentTopicOffset;

    private static final int CONTEXT_TYPE_TOPIC_LIST = 1001;

    private static final int CONTEXT_TYPE_EMPTY_LIST = 1002;

    private static final int CONTEXT_TYPE_COMIC_LIST = 1003;

    private int mCurrentTpye = CONTEXT_TYPE_TOPIC_LIST;

    public TopicListAdapter(Context context, TopicRefreshListener topicRefreshListener) {
        this.mContext = context;
        this.mTopicRefreshListener = topicRefreshListener;
        this.currentTopicOffset = RestClient.DEFAULT_SEARCH_LIMIT;

        roundedTransformation = new RoundedTransformationBuilder()
                .borderWidthDp(0)
                .cornerRadius(8)
                .oval(false)
                .build();
    }

    public void initTopicData( List<Topic> topics) {
        mCurrentTpye = CONTEXT_TYPE_TOPIC_LIST;
        addAllTopic(topics, true);
    }

    public void initComicData( List<Comic> comics) {
        mCurrentTpye = CONTEXT_TYPE_COMIC_LIST;
        addAllComic(comics, true);
    }

    public void initEmptyData() {
        mCurrentTpye = CONTEXT_TYPE_EMPTY_LIST;
        notifyDataSetChanged();
    }

    public void addAllTopic(List<Topic> list, boolean isRefresh) {
        int startIndex = mTopicList.size();
        if (isRefresh) {
            mTopicList = list;
            notifyDataSetChanged();
        } else {
            mTopicList.addAll(list);
            notifyItemRangeInserted(startIndex, list.size());
        }
    }

    public void addAllComic(List<Comic> list, boolean isRefresh) {
        int startIndex = mComicList.size();
        if (isRefresh) {
            mComicList = list;
            notifyDataSetChanged();
        } else {
            mComicList.addAll(list);
            notifyItemRangeInserted(startIndex, list.size());
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CONTEXT_TYPE_TOPIC_LIST) {
            return new TopicHorizontalViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.listitem_fav_topic, parent, false));//listitem_topic_in_topic_list, parent, false));
        }else if (viewType == CONTEXT_TYPE_COMIC_LIST) {
            return new ComicHorizontalViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.listitem_fav_comic, parent, false));
        }else{
            return new EmptyContentViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.view_empty_content, parent, false));
        }
    }

     @Override
     public int getItemViewType(int position) {
         if (mCurrentTpye == CONTEXT_TYPE_TOPIC_LIST) {
             return CONTEXT_TYPE_TOPIC_LIST;
         }else if (mCurrentTpye == CONTEXT_TYPE_COMIC_LIST) {
             return CONTEXT_TYPE_COMIC_LIST;
         }else {
             return CONTEXT_TYPE_EMPTY_LIST;
         }
     }

    private void gotoTopicDetailActivity(int position) {
        Topic topic = mTopicList.get(position);
        Intent intent = new Intent(mContext, TopicDetailActivity.class);
        intent.putExtra("topic_id", topic.getId());
        mContext.startActivity(intent);
    }


    private void gotoComicDetailActivity(int position) {
        Comic comic = mComicList.get(position);
        Intent intent = new Intent(mContext, ComicDetailActivity.class);
        intent.putExtra(ComicDetailActivity.INTENT_COMIC_DETAIL_TITLE, comic.getTitle());
        intent.putExtra(ComicDetailActivity.INTENT_COMIC_DETAIL_ID, comic.getId());
        mContext.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        switch (mCurrentTpye){
            case CONTEXT_TYPE_TOPIC_LIST:
                TopicHorizontalViewHolder topicViewHolder = (TopicHorizontalViewHolder) holder;
                Topic topic = mTopicList.get(position);
                topicViewHolder.mTitle.setText(topic.getTitle());
                topicViewHolder.mDetail.setText(topic.getUser().getNickname());
                Picasso.with(mContext).load(topic.getCover_image_url())
                        .transform(roundedTransformation)
                        .into(topicViewHolder.mImageIcon);
                topicViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoTopicDetailActivity(position);
                    }
                });

                if (position == getItemCount() - 4) {
                    mTopicRefreshListener.onLoadMoreTopic(getItemCount());// + RestClient.DEFAULT_SEARCH_LIMIT);
                }
                break;

            case CONTEXT_TYPE_COMIC_LIST:
                ComicHorizontalViewHolder comicViewHolder = (ComicHorizontalViewHolder) holder;
                Comic comic = mComicList.get(position);
                comicViewHolder.topicTitleTV.setText(comic.getTopic().getTitle());
                comicViewHolder.comicNameTV.setText(comic.getTitle());
                comicViewHolder.comicAuthorTV.setText(comic.getTopic().getUser().getNickname());
                Picasso.with(mContext).load(comic.getCover_image_url())
                        .transform(roundedTransformation)
                        .into(comicViewHolder.comicCoverIV);
                comicViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoComicDetailActivity(position);
                    }
                });

                if (position == getItemCount() - 4) {
                    mTopicRefreshListener.onLoadMoreTopic(getItemCount());// + RestClient.DEFAULT_SEARCH_LIMIT);
                }
                break;

            case CONTEXT_TYPE_EMPTY_LIST:
                EmptyContentViewHolder emptyContentViewHolder = (EmptyContentViewHolder) holder;
                break;
        }


    }

    @Override
    public int getItemCount() {
        if(mCurrentTpye == CONTEXT_TYPE_TOPIC_LIST){
            return mTopicList.size();
        }else if(mCurrentTpye == CONTEXT_TYPE_COMIC_LIST) {
            return mComicList.size();
        }else {
            return 1;
        }
    }

    public static class TopicHorizontalViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.topic_cover)
        ImageView mImageIcon;
        @InjectView(R.id.topic_name)
        TextView mTitle;
        @InjectView(R.id.topic_author)
        TextView mDetail;

        public TopicHorizontalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

    public static class ComicHorizontalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @InjectView(R.id.comic_cover)
        ImageView comicCoverIV;

        @InjectView(R.id.comic_name)
        TextView comicNameTV;

        @InjectView(R.id.comic_author)
        TextView comicAuthorTV;

        @InjectView(R.id.comic_topic_title)
        TextView topicTitleTV;

        public ComicHorizontalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            favComicClickListener.onClick(getPosition());
        }

    }

    public static class EmptyContentViewHolder extends RecyclerView.ViewHolder {

        public EmptyContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

    }

    public static interface TopicRefreshListener {
        public void onLoadMoreTopic(int newCurrentOffset);
    }
}