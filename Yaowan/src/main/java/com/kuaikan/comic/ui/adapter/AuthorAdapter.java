package com.kuaikan.comic.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.Topic;
import com.kuaikan.comic.ui.TopicDetailActivity;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by liuchao1 on 15/5/4.
 */
public class AuthorAdapter extends BaseAdapter{

    private List<Topic> mTopicList;
    private Context mContext;
    Transformation roundedTransformation;
    LoadMoreListener mLoadMoreListener;

    public AuthorAdapter(Context context,List<Topic> topics,LoadMoreListener loadMoreListener){
        this.mTopicList = topics;
        this.mContext = context;
        roundedTransformation = new RoundedTransformationBuilder()
                .borderWidthDp(0)
                .cornerRadius(8)
                .oval(false)
                .build();
        this.mLoadMoreListener = loadMoreListener;
    }

    @Override
    public int getCount() {
        if(mTopicList != null){
            return mTopicList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TopicViewHolder viewHolder;
        View view = null;
        if(convertView == null){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_author_topic, parent, false);
            viewHolder = new TopicViewHolder();
            viewHolder.mImageIcon = (ImageView) view.findViewById(R.id.topic_cover);
            viewHolder.mTitle = (TextView) view.findViewById(R.id.topic_name);
            viewHolder.mDetail = (TextView) view.findViewById(R.id.topic_description);
            view.setTag(viewHolder);
        }else{
            viewHolder = (TopicViewHolder)convertView.getTag();
            view = convertView;
        }
        Topic topic = mTopicList.get(position);
        viewHolder.mTitle.setText(topic.getTitle());
        viewHolder.mDetail.setText(topic.getDescription());
        Picasso.with(mContext).load(topic.getCover_image_url())
                .transform(roundedTransformation)
                .into(viewHolder.mImageIcon);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTopicDetailActivity(position);
            }
        });

//        if (position == getCount() - 4) {
//            mLoadMoreListener.onLoadMoreTopic(getCount());// + RestClient.DEFAULT_SEARCH_LIMIT);
//        }



        return view;
    }

    public class TopicViewHolder{
        ImageView mImageIcon;
        TextView mTitle;
        TextView mDetail;
    }

    private void gotoTopicDetailActivity(int position) {
        Topic topic = mTopicList.get(position);
        Intent intent = new Intent(mContext, TopicDetailActivity.class);
        intent.putExtra("topic_id", topic.getId());
        mContext.startActivity(intent);
    }

    public static interface LoadMoreListener {
        public void onLoadMoreTopic(int newCurrentOffset);
    }


}
