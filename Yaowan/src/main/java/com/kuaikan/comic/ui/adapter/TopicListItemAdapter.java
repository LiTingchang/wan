package com.kuaikan.comic.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.RestClient;
import com.kuaikan.comic.rest.model.Comic;
import com.kuaikan.comic.rest.model.MixTopic;
import com.kuaikan.comic.rest.model.Topic;
import com.kuaikan.comic.ui.ComicDetailActivity;
import com.kuaikan.comic.ui.TopicDetailActivity;
import com.kuaikan.comic.ui.TopicListActivity;
import com.kuaikan.comic.util.RoundedTransformation;
import com.kuaikan.comic.util.UIUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by a on 2015/3/28.
 */
public class TopicListItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    List<Topic> mTopicList;
    List<Comic> mComicList;
    int currentTopicOffset;

    private static final int MODE_TOPIC = 1001;
    private static final int MODE_COMIC = 1002;
    int mCurrentMode = MODE_TOPIC;

//    public enum DATA_MODE {
//        MODE_TOPIC,
//        MODE_COMIC
//    }

    public TopicListItemAdapter(Context context) {
        this.mContext = context;
        this.currentTopicOffset = RestClient.DEFAULT_OFFSET;
//        this.imageWidth = context.getResources().getDimensionPixelSize(R.dimen.view_topic_comic_list_width);//.getDisplayMetrics().widthPixels / 2;
//        this.imageHigth = context.getResources().getDimensionPixelSize(R.dimen.view_topic_comic_list_higth);

        Timber.tag(TopicListItemAdapter.class.getSimpleName());
    }

    public void setData(MixTopic data) {
        if(data.getType() == TopicListActivity.TOPIC_LIST_TYPE_TOPIC){
            this.mTopicList = data.getTopics();
            mCurrentMode = MODE_TOPIC;
        }else{
            this.mComicList = data.getComics();
            mCurrentMode = MODE_COMIC;
        }

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case MODE_TOPIC:
                return new TopicViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_comic_all_topic, parent, false), new TopicViewHolder.TopicViewHolderClickListener() {
                    @Override
                    public void onClick(int position) {
                        gotoTopicListActivity(position);
                    }
                });
            case MODE_COMIC:
                return new ComicViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_comic_all_comic, parent, false), new ComicViewHolder.ComicViewHolderClickListener() {
                    @Override
                    public void onClick(int position) {
                        gotoComicDetailActivity(position);
                    }
                });
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (mCurrentMode) {
            case MODE_TOPIC:
                TopicViewHolder topicViewHolder = (TopicViewHolder) holder;
                Topic topic = mTopicList.get(position);
                String imageUrl;
                if(!TextUtils.isEmpty(topic.getVerticalImageUrl())){
                    imageUrl = topic.getVerticalImageUrl();
                }else{
                    imageUrl = topic.getCover_image_url();
                }
                if(!TextUtils.isEmpty(imageUrl)){
                    Picasso.with(mContext)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_common_placeholder_ss)
                            .transform(new RoundedTransformation(UIUtil.dp2px(mContext, 2), RoundedTransformation.Corners.TOP))
                            .into(topicViewHolder.topicIV);
                }
                topicViewHolder.titleTV.setText(topic.getTitle());
                topicViewHolder.authourTV.setText(topic.getUser().getNickname());
                break;
            case MODE_COMIC:
                ComicViewHolder comicViewHolder = (ComicViewHolder) holder;
                Comic comic = mComicList.get(position);
                Picasso.with(mContext)
                        .load(comic.getCover_image_url())
                        .placeholder(R.drawable.ic_common_placeholder_m)
                        .transform(new RoundedTransformation(UIUtil.dp2px(mContext, 2), RoundedTransformation.Corners.TOP))
                        .into(comicViewHolder.comicIV);
                comicViewHolder.titleTV.setText(comic.getTitle());
                comicViewHolder.authourTV.setText(comic.getTopic().getUser().getNickname());
                break;
            default:
                return ;
        }



    }

    private void gotoTopicListActivity(int position) {
        Intent intent = new Intent(mContext, TopicDetailActivity.class);
            Topic topic = mTopicList.get(position);
            intent.putExtra("topic_id", topic.getId());
        mContext.startActivity(intent);
    }

    private void gotoComicDetailActivity(int position) {
        Intent intent = new Intent(mContext, ComicDetailActivity.class);
        Comic comic = mComicList.get(position);
        intent.putExtra("comic_id", comic.getId());
        intent.putExtra("comic_title", comic.getTitle());
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        switch(mCurrentMode){
            case MODE_TOPIC:
                return mTopicList.size();
            case MODE_COMIC:
                return mComicList.size();
            default:
                return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mCurrentMode;
    }


    public static class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.topic_image)
        ImageView topicIV;
        @InjectView(R.id.topic_title)
        TextView titleTV;
        @InjectView(R.id.topic_author)
        TextView authourTV;

        TopicViewHolderClickListener topicViewHolderClickListener;

        public TopicViewHolder(View view, TopicViewHolderClickListener listener) {
            super(view);
            ButterKnife.inject(this, view);
            view.setOnClickListener(this);
            this.topicViewHolderClickListener = listener;
        }

        @Override
        public void onClick(View view) {
            topicViewHolderClickListener.onClick(getPosition());
        }

        public static interface TopicViewHolderClickListener {
            public void onClick(int position);
        }
    }


    public static class ComicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.comic_image)
        ImageView comicIV;
        @InjectView(R.id.comic_title)
        TextView titleTV;
        @InjectView(R.id.comic_author)
        TextView authourTV;

        ComicViewHolderClickListener comicViewHolderClickListener;

        public ComicViewHolder(View view, ComicViewHolderClickListener listener) {
            super(view);
            ButterKnife.inject(this, view);
            view.setOnClickListener(this);
            this.comicViewHolderClickListener = listener;
        }

        @Override
        public void onClick(View view) {
            comicViewHolderClickListener.onClick(getPosition());
        }

        public static interface ComicViewHolderClickListener {
            public void onClick(int position);
        }
    }

}
