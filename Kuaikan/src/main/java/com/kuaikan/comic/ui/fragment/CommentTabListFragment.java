package com.kuaikan.comic.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.Comment;
import com.kuaikan.comic.ui.adapter.SmartFragmentStatePagerAdapter;
import com.kuaikan.comic.ui.view.SoftKeyboard;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.UIUtil;
import com.kuaikan.comic.util.UserUtil;
import com.rockerhieu.emojicon.EmojiconEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CommentTabListFragment extends Fragment {
    public static final String TAG = CommentTabListFragment.class.getSimpleName();

    @InjectView(R.id.comment_list_viewpager)
    ViewPager mCommentListViewPager;
//    @InjectView(R.id.comment_list_tab_layout)
//    RadioGroup mCommentTabHost;

    @InjectView(R.id.comic_comment_list_edittext)
    EmojiconEditText mCommentEdt;
    @InjectView(R.id.comic_comment_list_send)
    TextView mSendTv;
    @InjectView(R.id.comment_list_below_ly)
    LinearLayout mCommentLayout;

    CommentListFragment newestCommentListFragment;
    CommentListFragment hottestCommentListFragment;
    List<Fragment> mCommentFragmentList;
    private long mComicId;
    CommentPagerAdapter mCommentPagerAdapter;
    ProgressDialog progressDialog;
    ViewPager.OnPageChangeListener mOnPageChangeListener;
    SoftKeyboard softKeyboard;
    InputMethodManager imm;

    public RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId) {
                case R.id.comment_list_newest_tab:
                    newestCommentListFragment.refreshData();
                    mCommentListViewPager.setCurrentItem(0);
                    break;
                case R.id.comment_list_hottest_tab:
                    hottestCommentListFragment.refreshData();
                    mCommentListViewPager.setCurrentItem(1);
                    break;
            }
        }
    };

    public void setComicId(long mComicId) {
        this.mComicId = mComicId;
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener mOnPageChangeListener) {
        this.mOnPageChangeListener = mOnPageChangeListener;
    }

    public static CommentTabListFragment newInstance(long comicId, ViewPager.OnPageChangeListener onPageChangeListener) {
        CommentTabListFragment fragment = new CommentTabListFragment();
        fragment.setComicId(comicId);
        fragment.setOnPageChangeListener(onPageChangeListener);
        return fragment;
    }

    public CommentTabListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCommentFragmentList = new ArrayList<>();
        newestCommentListFragment = CommentListFragment.newInstance(mComicId, CommentListFragment.COMMENT_TAB.NEWEST);
        mCommentFragmentList.add(newestCommentListFragment);
        hottestCommentListFragment = CommentListFragment.newInstance(mComicId, CommentListFragment.COMMENT_TAB.HOTEST);
        mCommentFragmentList.add(hottestCommentListFragment);

    }

    TextWatcher textWatcher = new TextWatcher() {
        private int editStart;
        private int editEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                      int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = mCommentEdt.getSelectionStart();
            editEnd = mCommentEdt.getSelectionEnd();
            if (s.length() >= 300) {
                UIUtil.showThost(getActivity(), "评论字数不能超过300字");
                s.delete(editStart - 1, editEnd);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_tab_list, container, false);
        ButterKnife.inject(this, view);

        mCommentPagerAdapter = new CommentPagerAdapter(getChildFragmentManager(), mCommentFragmentList);
        mCommentListViewPager.setAdapter(mCommentPagerAdapter);
        mCommentListViewPager.setOnPageChangeListener(mOnPageChangeListener);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(mCommentLayout, imm);

        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
            }

            @Override
            public void onSoftKeyboardShow() {
            }
        });
        mSendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentContent = mCommentEdt.getText().toString().trim();
                if (!TextUtils.isEmpty(commentContent)) {
                    if (UserUtil.checkUserLogin(getActivity())){//accessToken.isSessionValid()) {
                        softKeyboard.closeSoftKeyboard();
                        progressDialog.show();
                        mSendTv.setEnabled(false);
                        KKMHApp.getRestClient().postComment(mComicId, commentContent, new Callback<Comment>() {
                            @Override
                            public void success(Comment comment, Response response) {
                                ((CommentListFragment)mCommentPagerAdapter.getItem(mCommentListViewPager.getCurrentItem())).refreshData();
                                UIUtil.showThost(getActivity(), "评论成功");
                                mCommentEdt.setText("");
                                progressDialog.dismiss();
                                mSendTv.setEnabled(true);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                progressDialog.dismiss();
                                RetrofitErrorUtil.handleError(getActivity(), error);
                                mSendTv.setEnabled(true);
                            }
                        });
                    }

                } else {
                    Toast.makeText(getActivity(), "请输入评论内容", Toast.LENGTH_SHORT).show();
                }
            }
        });
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在评论");
        progressDialog.setCancelable(false);
        mCommentEdt.addTextChangedListener(textWatcher);
        return view;

    }


    public static class CommentPagerAdapter extends SmartFragmentStatePagerAdapter {
        List<Fragment> fragmentList;

        public CommentPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragmentList = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

}
