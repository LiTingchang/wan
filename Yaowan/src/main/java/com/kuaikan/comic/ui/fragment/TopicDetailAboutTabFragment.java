package com.kuaikan.comic.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.TopicDetail;
import com.kuaikan.comic.ui.AuthorActivity;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by liuchao1 on 15/5/12.
 */
public class TopicDetailAboutTabFragment extends Fragment {

    public static final String TAG = TopicDetailAboutTabFragment.class.getSimpleName();

    @InjectView(R.id.fragment_detail_author_about)
    TextView mDetailAuthorAboutText;
    @InjectView(R.id.fragment_detail_author_layout)
    LinearLayout mDetailAuthorLayout;
    @InjectView(R.id.fragment_detail_author_avatar)
    ImageView mDetailAuthorAvatorImg;
    @InjectView(R.id.fragment_detail_author_name)
    TextView mDetailAuthorNameText;

    private Transformation roundedTransformation;
    TopicDetail mTopicDetail = new TopicDetail();

    public TopicDetailAboutTabFragment(){}

    public TopicDetailAboutTabFragment(TopicDetail topicDetail){
        mTopicDetail = topicDetail;
    }

    public static TopicDetailAboutTabFragment newInstance(TopicDetail topicDetail) {
        TopicDetailAboutTabFragment topicDetailAboutTabFragment = new TopicDetailAboutTabFragment(topicDetail);
        return topicDetailAboutTabFragment;
    }
//
//    public void initData(TopicDetail topicDetail){
//        mTopicDetail = topicDetail;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        roundedTransformation = new RoundedTransformation(UIUtil.dp2px(getActivity(), 2), RoundedTransformation.Corners.ALL);
        roundedTransformation = new RoundedTransformationBuilder()
                .borderWidthDp(0)
                .cornerRadius(getActivity().getResources().getDimensionPixelSize(R.dimen.topic_detail_author_avatar) / 2)
                .oval(false)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_about_fragment, container, false);
        ButterKnife.inject(this, view);

        if(mTopicDetail != null && mTopicDetail.getUser() != null && !TextUtils.isEmpty(mTopicDetail.getUser().getAvatar_url())){
                Picasso.with(getActivity()).load(mTopicDetail.getUser().getAvatar_url())
                        .resize(getActivity().getResources().getDimensionPixelSize(R.dimen.topic_detail_author_avatar), getActivity().getResources().getDimensionPixelSize(R.dimen.topic_detail_author_avatar))
                        .transform(roundedTransformation)
                        .into(mDetailAuthorAvatorImg);
            mDetailAuthorNameText.setText(mTopicDetail.getUser().getNickname());
            mDetailAuthorAboutText.setText(mTopicDetail.getDescription());
        }

        mDetailAuthorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTopicDetail != null && mTopicDetail.getUser() != null){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), AuthorActivity.class);
                    intent.putExtra(AuthorActivity.KEY_AUTHOR_ID, mTopicDetail.getUser().getId());
                    getActivity().startActivity(intent);
                }
            }
        });

        return view;
    }

}
