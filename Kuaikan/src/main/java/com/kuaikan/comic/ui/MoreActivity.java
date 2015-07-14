package com.kuaikan.comic.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.TextView;

import com.kuaikan.comic.R;
import com.kuaikan.comic.ui.fragment.MoreFragment;
import com.umeng.analytics.MobclickAgent;

import timber.log.Timber;

/**
 * 更多设置界面
 */

public class MoreActivity extends BaseActivity {

    private MoreFragment moreFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        Timber.tag(TopicDetailActivity.class.getSimpleName());
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_up_indicator);
        TextView title = (TextView)getActionBarToolbar().findViewById(R.id.toolbar_center_title);
        title.setText("设置");

        moreFragment = MoreFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_main, moreFragment);
        fragmentTransaction.commit();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                break;
        }
        return false;
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
