package com.kuaikan.comic.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.db.ComicDetailBeanDaoHelper;
import com.kuaikan.comic.rest.model.ComicBrief;
import com.kuaikan.comic.rest.model.TopicDetail;
import com.kuaikan.comic.ui.ComicDetailActivity;
import com.kuaikan.comic.util.DateUtil;
import com.kuaikan.comic.util.RoundedTransformation;
import com.kuaikan.comic.util.UIUtil;
import com.kuaikan.comic.util.UserUtil;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by skyfishjy on 12/28/14.
 */
public class TopicDetailComicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 1001;
    private static final int TYPE_LIST_COMIC_ITEM = 1002;

    TopicDetail mTopicDetail;
    Context mContext;
    int mCoverHeight;
    private RoundedTransformation roundedTransformation;

    public TopicDetailComicListAdapter(Context context, TopicDetail topicDetail) {
        this.mContext = context;
        this.mTopicDetail = topicDetail;
        mCoverHeight = (context.getResources().getDisplayMetrics().widthPixels * 11) /20;
        roundedTransformation = new RoundedTransformation(UIUtil.dp2px(mContext, 2), RoundedTransformation.Corners.ALL);
    }

    public void initData(TopicDetail topicDetail) {
        this.mTopicDetail = topicDetail;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                HeaderViewHolder headerViewHolder = new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_topic_detail_comic_list_header, parent, false));
                return headerViewHolder;
            case TYPE_LIST_COMIC_ITEM:
                return new ComicViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_comic_in_topic_detail, parent, false),
                        new ComicViewHolder.ComicViewHolderClickListener() {
                            @Override
                            public void onClick(int position, View view) {
                                view.setBackgroundColor(mContext.getResources().getColor(R.color.color_B));
//                                notifyItemChanged(position);
                                gotoComicDetailActivity(position);
                            }
                        });
            default:
                return null;
        }
    }

//    public void updateFavStatus(boolean status) {
//        topicDetail.setIs_favourite(status);
//        notifyItemChanged(TYPE_HEADER);
//    }

    private void gotoComicDetailActivity(int position) {
        if(mTopicDetail != null){
            ComicBrief comic = mTopicDetail.getComics().get(position);
            Intent intent = new Intent(mContext, ComicDetailActivity.class);
            intent.putExtra("comic_id", comic.getId());
            intent.putExtra("comic_title", comic.getTitle());
            mContext.startActivity(intent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.orderLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mTopicDetail != null)
                        refreshData(mTopicDetail.getSort() == 0 ? 1 : 0, headerViewHolder.orderImg, headerViewHolder.orderText);
                    }
                });
                break;
            case TYPE_LIST_COMIC_ITEM:
                ComicViewHolder comicViewHolder = (ComicViewHolder) holder;
                ComicBrief comicBrief = mTopicDetail.getComics().get(position - 1);
                comicViewHolder.comicTitleTV.setText(comicBrief.getTitle());
                if(ComicDetailBeanDaoHelper.getInstance().hasKey(comicBrief.getId())){
                    holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.color_B));
                }
                if(!TextUtils.isEmpty(comicBrief.getCover_image_url())){
                    Picasso.with(mContext)
                            .load(comicBrief.getCover_image_url())
                            .fit()
                            .centerCrop()
                            .transform(roundedTransformation)
                            .into(comicViewHolder.mComicCover);
                }
                comicViewHolder.mLikeCount.setText(UIUtil.getCalculatedCount(comicBrief.getLikes_count()));
                comicViewHolder.mTime.setText(DateUtil.covenrtUNIX2String(comicBrief.getCreated_at()));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(mTopicDetail != null && mTopicDetail.getComics() != null){
            return mTopicDetail.getComics().size() + 1;
        }
        return 0;
    }

    public interface ComicOrderListener {
        public void changeComicOrder(boolean positive);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEADER;
        }
        return TYPE_LIST_COMIC_ITEM;
    }


    private void refreshData(int order,final ImageView image,final TextView tv) {
        KKMHApp.getRestClient().getTopicDetail(mTopicDetail.getId(), order, new Callback<TopicDetail>() {
            @Override
            public void success(TopicDetail topicDetail, Response response) {
                mTopicDetail = topicDetail;
                image.setImageResource(mTopicDetail.getSort() == 0 ? R.drawable.topic_detail_comic_order_reverse : R.drawable.topic_detail_comic_order_positive);
                tv.setText(mTopicDetail.getSort() == 0 ? "倒序" : "正序");
                notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    public static class ComicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.comic_title_in_topic)
        TextView comicTitleTV;
        @InjectView(R.id.comic_cover)
        ImageView mComicCover;
        @InjectView(R.id.comic_time_in_topic)
        TextView mTime;
        @InjectView(R.id.comic_like_count)
        TextView mLikeCount;

        ComicViewHolderClickListener comicViewHolderClickListener;

        public ComicViewHolder(View itemView, ComicViewHolderClickListener listener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            this.comicViewHolderClickListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            comicViewHolderClickListener.onClick(getPosition() - 1 , itemView);
        }

        public static interface ComicViewHolderClickListener {
            public void onClick(int position, View view);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.topic_detail_comic_order_text)
        TextView orderText;
        @InjectView(R.id.topic_detail_comic_order_img)
        ImageView orderImg;
        @InjectView(R.id.topic_detail_comic_order_layout)
        LinearLayout orderLayout;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            orderLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(UserUtil.checkUserLogin(mContext)){
//                        topicFavListener.changeFavStatus(!topicDetail.is_favourite());
                    }
                }
            });
        }
    }
}