package com.kuaikan.comic.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.kuaikan.comic.rest.model.Banner;
import com.kuaikan.comic.ui.ComicDetailActivity;
import com.kuaikan.comic.ui.TopicDetailActivity;
import com.kuaikan.comic.ui.TopicListActivity;
import com.kuaikan.comic.ui.WebViewActivity;

/**
 * Created by a on 2015/4/1.
 */
public class BannerImageView extends ImageView implements View.OnClickListener{
    private Banner mBanner;

    public BannerImageView(Context context) {
        super(context);
    }

    public BannerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BannerImageView(Context context, Banner banner) {
        super(context);
        this.mBanner = banner;
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mBanner != null){
            switch (mBanner.getType()){
                case Banner.TYPE_URL:
                    Intent webViewIntent = new Intent(getContext(), WebViewActivity.class);
                    webViewIntent.putExtra(WebViewActivity.WEBVIEW_TITLE_FLAG, mBanner.getTitle());
                    webViewIntent.putExtra(WebViewActivity.WEBVIEW_URL_FLAG, mBanner.getValue());
                    webViewIntent.putExtra(WebViewActivity.COVER_IMAGE_URL, mBanner.getPic());
                    getContext().startActivity(webViewIntent);
                    break;
                case Banner.TYPE_TOPIC:
                    Intent topicIntent = new Intent(getContext(), TopicDetailActivity.class);
                    topicIntent.putExtra("topic_id",Long.valueOf(mBanner.getValue()));
                    getContext().startActivity(topicIntent);
                    break;
                case Banner.TYPE_COMIC:
                    Intent ComicIntent = new Intent(getContext(), ComicDetailActivity.class);
                    ComicIntent.putExtra("comic_id", Long.valueOf(mBanner.getValue()));
                    ComicIntent.putExtra("comic_title", mBanner.getTitle());
                    getContext().startActivity(ComicIntent);
                    break;
                case Banner.TYPE_CATEGORY:
                    Intent CategoryIntent = new Intent(getContext(), TopicListActivity.class);
                    CategoryIntent.putExtra(TopicListActivity.TOPIC_LIST_TYPE, TopicListActivity.TOPIC_LIST_TYPE_TAG);
                    CategoryIntent.putExtra(TopicListActivity.TOPIC_LIST_TYPE_SEARCH_STR, mBanner.getValue());
                    getContext().startActivity(CategoryIntent);
                    break;
                default:
                    break;
            }
        }
    }
}
