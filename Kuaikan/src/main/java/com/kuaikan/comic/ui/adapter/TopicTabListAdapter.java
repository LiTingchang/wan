package com.kuaikan.comic.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.Banner;
import com.kuaikan.comic.rest.model.MixTopic;
import com.kuaikan.comic.ui.TopicListActivity;
import com.kuaikan.comic.ui.view.BannerImageView;
import com.kuaikan.comic.ui.view.CirclePageIndicator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

//import static com.kuaikan.comic.ui.adapter.TopicTabListAdapter.TOPIC_ITEMS.getTopicItem;

/**
 * Created by skyfishjy on 12/25/14.
 */
public class TopicTabListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements  Runnable, ViewPager.OnPageChangeListener{

    private static final int TOPIC_TAB_HEADER_ITEM = 0;
    private static final int TOPIC_TAB_SECTION_ITEM = 1;
    private static final int TOPIC_TAB_CONTENT_ITEM_TOPIC = 2;
//    private static final int TOPIC_TAB_CONTENT_ITEM_COMIC = 3;


    Context mContext;
//    List<List<Topic>> mTopicLists;
    private List<MixTopic> mTopics;
    List<Banner> mBanners = new ArrayList<>();
    int mBannerHeight;
    private ViewPager mBannerViewPager;
    private static final int BANNER_CHANGE_DEALY = 4000;
    private static final int HANDLER_MSG_RESRESH = 1001;

    public TopicTabListAdapter(Context context,
                               List<MixTopic> topics,
                               List<Banner> banners) {
        this.mContext = context;
        this.mTopics = topics;
        this.mBanners = banners;
        this.mBannerHeight = (context.getResources().getDisplayMetrics().widthPixels * 9) >> 4;

        Timber.tag(TopicTabListAdapter.class.getSimpleName());
    }

    public void refreshDataList(List<MixTopic> topics, List<Banner> banners) {
        this.mTopics = topics;
        this.mBanners = banners;
        notifyDataSetChanged();
    }

    public void run() {
        if(mBanners.size() > 0){
            int next_num = mBannerViewPager.getCurrentItem() + 1;
            mBannerViewPager.setCurrentItem(next_num % mBanners.size());
            mBannerViewPager.postDelayed(this, BANNER_CHANGE_DEALY);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TOPIC_TAB_HEADER_ITEM:
                final RecomendViewHolder recomendViewHolder = new RecomendViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pager,parent, false));
                mBannerViewPager = recomendViewHolder.mViewPager;
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mBannerHeight);
                mBannerViewPager.setLayoutParams(layoutParams);
                recomendViewHolder.mViewPager.postDelayed(this, BANNER_CHANGE_DEALY);
                return  recomendViewHolder;
            case TOPIC_TAB_SECTION_ITEM:
                return new SectionViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_section_topic_hot, parent, false), new SectionViewHolderClickListener() {
                    @Override
                    public void onClick(int position) {
                        gotoTopicListActivity(position);
                    }
                });
            default:
                return new TopicListViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listitem_section_card_list, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position == 0){
            RecomendViewHolder recomendViewHolder = (RecomendViewHolder) holder;
            //TODO 添加图片列表
            List<BannerImageView> mViews = new ArrayList<>();
            for(Banner banner : mBanners){
                BannerImageView image = new BannerImageView(mContext, banner);
                Picasso.with(mContext)
                        .load(banner.getPic())
                        .placeholder(R.drawable.ic_common_placeholder_l)
                        .fit().centerCrop()
                        .into(image);
                mViews.add(image);
            }
            mBannerViewPager = recomendViewHolder.mViewPager;
            mBannerViewPager.setPageTransformer(true, new DepthPageTransformer());
            recomendViewHolder.mViewPager.setAdapter(new MainPageAdapter(mViews));
            recomendViewHolder.mViewPager.setCurrentItem(0);
            recomendViewHolder.mIndicator.setViewPager(recomendViewHolder.mViewPager);
        }else if (mTopics.size() > 0) {
            if ((position % 2) != 0 ) {
                SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
                sectionViewHolder.mSectionTitle.setText(mTopics.get((position - 1) / 2).getTitle());
            }else{
                TopicListViewHolder topicListViewHolder = (TopicListViewHolder) holder;
                TopicListItemAdapter topicListItemAdapter = new TopicListItemAdapter(mContext);
                LinearLayout.LayoutParams layoutParam = (LinearLayout.LayoutParams)topicListViewHolder.mRecyclerView.getLayoutParams();
                topicListViewHolder.mRecyclerView.setAdapter(topicListItemAdapter);
                if(mTopics.get((position - 1) / 2).getType() == TopicListActivity.TOPIC_LIST_TYPE_TOPIC){
                    layoutParam.height = mContext.getResources().getDimensionPixelSize(R.dimen.find_tab_recycleview_topic_recyclerview_height);
                    topicListItemAdapter.setData(mTopics.get((position - 1) / 2));
                }else{
                    layoutParam.height = mContext.getResources().getDimensionPixelSize(R.dimen.find_tab_recycleview_comic_recyclerview_height);
                    topicListItemAdapter.setData(mTopics.get((position - 1) / 2));
                }
                topicListViewHolder.mRecyclerView.requestLayout();
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        mBannerViewPager.removeCallbacks(this);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        switch (state){
//            case ViewPager.SCROLL_STATE_DRAGGING:
//                mBannerViewPager.removeCallbacks(this);
//                break;
//            case ViewPager.SCROLL_STATE_SETTLING:
//            case ViewPager.SCROLL_STATE_IDLE:
//                mBannerViewPager.postDelayed(this, BANNER_CHANGE_DEALY);
//                break;
//        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) {
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                view.setAlpha(1 - position);

                view.setTranslationX(pageWidth * -position);

                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else {
                view.setAlpha(0);
            }
        }
    }


    private void gotoTopicListActivity(int position) {
        Intent intent = new Intent(mContext, TopicListActivity.class);
        MixTopic mixTopic = mTopics.get((position - 1) / 2);
        intent.putExtra(TopicListActivity.TOPIC_LIST_TYPE_SEARCH_STR, mixTopic.getTitle() );
        intent.putExtra(TopicListActivity.TOPIC_LIST_TYPE, mixTopic.getType());
        intent.putExtra(TopicListActivity.TOPIC_LIST_TYPE_ACTION, mixTopic.getAction());
        mContext.startActivity(intent);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TOPIC_TAB_HEADER_ITEM;
        }
        if (position%2 == 0) {
            return TOPIC_TAB_CONTENT_ITEM_TOPIC;
        } else {
            return TOPIC_TAB_SECTION_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if(mTopics == null){
            return 1;
        }
        return mTopics.size() * 2 + 1;
    }

    public class TopicListViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.recyclerView)
        RecyclerView mRecyclerView;

        public TopicListViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,RecyclerView.HORIZONTAL,false));
        }
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.section_title)
        TextView mSectionTitle;
        @InjectView(R.id.section_more)
        TextView mSectionMore;

        SectionViewHolderClickListener sectionViewHolderClickListener;

        public SectionViewHolder(View view, SectionViewHolderClickListener listener) {
            super(view);
            ButterKnife.inject(this, view);
            this.sectionViewHolderClickListener = listener;
            mSectionMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            sectionViewHolderClickListener.onClick(getPosition());
        }
    }

    public interface SectionViewHolderClickListener {
        public void onClick(int position);
    }

    public static class RecomendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @InjectView(R.id.kuaikan_viewpager)
        ViewPager mViewPager;
        @InjectView(R.id.indicator)
        CirclePageIndicator mIndicator;

        public RecomendViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            mIndicator.setRadius(8);
            mViewPager.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            mViewPager.getCurrentItem();
        }

        public static interface RecomendViewHolderClickListener {
            public void onClick();
        }
    }

}
