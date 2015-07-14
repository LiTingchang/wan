package com.kuaikan.comic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.TextView;

import com.kuaikan.comic.R;
import com.kuaikan.comic.ui.fragment.TopicListFragment;
import com.umeng.analytics.MobclickAgent;

import timber.log.Timber;

public class TopicListActivity extends BaseActivity {

    public static final int TOPIC_LIST_TYPE_TOPIC = 0;
    public static final int TOPIC_LIST_TYPE_COMIC = 1;
    public static final int TOPIC_LIST_TYPE_SEARCH = 4;
    public static final int TOPIC_LIST_TYPE_TAG = 5;
    public static final String TOPIC_LIST_TYPE = "topic_list_search_type";
    public static final String TOPIC_LIST_TYPE_SEARCH_STR = "topic_list_search_str";
    public static final String TOPIC_LIST_TYPE_ACTION = "topic_list_type_action";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);

        Timber.tag(TopicDetailActivity.class.getSimpleName());
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_up_indicator);
        TextView title = (TextView)getActionBarToolbar().findViewById(R.id.toolbar_center_title);

        Intent intent = getIntent();
        int type = TOPIC_LIST_TYPE_SEARCH;
        String tag = "";
        String action = "";
        if(intent != null){
            type = intent.getIntExtra(TOPIC_LIST_TYPE, TOPIC_LIST_TYPE_SEARCH);
            tag = intent.getStringExtra(TOPIC_LIST_TYPE_SEARCH_STR);
            action = intent.getStringExtra(TOPIC_LIST_TYPE_ACTION);
            title.setText(tag);
        }

        TopicListFragment topicListFragment = TopicListFragment.newInstance(type, tag, action);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_main, topicListFragment);
        fragmentTransaction.commit();
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
