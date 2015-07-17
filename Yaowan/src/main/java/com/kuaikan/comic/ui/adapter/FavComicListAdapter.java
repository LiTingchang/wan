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
import com.kuaikan.comic.rest.model.Comic;
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
public class FavComicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Comic> comicList;
    private ListRefreshListener listRefreshListener;
    private int currentListItemOffset;
    private RoundedTransformation roundedTransformation;
    private FavComicLongClickListener favComicLongClickListener;
    private FavComicClickListener favComicClickListener;

    public FavComicListAdapter(Context context,
                               List<Comic> list,
                               ListRefreshListener listener,
                               FavComicLongClickListener longClickListener,
                               FavComicClickListener clickListener) {
        mContext = context;
        comicList = list;
        listRefreshListener = listener;
        currentListItemOffset = RestClient.DEFAULT_OFFSET;
        roundedTransformation = new RoundedTransformation(UIUtil.dp2px(mContext, 2), RoundedTransformation.Corners.ALL);
        favComicLongClickListener = longClickListener;
        favComicClickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_fav_comic, parent, false);
        return new FavComicViewHolder(view, favComicLongClickListener, favComicClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FavComicViewHolder) holder).bindFavComic(comicList.get(position));

        if (position == getItemCount() - 2 && (comicList.size() % 20) == 0 ) {
            listRefreshListener.onLoadMoreItem(comicList.size() + RestClient.DEFAULT_LIMIT);
        }

    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }

    public void addAll(List<Comic> list) {
        int startIndex = getItemCount();
        this.comicList.addAll(startIndex, list);
        notifyItemRangeInserted(startIndex, list.size());
    }

    public void clear() {
        int size = getItemCount();
        comicList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void delete(int position) {
        comicList.remove(position);
        notifyItemRemoved(position);
    }

    public void refreshList(List<Comic> list) {
        this.comicList = list;
        notifyDataSetChanged();
    }

    public Comic getObject(int position) {
        return comicList.get(position);
    }

    public void setCurrentListItemOffset(int offset) {
        this.currentListItemOffset = offset;
    }

    public interface FavComicLongClickListener {
        public boolean onLongClick(int position);
    }

    public interface FavComicClickListener {
        public void onClick(int position);
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

        FavComicLongClickListener favComicLongClickListener;
        FavComicClickListener favComicClickListener;

        public FavComicViewHolder(View itemView,
                                  FavComicLongClickListener longClickListener,
                                  FavComicClickListener clickListener) {
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
            favComicClickListener.onClick(getPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            return favComicLongClickListener.onLongClick(getPosition());
        }
    }
}
