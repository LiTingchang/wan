package com.kuaikan.comic.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.RestClient;
import com.kuaikan.comic.rest.model.Comment;
import com.kuaikan.comic.ui.view.PageLikeAnimation;
import com.kuaikan.comic.util.DateUtil;
import com.kuaikan.comic.util.UIUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by skyfishjy on 2/13/15.
 */
public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Comment> commentList;
    private int currentListItemOffset;
    private ComicDetailAdapter.CommentLikeListener mCommentLikeListener;
    private int mCurrentOffset;
    private CommentRefreshListener mCommentRefreshListener;

    public CommentListAdapter(Context context,
                              ComicDetailAdapter.CommentLikeListener commentLikeListener, CommentRefreshListener commentRefreshListener) {
        mContext = context;
        currentListItemOffset = RestClient.DEFAULT_OFFSET;
        mCommentLikeListener = commentLikeListener;
        mCurrentOffset = RestClient.DEFAULT_LIMIT;
        this.mCommentRefreshListener = commentRefreshListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_comment, parent, false);
        return new ComicDetailAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ComicDetailAdapter.CommentViewHolder commentViewHolder = (ComicDetailAdapter.CommentViewHolder) holder;
        final Comment comment = commentList.get(position);
        commentViewHolder.contentTV.setText(comment.getContent());
        commentViewHolder.timeTV.setText(DateUtil.covenrtCommentUNIX2SimpleString(comment.getCreated_at()));
        commentViewHolder.userNameTV.setText(comment.getUser().getNickname());
        if(!TextUtils.isEmpty(comment.getUser().getAvatar_url())){
            Picasso.with(mContext).load(comment.getUser().getAvatar_url())
                    .resize(144, 144)
                    .transform(UIUtil.avatarRoundedTransformation)
                    .into(commentViewHolder.userIconIV);
        }
        if(comment.getLikes_count() > 0){
            commentViewHolder.likeCountTV.setVisibility(View.VISIBLE);
            commentViewHolder.likeCountTV.setText(UIUtil.getCalculatedCount(comment.getLikes_count()));
        }else{
            commentViewHolder.likeCountTV.setVisibility(View.INVISIBLE);
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
//        commentViewHolder.likeBtn.setChecked(comment.isIs_liked());
//        commentViewHolder.likeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(mCommentLikeListener != null){
//                    mCommentLikeListener.onCommentLike(comment.getComic_id(), isChecked);
//                    if(isChecked){
//                        commentViewHolder.likeCountTV.setText((comment.getLikes_count() + 1) + "");
//                        comment.setIs_liked(true);
//                        comment.setLikes_count(comment.getLikes_count() + 1);
//                    }else{
//                        commentViewHolder.likeCountTV.setText((comment.getLikes_count() - 1) + "");
//                        comment.setIs_liked(false);
//                        comment.setLikes_count(comment.getLikes_count() - 1);
//                    }
//                }
//            }
//        });

        if (position == getItemCount() - 4) {
            mCommentRefreshListener.onLoadMoreComment(getItemCount() + RestClient.DEFAULT_LIMIT);
        }

    }

    public static interface CommentRefreshListener {
        public void onLoadMoreComment(int newCurrentOffset);
    }

    @Override
    public int getItemCount() {
        if(commentList != null){
            return commentList.size();
        }
        return 0;
    }

    public void addAll(List<Comment> list) {
        int startIndex = getItemCount();
        this.commentList.addAll(startIndex, list);
        notifyItemRangeInserted(startIndex, list.size());
    }

    public void clear() {
        int size = getItemCount();
        commentList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void delete(int position) {
        commentList.remove(position);
        notifyItemRemoved(position);
    }

    public void refreshList(List<Comment> list) {
        this.commentList = list;
        notifyDataSetChanged();
    }

    public Comment getObject(int position) {
        return commentList.get(position);
    }



}
