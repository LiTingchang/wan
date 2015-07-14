package com.kuaikan.comic.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.API.EmptyDataResponse;
import com.kuaikan.comic.rest.model.TopicDetail;
import com.kuaikan.comic.ui.fragment.TopicDetailFragment;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class TopicDetailActivity extends BaseActivity {

    public static final String INTENT_TOPIC_ID = "topic_id";

    long topicID;
    @InjectView(R.id.activity_topic_back)
    TextView mBack;
    @InjectView(R.id.topic_detail_header_collect)
    TextView mCollectBtn;
    @InjectView(R.id.activity_topic_back_layout)
    FrameLayout mBackLayout;
    @InjectView(R.id.topic_detail_header_collect_layout)
    FrameLayout mCollectLayout;
    @InjectView(R.id.activity_topic_back_bg)
    View mBackBg;


    TopicDetail mTopicDetail = new TopicDetail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        Timber.tag(TopicDetailActivity.class.getSimpleName());
        topicID = getIntent().getLongExtra(INTENT_TOPIC_ID, 0);

        ButterKnife.inject(this);

        loadData();

        mBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopicDetailActivity.this.finish();
            }
        });
        mCollectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTopicDetail.is_favourite()){
                    delFavStatus();
                }else{
                    addFavStatus();
                }
            }
        });
    }

    private void addFavStatus() {
        KKMHApp.getRestClient().addFavTopic(mTopicDetail.getId(), new Callback<EmptyDataResponse>() {
            @Override
            public void success(EmptyDataResponse emptyDataResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(TopicDetailActivity.this, response)){
                    return;
                }
                makeToast("关注成功");
                mCollectBtn.setBackgroundResource(R.drawable.ic_album_nav_followed_btn);
                mTopicDetail.setIs_favourite(true);
//                mTopicDetailAdapter.updateFavStatus(true);
            }

            @Override
            public void failure(RetrofitError error) {
                makeToast("关注失败");
                RetrofitErrorUtil.handleError(TopicDetailActivity.this, error);
                mCollectBtn.setBackgroundResource(R.drawable.ic_album_nav_follow_btn);
//                mTopicDetailAdapter.updateFavStatus(false);
            }
        });
    }

    private void delFavStatus() {
        KKMHApp.getRestClient().delFavTopic(mTopicDetail.getId(), new Callback<EmptyDataResponse>() {
            @Override
            public void success(EmptyDataResponse emptyDataResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(TopicDetailActivity.this, response)){
                    return;
                }
                makeToast("取消关注成功");
                mCollectBtn.setBackgroundResource(R.drawable.ic_album_nav_follow_btn);
                mTopicDetail.setIs_favourite(false);
//                mTopicDetailAdapter.updateFavStatus(false);
            }

            @Override
            public void failure(RetrofitError error) {
                RetrofitErrorUtil.handleError(TopicDetailActivity.this, error);
                makeToast("取消关注失败");
//                mTopicDetailAdapter.updateFavStatus(true);
            }
        });
    }

    private void makeToast(String toast) {
        try{
            Toast.makeText(TopicDetailActivity.this, toast, Toast.LENGTH_SHORT).show();
        }catch(NullPointerException e){

        }

    }

    private void loadData() {
        KKMHApp.getRestClient().getTopicDetail(topicID, 0, new Callback<TopicDetail>() {
            @Override
            public void success(TopicDetail topicDetail, Response response) {
                if(RetrofitErrorUtil.handleResponse(TopicDetailActivity.this, response)){
                    return;
                }
                mTopicDetail = topicDetail;
                try{
                    TopicDetailFragment topicDetailFragment = TopicDetailFragment.newInstance(topicDetail);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content_main, topicDetailFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    mCollectBtn.setBackgroundResource(mTopicDetail.is_favourite() ? R.drawable.ic_album_nav_followed_btn : R.drawable.ic_album_nav_follow_btn);
                }catch(IllegalStateException e){}
            }

            @Override
            public void failure(RetrofitError error) {
                RetrofitErrorUtil.handleError(TopicDetailActivity.this, error);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
