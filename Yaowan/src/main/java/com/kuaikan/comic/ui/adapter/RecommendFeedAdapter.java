package com.kuaikan.comic.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.RestClient;
import com.kuaikan.comic.rest.model.Comic;
import com.kuaikan.comic.ui.AuthorActivity;
import com.kuaikan.comic.ui.ComicDetailActivity;
import com.kuaikan.comic.ui.TopicDetailActivity;
import com.kuaikan.comic.util.DateUtil;
import com.kuaikan.comic.util.LogUtil;
import com.kuaikan.comic.util.UIUtil;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by skyfishjy on 12/19/14.
 */
public class RecommendFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SECTION_TYPE = 0;
    private static final int ITEM_TYPE = 1;
    SparseArray<Section> mSections = new SparseArray<Section>();
    List<Comic> comicList;
    Context mContext;
    ComicRefreshListener comicRefreshListener;
    public ComicOperationListener mComicOperationListener;
    int currentComicOffset;
    Calendar todayCalendar;
    private Transformation roundedTransformation;

    private enum TAB_KUAIKAN_CATEGORY{
        TAB_ATTENTION,
        TAB_RECOMMEND
    }

    public RecommendFeedAdapter(Context context, List<Comic> list, ComicRefreshListener listener, ComicOperationListener comicOperationListener) {
        this.mContext = context;
        comicList = list;
        Timber.tag(RecommendFeedAdapter.class.getSimpleName());
//        roundedTransformation = new RoundedTransformation(UIUtil.dp2px(mContext, 2), RoundedTransformation.Corners.TOP);
        this.mComicOperationListener = comicOperationListener;
        this.comicRefreshListener = listener;
        this.currentComicOffset = RestClient.DEFAULT_OFFSET;
        this.todayCalendar = Calendar.getInstance();
        roundedTransformation = new RoundedTransformationBuilder()
                .borderWidthDp(0)
                .cornerRadius(mContext.getResources().getDimensionPixelSize(R.dimen.comic_author_avatar) / 2)
                .oval(false)
                .build();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SECTION_TYPE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_section_day_recom, parent, false);
            return new SectionViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_comic_day_recom, parent, false);
            ComicViewHolder comicViewHolder = new ComicViewHolder(view);
            return comicViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LogUtil.d("position----------->" + position);
        switch (getItemViewType(position)) {
            case SECTION_TYPE:
    //        if (isSectionHeaderPosition(position)) {
                LogUtil.d("Sectionposition->" + position);
                SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
                Section section = mSections.get(position);
                if (section.isSameData(todayCalendar)) {
                    sectionViewHolder.isTodayTV.setVisibility(View.VISIBLE);
                    sectionViewHolder.dayMonthTV.setVisibility(View.GONE);
                    sectionViewHolder.weekTV.setVisibility(View.GONE);
                } else {
                    sectionViewHolder.isTodayTV.setVisibility(View.GONE);
                    sectionViewHolder.dayMonthTV.setVisibility(View.VISIBLE);
                    sectionViewHolder.weekTV.setVisibility(View.VISIBLE);
                    sectionViewHolder.dayMonthTV.setText(section.dateDay + "更新");
                    sectionViewHolder.weekTV.setText(section.dateMonthWeek);
                }
                    break;
    //        } else {
            case ITEM_TYPE:
                ComicViewHolder comicViewHolder = (ComicViewHolder) holder;
                int realPosition = sectionedPositionToPosition(position);
                LogUtil.d("realPosition---->" + realPosition );
                Comic comic = comicList.get(realPosition);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //                comicViewHolder.cardView.setPreventCornerOverlap(true);
                    Picasso.with(mContext)
                            .load(comic.getCover_image_url())
                            .placeholder(R.drawable.ic_common_placeholder_l)
                            .into(comicViewHolder.coverIV);
                } else {
                    Picasso.with(mContext)
                            .load(comic.getCover_image_url())
                            .fit().centerCrop()
                            .placeholder(R.drawable.ic_common_placeholder_l)
                            .into(comicViewHolder.coverIV);
                }

                comicViewHolder.topicTitleTV.setText(comic.getTopic().getTitle());
                comicViewHolder.comicTitleTV.setText(comic.getTitle());
    //            String commentCount;
    //            if (comic.getComments_count() < 100000) {
    //                commentCount = " " + comic.getComments_count();
    //            } else {
    //                commentCount = " " + comic.getComments_count() / 10000 + "W";
    //            }

                comicViewHolder.comicCommentTV.setText(UIUtil.getCalculatedCount(comic.getComments_count()));
    //            String likesCount;
    //            if (comic.getLikes_count() < 100000) {
    //                likesCount = " " + comic.getLikes_count();
    //            } else {
    //                likesCount = " " + comic.getLikes_count() / 10000 + "W";
    //            }
                comicViewHolder.comicLikeIcon.setImageResource(comic.is_liked() ? R.drawable.ic_home_praise_pressed : R.drawable.ic_home_praise_normal);
                comicViewHolder.comicLikesCB.setText(UIUtil.getCalculatedCount(comic.getLikes_count()));

                comicViewHolder.comicShareTV.setText(UIUtil.getCalculatedCount(comic.getShare_count()));
                comicViewHolder.mAuthorName.setText(comic.getTopic().getUser().getNickname());
                if(!TextUtils.isEmpty(comic.getTopic().getUser().getAvatar_url())){
                    Picasso.with(mContext).load(comic.getTopic().getUser().getAvatar_url())
                            .resize(mContext.getResources().getDimensionPixelSize(R.dimen.comic_author_avatar), mContext.getResources().getDimensionPixelSize(R.dimen.comic_author_avatar))
                            .transform(roundedTransformation)
                            .into(comicViewHolder.mAvatarImg);
                }
                final long userId = comic.getTopic().getUser().getId();

                View.OnClickListener authorOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, AuthorActivity.class);
                        intent.putExtra(AuthorActivity.KEY_AUTHOR_ID, userId);
                        mContext.startActivity(intent);
                    }
                };

                comicViewHolder.mAvatarImg.setOnClickListener(authorOnClickListener);
                comicViewHolder.mAuthorName.setOnClickListener(authorOnClickListener);
                break;

            }
        if (position == getItemCount() - 5) {
            comicRefreshListener.onLoadMoreComic(currentComicOffset += RestClient.DEFAULT_LIMIT);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE : ITEM_TYPE;
    }

    public void refresh(List<Comic> list) {
        comicList.clear();
        mSections.clear();
        addAll(list, true);
        notifyDataSetChanged();
    }

    public void addAll(List<Comic> list, boolean isRefresh) {
        int startIndex = comicList.size();
        comicList.addAll(startIndex, list);
        int oldLength = getItemCount();
        List<Section> sectionList = new ArrayList<>();
        if (comicList != null && comicList.size() > 0) {
            Comic firstComic = comicList.get(0);
            Calendar sectionCalendar = DateUtil.convertUNIX2Calendar(firstComic.getCreated_at());
            sectionList.add(new Section(0, DateUtil.formatSectionHeaderDate(sectionCalendar), DateUtil.formatSectionHeaderWeek(sectionCalendar), sectionCalendar));
            if (comicList.size() > 1) {
                for (int i = 1; i < comicList.size(); i++) {
                    Comic nextComic = comicList.get(i);
                    Calendar nextCalendar = DateUtil.convertUNIX2Calendar(nextComic.getCreated_at());
                    if ((nextCalendar.get(Calendar.YEAR) == sectionCalendar.get(Calendar.YEAR))
                            && (nextCalendar.get(Calendar.DAY_OF_YEAR) == sectionCalendar.get(Calendar.DAY_OF_YEAR))) {
                    } else {
                        sectionCalendar = nextCalendar;
                        sectionList.add(new Section(i, DateUtil.formatSectionHeaderDate(sectionCalendar), DateUtil.formatSectionHeaderWeek(sectionCalendar), sectionCalendar));
                    }
                }
            }
            Section[] dummy = new Section[sectionList.size()];
            setSections(sectionList.toArray(dummy));
        }
        int newLength = mSections.size() + comicList.size();
        if (isRefresh) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeInserted(oldLength, newLength - oldLength);//notifyItemRangeInserted(startIndex, newLength - oldLength);
        }
    }

    public void setSections(Section[] sections) {
        mSections.clear();
        Arrays.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return (o.firstPosition == o1.firstPosition)
                        ? 0
                        : ((o.firstPosition < o1.firstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            mSections.append(section.sectionedPosition, section);
            ++offset;
        }
    }
//
//    public int positionToSectionedPosition(int position) {
//        int offset = 0;
//        for (int i = 0; i < mSections.size(); i++) {
//            if (mSections.valueAt(i).firstPosition > position) {
//                break;
//            }
//            ++offset;
//        }
//        return position + offset;
//    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }

        return sectionedPosition + offset;
    }

//    @Override
//    public long getItemId(int position) {
//        super.getItemId(position);
//        return isSectionHeaderPosition(position)
//                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
//                : sectionedPositionToPosition(position);
////        return position;
//    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }

    @Override
    public int getItemCount() {
        return comicList.size() + mSections.size();
    }

    public void setCurrentComicOffset(int offset) {
        this.currentComicOffset = offset;
    }

    public interface ComicRefreshListener {
        public void onLoadMoreComic(int newCurrentOffset);
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.date_month_day)
        TextView dayMonthTV;
        @InjectView(R.id.date_week)
        TextView weekTV;
        @InjectView(R.id.date_is_today)
        TextView isTodayTV;

        public SectionViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public class ComicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.card_view)
        LinearLayout cardView;
        @InjectView(R.id.comic_detail_action_share)
        LinearLayout shareLayout;
        @InjectView(R.id.comic_detail_action_comment)
        LinearLayout commentLayout;
        @InjectView(R.id.comic_topic_text)
        TextView topicText;
        @InjectView(R.id.cover_image)
        ImageView coverIV;
        @InjectView(R.id.topic_title)
        TextView topicTitleTV;
        @InjectView(R.id.comic_title)
        TextView comicTitleTV;
        @InjectView(R.id.comic_list_share_tv)
        TextView comicShareTV;
        @InjectView(R.id.comic_comment_count)
        TextView comicCommentTV;
        @InjectView(R.id.comic_detail_action_like)
        LinearLayout likeLayout;
        @InjectView(R.id.comic_likes_count)
        TextView comicLikesCB;
        @InjectView(R.id.comic_like_ic)
        ImageView comicLikeIcon;
        @InjectView(R.id.comic_author_avatar)
        ImageView mAvatarImg;
        @InjectView(R.id.comic_author_name)
        TextView mAuthorName;

        public ComicViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            coverIV.setOnClickListener(this);
            commentLayout.setOnClickListener(this);
            shareLayout.setOnClickListener(this);
//            comicLikesCB.setOnClickListener(this);
//            comicLikesCB.setText(comicList.get(sectionedPositionToPosition(getPosition())).getLikes_count() + "");
            topicText.setOnClickListener(this);
            topicTitleTV.setOnClickListener(this);
            likeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.comic_detail_action_share:
                    mComicOperationListener.onShareOperation(comicList.get(sectionedPositionToPosition(getPosition())));
                    break;
                case R.id.comic_detail_action_comment:
                    mComicOperationListener.onCommentOperation(comicList.get(sectionedPositionToPosition(getPosition())).getId());
                    break;
                case R.id.comic_detail_action_like:
                    mComicOperationListener.onLikeOperation(comicList.get(sectionedPositionToPosition(getPosition())), comicLikeIcon, comicLikesCB);
                    break;
                case R.id.cover_image:
                    Comic comic = comicList.get(sectionedPositionToPosition(getPosition()));
                    Intent intentComic = new Intent(mContext, ComicDetailActivity.class);
                    intentComic.putExtra("comic_id", comic.getId());
                    intentComic.putExtra("comic_title", comic.getTitle());
                    mContext.startActivity(intentComic);
                    break;
                case R.id.comic_topic_text:
                case R.id.topic_title:
                    Intent intentTopic = new Intent(mContext, TopicDetailActivity.class);
                    intentTopic.putExtra(TopicDetailActivity.INTENT_TOPIC_ID, comicList.get(sectionedPositionToPosition(getPosition())).getTopic().getId());
                    mContext.startActivity(intentTopic);
                default :
                    break;

            }

//            if (view instanceof CardView)
//                mListener.onClick((getPosition()));
        }

    }

    public static class Section {
        int firstPosition;
        int sectionedPosition;
        CharSequence dateDay;
        CharSequence dateMonthWeek;
        Calendar sectionCalendar;

        public Section(int firstPosition, CharSequence day, CharSequence monthWeek, Calendar sectionCalendar) {
            this.firstPosition = firstPosition;
            this.dateDay = day;
            this.dateMonthWeek = monthWeek;
            this.sectionCalendar = sectionCalendar;
        }

        public boolean isSameData(Calendar calendar) {
            return sectionCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && sectionCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
        }


    }

    public interface ComicOperationListener{
        public void onShareOperation(Comic comic);
        public void onLikeOperation(Comic comic, ImageView likeIcon, TextView likeCount);
        public void onCommentOperation(long comic_id);
    }

}
