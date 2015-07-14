package com.kuaikan.comic.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.API.RecommendAppResponse;
import com.kuaikan.comic.rest.model.App;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.UIUtil;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RecommendAppActivity extends BaseActivity {

    @InjectView(R.id.recyclerView)
    RecyclerView mRecycleView;

    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_app);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_up_indicator);

        ButterKnife.inject(this);
        mRecycleView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(RecommendAppActivity.this);
        mRecycleView.setLayoutManager(mLayoutManager);
        getRecommendAppList();
    }


    private void getRecommendAppList(){
        KKMHApp.getRestClient().getRecommendAppList(new Callback<RecommendAppResponse>() {
            @Override
            public void success(final RecommendAppResponse recommendAppResponse, Response response) {
                if(RetrofitErrorUtil.handleResponse(RecommendAppActivity.this, response)){
                    return;
                }
                mRecycleView.setAdapter(new RecommendAppAdapter(recommendAppResponse.getApps()));
            }

            @Override
            public void failure(RetrofitError error) {
                RetrofitErrorUtil.handleError(RecommendAppActivity.this, error);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class RecommendAppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<App> mList;

        public RecommendAppAdapter(List<App> apps){
            this.mList = apps;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecommendAppViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_recommend_app, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
            RecommendAppViewHolder recommendAppViewHolder = (RecommendAppViewHolder) holder;
            try {
                if(!TextUtils.isEmpty(mList.get(position).getCoverUrl())){
                    Picasso.with(RecommendAppActivity.this)
                            .load(mList.get(position).getCoverUrl())
                            .into(recommendAppViewHolder.mRecommendAppImage);
                }
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
            recommendAppViewHolder.mRecommendAppName.setText(mList.get(position).getTitle());
            recommendAppViewHolder.mRecommendAppDesc.setText(mList.get(position).getDesc());
            recommendAppViewHolder.mDownloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtils.isEmpty(mList.get(position).getPackagename())){
                        UIUtil.startMarket(RecommendAppActivity.this, mList.get(position).getPackagename());
                    }else if(!TextUtils.isEmpty(mList.get(position).getUrl())){
                        Uri uri = Uri.parse(mList.get(position).getUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if(mList != null){
                return mList.size();
            }
            return 0;
        }
    }

    public class RecommendAppViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.view_recommend_app_image)
        ImageView mRecommendAppImage;
        @InjectView(R.id.view_recommend_app_name)
        TextView mRecommendAppName;
        @InjectView(R.id.view_recommend_app_desc)
        TextView mRecommendAppDesc;
        @InjectView(R.id.view_recommend_app_download)
        TextView mDownloadBtn;

        public RecommendAppViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

}
