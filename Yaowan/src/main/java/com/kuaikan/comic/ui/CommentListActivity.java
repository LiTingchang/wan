package com.kuaikan.comic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.RadioGroup;

import com.kuaikan.comic.R;
import com.kuaikan.comic.ui.fragment.CommentTabListFragment;
import com.umeng.analytics.MobclickAgent;

import timber.log.Timber;

public class CommentListActivity extends BaseActivity{

    public static final String COMMENT_COMIC_ID = "comic_id";

    CommentTabListFragment mCommentTabListFragment;

    private long mComicId;

    RadioGroup mCommentTabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        Timber.tag(CommentListActivity.class.getSimpleName());
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_nav_delete);
        mCommentTabHost = (RadioGroup)getActionBarToolbar().findViewById(R.id.comment_list_tab_layout);

        initView();
    }

    private void initView(){
        Intent intent = getIntent();
        if(intent == null){
            return ;
        }
        mComicId = intent.getLongExtra(COMMENT_COMIC_ID, 0l);
        if(mComicId != 0l){
            mCommentTabListFragment = CommentTabListFragment.newInstance(mComicId, new CommentTabViewPagerOnPageChangeListener());
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_main, mCommentTabListFragment);
            fragmentTransaction.commit();
            mCommentTabHost.check(R.id.comment_list_newest_tab);
            mCommentTabHost.setOnCheckedChangeListener(mCommentTabListFragment.onCheckedChangeListener);
        }

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

    public class CommentTabViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mCommentTabHost.check(position == 0 ? R.id.comment_list_newest_tab : R.id.comment_list_hottest_tab);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("评论列表");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("评论列表");
    }

}
