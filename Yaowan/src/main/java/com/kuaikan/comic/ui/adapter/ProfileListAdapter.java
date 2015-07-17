package com.kuaikan.comic.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.Comic;
import com.kuaikan.comic.rest.model.Topic;
import com.kuaikan.comic.util.RoundedTransformation;
import com.kuaikan.comic.util.UIUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by skyfishjy on 2/13/15.
 */
public class ProfileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Comic> mComicList = new ArrayList<>();
    private List<Topic> mTopicList = new ArrayList<>();
    private RoundedTransformation roundedTransformation;
    private ProfileItemLongClickListener mProfileItemLongClickListener;
    private ProfileItemComicClickListener mProfileItemComicClickListener;
    private ProfileTabChangeListener mProfileTabChangeListener;
    private ProfileRefreshListener mProfileRefreshListener;

    public static final int MODE_COMIC = 0;
    public static final int MODE_TOPIC = 1;

    private int mCurrentMode = MODE_COMIC;

    private static final int VIEW_TYPE_HEADER = 1001;
    private static final int VIEW_TYPE_TAB = 1002;
    private static final int VIEW_TYPE_COMIC = 1003;
    private static final int VIEW_TYPE_TOPIC = 1004;

    public ProfileListAdapter(Context context,
                              ProfileItemLongClickListener longClickListener,
                              ProfileItemComicClickListener clickListener,
                              ProfileTabChangeListener profileTabChangeListener,
                              ProfileRefreshListener profileRefreshListener) {
        mContext = context;
        roundedTransformation = new RoundedTransformation(UIUtil.dp2px(mContext, 2), RoundedTransformation.Corners.ALL);
        mProfileItemLongClickListener = longClickListener;
        mProfileItemComicClickListener = clickListener;
        mProfileTabChangeListener = profileTabChangeListener;
        mProfileRefreshListener = profileRefreshListener;
    }

    public void initComicData(List<Comic> comicList){
        this.mComicList = comicList;
        this.mCurrentMode = MODE_COMIC;
        notifyDataSetChanged();
    }

    public void initTopicData(List<Topic> topicList){
        this.mTopicList = topicList;
        this.mCurrentMode = MODE_TOPIC;
        notifyDataSetChanged();
    }

    public Comic getComicObject(int position) {
        return mComicList.get(position);
    }

    public Topic getTopicObject(int position) {
        return mTopicList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_TYPE_HEADER;
        }else if(position == 1){
            return VIEW_TYPE_TAB;
        }else{
            if(mCurrentMode == MODE_COMIC){
                return VIEW_TYPE_COMIC;
            }else{
                return VIEW_TYPE_TOPIC;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_HEADER){
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header, parent, false));
        }else if(viewType == VIEW_TYPE_TAB){
            return new TabViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_profile_tab, parent, false));
        }else if(viewType == VIEW_TYPE_COMIC){
            return new FavComicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_fav_comic, parent, false),
                    mProfileItemLongClickListener, mProfileItemComicClickListener);
        }else{
            return new FavTopicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_fav_topic, parent, false),
                    mProfileItemLongClickListener, mProfileItemComicClickListener);
        }
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_fav_comic, parent, false);
//        return new FavComicViewHolder(view, profileItemLongClickListener, profileItemComicClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                break;
            case VIEW_TYPE_TAB:
                TabViewHolder tabViewHolder = (TabViewHolder) holder;
                if(mCurrentMode == MODE_COMIC){
                    tabViewHolder.comicTab.setSelected(true);
                    tabViewHolder.topicTab.setSelected(false);
                }else{
                    tabViewHolder.comicTab.setSelected(false);
                    tabViewHolder.topicTab.setSelected(true);
                }
                tabViewHolder.comicTab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCurrentMode = MODE_COMIC;
                        notifyDataSetChanged();
                            if(mProfileTabChangeListener != null) {
                                mProfileTabChangeListener.onTabChange(MODE_COMIC, mComicList.size() == 0);
                            }
                    }
                });
                tabViewHolder.topicTab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCurrentMode = MODE_TOPIC;
                        notifyDataSetChanged();
                            if(mProfileTabChangeListener != null) {
                                mProfileTabChangeListener.onTabChange(MODE_TOPIC, mTopicList.size() == 0);
                            }
                    }
                });
                break;
            case VIEW_TYPE_COMIC:
                FavComicViewHolder favComicViewHolder = (FavComicViewHolder) holder;
                favComicViewHolder.bindFavComic(mComicList.get(position - 2));
                break;
            case VIEW_TYPE_TOPIC:
                FavTopicViewHolder favTopicViewHolder = (FavTopicViewHolder) holder;
                favTopicViewHolder.bindFavTopic(mTopicList.get(position - 2));
                break;

        }

        if (position == getItemCount() - 2 ) {
            if(mCurrentMode == MODE_COMIC){
                if(mComicList.size() > 0 && (mComicList.size() % 20) == 0){
                    mProfileRefreshListener.onLoadMoreItem(mComicList.size());
                }
            }else{
                if(mTopicList.size() > 0 && (mTopicList.size() % 20) == 0){
                    mProfileRefreshListener.onLoadMoreItem(mTopicList.size());
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        if(mCurrentMode == MODE_COMIC){
            if(mComicList != null){
                return mComicList.size() + 2;
            }else{
                return 2;
            }
        }else{
            if(mTopicList != null){
                return mTopicList.size() + 2;
            }else{
                return 2;
            }
        }
    }

    public void addAllComic(List<Comic> list) {
        int startIndex = getItemCount() - 2;
        this.mComicList.addAll(startIndex, list);
        notifyItemRangeInserted(startIndex + 2, list.size());
    }

    public void addAllTopic(List<Topic> list) {
        int startIndex = getItemCount() - 2;
        this.mTopicList.addAll(startIndex, list);
        notifyItemRangeInserted(startIndex + 2, list.size());
    }

    public void clear() {
        int size = getItemCount();
        mComicList.clear();
        mTopicList.clear();
        notifyItemRangeRemoved(2, size);
    }

    public void deleteComic(int position) {
        mComicList.remove(position);
        notifyItemRemoved(position + 2);
    }

    public void deleteTopic(int position) {
        mTopicList.remove(position);
        notifyItemRemoved(position + 2) ;
    }

    public void refreshComicList(List<Comic> list) {
        this.mComicList = list;
        mCurrentMode = MODE_COMIC;
        notifyDataSetChanged();
    }

    public void refreshTopicList(List<Topic> list) {
        this.mTopicList = list;
        mCurrentMode = MODE_TOPIC;
        notifyDataSetChanged();
    }

    public interface ProfileItemLongClickListener {
        public boolean onLongClick(int mode, int position);
    }

    public interface ProfileItemComicClickListener {
        public void onClick(int mode, int position);
    }

    public interface ProfileTabChangeListener {
        public void onTabChange(int tab, boolean needLoad);
    }

    public interface ProfileRefreshListener {
        public void onLoadMoreItem(int newPosition);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    public class TabViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.fav_topic)
        TextView topicTab;
        @InjectView(R.id.fav_comic)
        TextView comicTab;
        public TabViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, itemView);
        }
    }

    public class FavComicViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        @InjectView(R.id.comic_cover)
        ImageView comicCoverIV;

        @InjectView(R.id.comic_name)
        TextView comicNameTV;

        @InjectView(R.id.comic_author)
        TextView comicAuthorTV;

        @InjectView(R.id.comic_topic_title)
        TextView topicTitleTV;

        ProfileItemLongClickListener favComicLongClickListener;
        ProfileItemComicClickListener favComicClickListener;

        public FavComicViewHolder(View itemView,
                                  ProfileItemLongClickListener longClickListener,
                                  ProfileItemComicClickListener clickListener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            this.favComicLongClickListener = longClickListener;
            this.favComicClickListener = clickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setLongClickable(true);
        }

        public void bindFavComic(Comic comic) {
            Picasso.with(mContext)
                    .load(comic.getCover_image_url())
                    .fit()
                    .centerCrop()
                    .transform(roundedTransformation)
                    .into(comicCoverIV);
            comicNameTV.setText(comic.getTitle());
            comicAuthorTV.setText(comic.getTopic().getUser().getNickname());
            topicTitleTV.setText(comic.getTopic().getTitle());
        }

        @Override
        public void onClick(View view) {
            favComicClickListener.onClick(MODE_COMIC, getPosition() -2 );
        }

        @Override
        public boolean onLongClick(View view) {
            return favComicLongClickListener.onLongClick(MODE_COMIC, getPosition() -2);
        }
    }


    public class FavTopicViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        @InjectView(R.id.topic_cover)
        ImageView topicCoverIV;

        @InjectView(R.id.topic_name)
        TextView topicNameTV;

        @InjectView(R.id.topic_author)
        TextView topicAuthorTV;

        ProfileItemLongClickListener profileItemLongClickListener;
        ProfileItemComicClickListener profileItemComicClickListener;

        public FavTopicViewHolder(View itemView, ProfileItemLongClickListener longClickListener,
                                  ProfileItemComicClickListener clickListener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            this.profileItemLongClickListener = longClickListener;
            this.profileItemComicClickListener = clickListener;
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
            profileItemComicClickListener.onClick(MODE_TOPIC, getPosition() - 2);
        }

        @Override
        public boolean onLongClick(View view) {
            return profileItemLongClickListener.onLongClick(MODE_TOPIC, getPosition() - 2);
        }
    }
}
