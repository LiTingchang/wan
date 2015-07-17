package com.kuaikan.comic.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.RestClient;
import com.kuaikan.comic.rest.model.Topic;
import com.kuaikan.comic.ui.listener.ListRefreshListener;
import com.kuaikan.comic.util.RoundedTransformation;
import com.kuaikan.comic.util.UIUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by skyfishjy on 2/13/15.
 */
public class FavTopicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Topic> topicList;
    private ListRefreshListener listRefreshListener;
    private int currentListItemOffset;
    private RoundedTransformation roundedTransformation;
    private FavTopicLongClickListener favTopicLongClickListener;
    private FavTopicClickListener favTopicClickListener;

    public FavTopicListAdapter(Context context,
                               List<Topic> list,
                               ListRefreshListener listener,
                               FavTopicLongClickListener longClickListener,
                               FavTopicClickListener clickListener) {
        mContext = context;
        topicList = list;
        listRefreshListener = listener;
        currentListItemOffset = RestClient.DEFAULT_OFFSET;
        roundedTransformation = new RoundedTransformation(UIUtil.dp2px(mContext, 2), RoundedTransformation.Corners.ALL);
        favTopicLongClickListener = longClickListener;
        favTopicClickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_fav_topic, parent, false);
        return new FavTopicViewHolder(view, favTopicLongClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FavTopicViewHolder) holder).bindFavTopic(topicList.get(position));

        if (position == getItemCount() - 2 && (topicList.size() % 20) == 0) {
            listRefreshListener.onLoadMoreItem(topicList.size() + RestClient.DEFAULT_LIMIT);
        }
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public void addAll(List<Topic> list) {
        int startIndex = getItemCount();
        this.topicList.addAll(startIndex, list);
        notifyItemRangeInserted(startIndex, list.size());
    }

    public void clear() {
        int size = getItemCount();
        topicList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void delete(int position) {
        topicList.remove(position);
        notifyItemRemoved(position);
    }

    public void refreshList(List<Topic> list) {
        this.topicList = list;
        notifyDataSetChanged();
    }

    public Topic getObject(int position) {
        return topicList.get(position);
    }
    public void setCurrentListItemOffset(int offset) {
        this.currentListItemOffset = offset;
    }

    public interface FavTopicLongClickListener {
        public boolean onLongClick(int position);
    }

    public interface FavTopicClickListener {
        public void onClick(int position);
    }

    public class FavTopicViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        @InjectView(R.id.topic_cover)
        ImageView topicCoverIV;

        @InjectView(R.id.topic_name)
        TextView topicNameTV;

        @InjectView(R.id.topic_author)
        TextView topicAuthorTV;

        FavTopicLongClickListener favTopicLongClickListener;

        public FavTopicViewHolder(View itemView, FavTopicLongClickListener listener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            this.favTopicLongClickListener = listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setLongClickable(true);
        }

        public void bindFavTopic(Topic topic) {
            Picasso.with(mContext)
                    .load(topic.getCover_image_url())
                    .fit()
                    .centerCrop()
                    .transform(roundedTransformation)
                    .into(topicCoverIV);
            topicNameTV.setText(topic.getTitle());
            topicAuthorTV.setText(topic.getUser().getNickname());
        }

        @Override
        public void onClick(View view) {
            favTopicClickListener.onClick(getPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            return favTopicLongClickListener.onLongClick(getPosition());
        }
    }
}
