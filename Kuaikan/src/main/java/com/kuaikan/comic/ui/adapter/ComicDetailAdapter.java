package com.kuaikan.comic.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.RestClient;
import com.kuaikan.comic.rest.model.API.ComicDetailResponse;
import com.kuaikan.comic.rest.model.Comment;
import com.kuaikan.comic.ui.ComicDetailActivity;
import com.kuaikan.comic.ui.CommentListActivity;
import com.kuaikan.comic.ui.view.PageLikeAnimation;
import com.kuaikan.comic.util.DateUtil;
import com.kuaikan.comic.util.UIUtil;
import com.makeramen.RoundedTransformationBuilder;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by skyfishjy on 12/23/14.
 */
public class ComicDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int COMIC_TOPIC_INFO = 0;
    private static final int COMIC_IMAGE_TYPE = 1;
    private static final int COMIC_TOPIC_DESCRIPTION = 2;
    private static final int COMIC_COMMENTLIST = 3;
    private static final int COMIC_COMMENT_MORE = 4;

    Context mContext;
    ComicDetailResponse comicDetailResponse;
    int screenWidth;
    List<Comment> commentList;
    Transformation roundedTransformation;
    int currentCommentOffset;
    TopicInfoViewHolderClickListener topicInfoViewHolderClickListener;
    TopicDescriptionViewHolderClickListener topicDescriptionViewHolderClickListener;
    CommentLikeListener mCommentLikeListener;

    public ComicDetailAdapter(Context context,
                              ComicDetailResponse comicDetailResponse,
                              List<Comment> commentList,CommentLikeListener commentLikeListener,
                              TopicInfoViewHolderClickListener topicInfoViewHolderClickListener,
                              TopicDescriptionViewHolderClickListener topicDescriptionViewHolderClickListener) {
        this.mContext = context;
        this.comicDetailResponse = comicDetailResponse;
        this.commentList = commentList;
        this.screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.mCommentLikeListener = commentLikeListener;
        this.topicInfoViewHolderClickListener = topicInfoViewHolderClickListener;
        this.topicDescriptionViewHolderClickListener = topicDescriptionViewHolderClickListener;
        this.currentCommentOffset = RestClient.DEFAULT_OFFSET;
        Timber.tag(ComicDetailAdapter.class.getSimpleName());

        roundedTransformation = new RoundedTransformationBuilder()
                .borderWidthDp(0)
                .cornerRadius(mContext.getResources().getDimensionPixelSize(R.dimen.comic_detail_author_avatar) / 2)
                .oval(false)
                .build();
    }

    public void addCommentList(List<Comment> newCommentList) {
        commentList.addAll(newCommentList);
        notifyDataSetChanged();
    }

    public void initData(ComicDetailResponse comicDetailResponse, List<Comment> commentList) {
        this.comicDetailResponse = comicDetailResponse;
        this.commentList = commentList;
        this.currentCommentOffset = RestClient.DEFAULT_OFFSET;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case COMIC_TOPIC_INFO:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_comic_info, parent, false);
                return new TopicInfoViewHolder(view, topicInfoViewHolderClickListener);
            case COMIC_IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_image_comic_detail, parent, false);
                return new ImageViewHolder(view);
            case COMIC_TOPIC_DESCRIPTION:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_comic_topic_description, parent, false);
                return new TopicDescriptionViewHolder(view, topicDescriptionViewHolderClickListener);
            case COMIC_COMMENTLIST:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_comment, parent, false);
                return new CommentViewHolder(view);
            case COMIC_COMMENT_MORE:
                return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_loadmore, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case COMIC_IMAGE_TYPE:
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                ViewGroup.LayoutParams layoutParams = imageViewHolder.comicImage.getLayoutParams();
                layoutParams.width = screenWidth;
                imageViewHolder.comicImage.setLayoutParams(layoutParams);
                try {
                    if(!TextUtils.isEmpty(comicDetailResponse.getImages()[position - 1])){
                        Picasso.with(mContext)
                                .load(comicDetailResponse.getImages()[position - 1])
                                .resize(screenWidth, 0)
                                .placeholder(R.drawable.ic_common_placeholder_l)
                                .into(imageViewHolder.comicImage);
                    }
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }
                break;
            case COMIC_TOPIC_INFO:
                TopicInfoViewHolder topicInfoViewHolder = (TopicInfoViewHolder) holder;
                topicInfoViewHolder.mAuthor.setText(comicDetailResponse.getTopic().getUser().getNickname());
                if(!TextUtils.isEmpty(comicDetailResponse.getTopic().getUser().getAvatar_url())){
                    Picasso.with(mContext)
                            .load(comicDetailResponse.getTopic().getUser().getAvatar_url())
                            .placeholder(R.drawable.ic_personal_headportrait)
                            .transform(roundedTransformation)
                            .fit()
                            .centerInside()
                            .into(topicInfoViewHolder.mAvatar);
                }
                topicInfoViewHolder.likeTV.setImageResource(comicDetailResponse.is_favourite() ? R.drawable.ic_album_nav_collection_pressed : R.drawable.ic_album_nav_collection_normal);
                topicInfoViewHolder.mTopic.setText(comicDetailResponse.getTopic().getTitle());
                break;
            case COMIC_TOPIC_DESCRIPTION:
                final TopicDescriptionViewHolder topicDescriptionViewHolder = (TopicDescriptionViewHolder) holder;
                if(comicDetailResponse.is_liked()){
                    topicDescriptionViewHolder.likeCB.setChecked(true);
                    topicDescriptionViewHolder.likeCB.setText("赞 " + comicDetailResponse.getLikes_count() + "");
                }else{
                    topicDescriptionViewHolder.likeCB.setChecked(false);
                    topicDescriptionViewHolder.likeCB.setText("赞 " + comicDetailResponse.getLikes_count() + "");
                }
                if (comicDetailResponse.is_favourite()) {
                    topicDescriptionViewHolder.favCB.setChecked(true);
                    topicDescriptionViewHolder.favCB.setText("已收藏");
                } else {
                    topicDescriptionViewHolder.favCB.setChecked(false);
                    topicDescriptionViewHolder.favCB.setText("收藏本条");
                }
//                topicDescriptionViewHolder.comicNext.setEnabled(comicDetailResponse.getNext_comic_id() != 0l);
//                topicDescriptionViewHolder.comicPre.setEnabled(comicDetailResponse.getPrevious_comic_id() != 0l);

                ComicDetailActivity.setOnLikeBtnClickListener(new ComicDetailActivity.OnLikeBtnClickListener() {
                    @Override
                    public void onLikeBtnClick(boolean like) {
                        if(like){
                            topicDescriptionViewHolder.likeCB.setChecked(true);
                            topicDescriptionViewHolder.likeCB.setText("赞 " + comicDetailResponse.getLikes_count() + "");
                        }else{
                            topicDescriptionViewHolder.likeCB.setChecked(false);
                            topicDescriptionViewHolder.likeCB.setText("赞 " + comicDetailResponse.getLikes_count() + "");
                        }
                    }
                });
                break;
            case COMIC_COMMENTLIST:
                final CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
                final Comment comment = commentList.get(getComicCommentPosition(position));
                commentViewHolder.contentTV.setText(comment.getContent());
                commentViewHolder.timeTV.setText(DateUtil.covenrtCommentUNIX2SimpleString(comment.getCreated_at()));
                commentViewHolder.userNameTV.setText(comment.getUser().getNickname());
                if(!TextUtils.isEmpty(comment.getUser().getAvatar_url())){
                    Picasso.with(mContext).load(comment.getUser().getAvatar_url())
                            .resize(mContext.getResources().getDimensionPixelSize(R.dimen.comic_detail_author_avatar), mContext.getResources().getDimensionPixelSize(R.dimen.comic_detail_author_avatar))
                            .transform(roundedTransformation)
                            .into(commentViewHolder.userIconIV);
                }
                if(comment.getLikes_count() == 0){
                    commentViewHolder.likeCountTV.setVisibility(View.INVISIBLE);
                }else{
                    commentViewHolder.likeCountTV.setVisibility(View.VISIBLE);
                    commentViewHolder.likeCountTV.setText(UIUtil.getCalculatedCount(comment.getLikes_count()));
                }
                commentViewHolder.likeBtn.setImageResource(comment.isIs_liked() ? R.drawable.ic_details_toolbar_praise_pressed : R.drawable.ic_details_toolbar_praise_normal);
                commentViewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mCommentLikeListener != null){
                            mCommentLikeListener.onCommentLike(comment.getId(), !comment.isIs_liked());
                            if(!comment.isIs_liked()){
                                commentViewHolder.likeCountTV.setVisibility(View.VISIBLE);
                                commentViewHolder.likeCountTV.setText(UIUtil.getCalculatedCount(comment.getLikes_count() + 1));
                                comment.setIs_liked(true);
                                comment.setLikes_count(comment.getLikes_count() + 1);
                            }else{
                                commentViewHolder.likeCountTV.setText(UIUtil.getCalculatedCount(comment.getLikes_count() - 1));
                                if(comment.getLikes_count() - 1 == 0){
                                    commentViewHolder.likeCountTV.setVisibility(View.INVISIBLE);
                                }
                                comment.setIs_liked(false);
                                comment.setLikes_count(comment.getLikes_count() - 1);
                            }
                            commentViewHolder.likeBtn.setImageResource(comment.isIs_liked() ? R.drawable.ic_details_toolbar_praise_pressed : R.drawable.ic_details_toolbar_praise_normal);
                            commentViewHolder.likeBtn.startAnimation(new PageLikeAnimation(false, 1.8f, 0.8f, 1.0f));
                        }
                    }
                });
                break;
            case COMIC_COMMENT_MORE:
                LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
                loadMoreViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 打开新界面
                        Intent intent = new Intent();
                        intent.setClass(mContext, CommentListActivity.class);
                        intent.putExtra(CommentListActivity.COMMENT_COMIC_ID, comicDetailResponse.getId());
                        mContext.startActivity(intent);

                    }
                });
                break;
        }
    }

    public int getComicCommentPosition(int position) {
        return position - (comicDetailResponse.getImages().length + 2);
    }

    @Override
    public int getItemCount() {
        return comicDetailResponse.getImages().length + 2 + commentList.size() + 1;
    }

    public int getCommentPosition() {
        int position;
        if (commentList.size() > 0) {
            position = comicDetailResponse.getImages().length + 2;
        } else {
            position = comicDetailResponse.getImages().length + 1;
        }
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return COMIC_TOPIC_INFO;
        } else if (0 < position && position < comicDetailResponse.getImages().length + 1) {
            return COMIC_IMAGE_TYPE;
        } else if (position == getComicTopicDescriptionPosition()) {
            return COMIC_TOPIC_DESCRIPTION;
        } else if(getComicTopicDescriptionPosition() < position && position <= getComicTopicDescriptionPosition() + commentList.size()){
            return COMIC_COMMENTLIST;
        } else {
            return COMIC_COMMENT_MORE;
        }


    }

    public int getComicTopicDescriptionPosition() {
        return comicDetailResponse.getImages().length + 1;
    }

    public void updateFavStatus() {
//        topicInfoViewHolder.likeTV.setBackgroundResource(comicDetailResponse.is_favourite() ? R.drawable.ic_fav_added_small : R.drawable.ic_fav_unadded_small);
        notifyItemChanged(getComicTopicDescriptionPosition());
        notifyItemChanged(0);
    }

//    public interface CommentRefreshListener {
//        public void onLoadNewComment(int newCommentOffset);
//    }

    public interface TopicInfoViewHolderClickListener {
        public void onShowAuthorInfo();

        public void onFavOption();
    }

    private void gotoNextComic(){

    }

    public static interface CommentLikeListener {
        public void onCommentLike(long comment_id, boolean like);
    }

    public interface TopicDescriptionViewHolderClickListener {
        public void onAddToFav();

        public void onAddToLike();

        public void onPreComic();

        public void onNextComic();
    }

    public static class TopicInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.detail_user_layout)
        RelativeLayout infoContainerRL;
        @InjectView(R.id.detail_like)
        ImageView likeTV;
        @InjectView(R.id.detail_comic_avatar)
        ImageView mAvatar;
        @InjectView(R.id.comic_author)
        TextView mAuthor;
        @InjectView(R.id.info_topic)
        TextView mTopic;

        TopicInfoViewHolderClickListener topicInfoViewHolderClickListener;

        public TopicInfoViewHolder(View view, TopicInfoViewHolderClickListener listener) {
            super(view);
            ButterKnife.inject(this, view);
            this.topicInfoViewHolderClickListener = listener;
            infoContainerRL.setOnClickListener(this);
            likeTV.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.detail_user_layout:
                    if(topicInfoViewHolderClickListener != null){
                        topicInfoViewHolderClickListener.onShowAuthorInfo();
                    }
                    break;
                case R.id.detail_like:
                    if(topicInfoViewHolderClickListener != null){
                        topicInfoViewHolderClickListener.onFavOption();
                    }
                    break;
            }
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.comic_image)
        ImageView comicImage;

        public ImageViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public static class TopicDescriptionViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.comic_fav)
        CheckBox favCB;
        @InjectView(R.id.comic_like)
        CheckBox likeCB;
        @InjectView(R.id.comic_pre)
        RelativeLayout comicPre;
        @InjectView(R.id.comic_next)
        RelativeLayout comicNext;

        TopicDescriptionViewHolderClickListener topicDescriptionContainerClickListener;

        public TopicDescriptionViewHolder(View view, TopicDescriptionViewHolderClickListener listener) {
            super(view);
            ButterKnife.inject(this, view);
            this.topicDescriptionContainerClickListener = listener;
            likeCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    topicDescriptionContainerClickListener.onAddToLike();
                }
            });
            favCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    topicDescriptionContainerClickListener.onAddToFav();
                }
            });
            comicPre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    topicDescriptionContainerClickListener.onPreComic();
                }
            });
            comicNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    topicDescriptionContainerClickListener.onNextComic();
                }
            });

        }
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.comment_user_icon)
        public ImageView userIconIV;
        @InjectView(R.id.comment_user_name)
        public TextView userNameTV;
        @InjectView(R.id.comment_content)
        public EmojiconTextView contentTV;
        @InjectView(R.id.comment_time)
        public TextView timeTV;
        @InjectView(R.id.comment_like_count)
        public TextView likeCountTV;
        @InjectView(R.id.comment_like_btn)
        public ImageView likeBtn;

        public CommentViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.view_loadmore_tv)
        TextView mLoadMore;

        public LoadMoreViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
