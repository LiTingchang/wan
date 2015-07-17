package com.kuaikan.comic.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.dao.bean.SearchHistory;
import com.kuaikan.comic.db.HistoryDaoHelper;
import com.kuaikan.comic.rest.RestClient;
import com.kuaikan.comic.rest.model.API.SuggestionListResponse;
import com.kuaikan.comic.rest.model.API.TopicListResponse;
import com.kuaikan.comic.ui.adapter.SearchAdapter;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class SearchActivity extends Activity implements View.OnClickListener, TextWatcher{

    private static final int SEARCH_DEFAULT_OFFSET = 0;
    private static final int SEARCH_DEFAULT_LIMIT = 20;

    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @InjectView(R.id.search_bar_cancel)
    TextView mCancleBtn;
    @InjectView(R.id.ic_search_searchbar_del)
    TextView mSearchBarDel;
    @InjectView(R.id.search_input)
    EditText mInputEdt;

    private InputMethodManager mInputMethodManager;

    private SearchAdapter mSearchAdapter;

    private List<String> mSearchHistory = new ArrayList<>();
    private SuggestionListResponse mSuggestionListResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(LoginActivity.class.getSimpleName());

        setContentView(R.layout.activity_search);

        ButterKnife.inject(this);

        mCancleBtn.setOnClickListener(this);
        mSearchBarDel.setOnClickListener(this);

        initListView();

        if(mInputMethodManager == null){
            mInputMethodManager = (InputMethodManager) SearchActivity.this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        mInputEdt.requestFocus();
        mInputEdt.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((mInputMethodManager != null) && (mInputEdt != null)) {
                    mInputMethodManager.showSoftInput(mInputEdt, 0);
                }
            }
        }, 500);

        mInputEdt.addTextChangedListener(this);
        mInputEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    Toast.makeText(SearchActivity.this, "1111111", Toast.LENGTH_SHORT).show();
                    String searchTxt = mInputEdt.getText().toString().trim();
                    if(!TextUtils.isEmpty(searchTxt)){
                        boolean has = false;
                        for (String s : mSearchHistory) {
                            if (s.equals(searchTxt)) {
                                has = true;
                            }
                        }
                        if (!has) {
                            mSearchHistory.add(searchTxt);
                            SearchHistory searchHistory = new SearchHistory();
                            searchHistory.setSearch_content(searchTxt);
                            HistoryDaoHelper.getInstance().addData(searchHistory);
                        }
                        gotoTopicListActivity(searchTxt);
//                        searchTopic(searchTxt);
                    }
                }

                return false;
            }
        });
        @SuppressWarnings("unchecked")
        List<SearchHistory> searchList = HistoryDaoHelper.getInstance().getAllData();
        for(SearchHistory searchHistory: searchList){
            mSearchHistory.add(searchHistory.getSearch_content());
        }
        getCategoryData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HistoryDaoHelper.getInstance().deleteAll();
        for(String searchStr: mSearchHistory){
            SearchHistory searchHistory = new SearchHistory();
            searchHistory.setSearch_content(searchStr);
            HistoryDaoHelper.getInstance().addData(searchHistory);
        }
    }

    private void initListView(){
        mRecyclerView.setHasFixedSize(true);
//        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 5);
//        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        mSearchAdapter = new SearchAdapter(SearchActivity.this);
        mRecyclerView.setAdapter(mSearchAdapter);

//        List<SearchHistory> searchHistory = HistoryDaoHelper.getInstance().getAllData();
//        HistoryAdapter historyAdapter = new HistoryAdapter();
//        mListView.setAdapter(historyAdapter);
//        historyAdapter.initData(searchHistory);
    }

    private void getCategoryData(){
            KKMHApp.getRestClient().getSuggestionTag(new Callback<SuggestionListResponse>() {
                @Override
                public void success(SuggestionListResponse suggestionListResponse, Response response) {
                    if(RetrofitErrorUtil.handleResponse(SearchActivity.this, response)){
                        return;
                    }
                    if(suggestionListResponse.getSuggestion() != null & suggestionListResponse.getSuggestion().size() > 0){
                        mSuggestionListResponse = suggestionListResponse;
                        mSearchAdapter.initData(mSuggestionListResponse.getSuggestion(), mSearchHistory);
                    }else{
                        mSearchAdapter.initEmptyData();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    RetrofitErrorUtil.handleError(SearchActivity.this, error);
                }
            });

    }

    private void gotoTopicListActivity(String searchString){
        Intent intent = new Intent();
        intent.setClass(SearchActivity.this, TopicListActivity.class);
        intent.putExtra(TopicListActivity.TOPIC_LIST_TYPE, TopicListActivity.TOPIC_LIST_TYPE_SEARCH);
        intent.putExtra(TopicListActivity.TOPIC_LIST_TYPE_SEARCH_STR, searchString);
        SearchActivity.this.startActivity(intent);
    }

    private void searchTopic(String searchString){
//        Intent intent = new Intent();
//        intent.setClass(SearchActivity.this, TopicListActivity.class);
//        intent.putExtra(TopicListActivity.TOPIC_LIST_TYPE, TopicListActivity.TOPIC_LIST_TYPE_SEARCH_TAG);
//        intent.putExtra(TopicListActivity.TOPIC_LIST_TYPE_SEARCH_TAG, searchString);
//        SearchActivity.this.startActivity(intent);

        KKMHApp.getRestClient().searchTopic(searchString, RestClient.DEFAULT_OFFSET, RestClient.DEFAULT_SEARCH_LIMIT, new Callback<TopicListResponse>() {
            @Override
            public void success(TopicListResponse topicListResponse, Response response) {
//                TODO 搜索结果填充
                if(RetrofitErrorUtil.handleResponse(SearchActivity.this, response)){
                    return;
                }
                if(topicListResponse != null && topicListResponse.getTopics().size() > 0){
                    mSearchAdapter.initResultData(topicListResponse.getTopics());
                }else{
                    mSearchAdapter.initEmptyData();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mSearchAdapter.initEmptyData();
                RetrofitErrorUtil.handleError(SearchActivity.this, error);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_bar_cancel:
                String input = mInputEdt.getText().toString().trim();
                if(!TextUtils.isEmpty(input)){
                    mInputEdt.setText("");
                }else{
                    SearchActivity.this.finish();
                }
                break;
            case R.id.ic_search_searchbar_del:
                mInputEdt.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String text = s.toString().trim();
        if (!TextUtils.isEmpty(text)) {
            if (mSearchBarDel.getVisibility() != View.VISIBLE) {
                mSearchBarDel.setVisibility(View.VISIBLE);
            }
            searchTopic(text);
        } else {
            if (mSearchBarDel.getVisibility() != View.GONE) {
                mSearchBarDel.setVisibility(View.GONE);
            }
            if(mSuggestionListResponse != null){
                mSearchAdapter.initData(mSuggestionListResponse.getSuggestion(), mSearchHistory);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

}
