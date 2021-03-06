package com.kuaikan.comic.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaikan.comic.R;

/**
 * Created by skyfishjy on 1/11/15.
 */
public class GuideImageFragment extends Fragment {

    private static final String KEY_GUIDE_IMAGE_BG_ID = "guide_image_bg_id";
    private static final String KEY_GUIDE_IMAGE_RES_ID = "guide_image_res_id";
    private static final String KEY_GUIDE_IMAGE_STR_TOP = "guide_text_top";
    private static final String KEY_GUIDE_IMAGE_STR_BOTTOM = "guide_text_bottom";


    private int mBgId;
    private int imageResId;
    private String mTopStr;
    private String mBottomStr;


    public static GuideImageFragment newInstance(int bgId, int imageResId, String top, String bottom) {
        GuideImageFragment guideImageFragment = new GuideImageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_GUIDE_IMAGE_BG_ID, bgId);
        bundle.putInt(KEY_GUIDE_IMAGE_RES_ID, imageResId);
        bundle.putString(KEY_GUIDE_IMAGE_STR_TOP, top);
        bundle.putString(KEY_GUIDE_IMAGE_STR_BOTTOM, bottom);
        guideImageFragment.setArguments(bundle);
        return guideImageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBgId = getArguments().getInt(KEY_GUIDE_IMAGE_BG_ID, -1);
        imageResId = getArguments().getInt(KEY_GUIDE_IMAGE_RES_ID, -1);
        mTopStr = getArguments().getString(KEY_GUIDE_IMAGE_STR_TOP);
        mBottomStr = getArguments().getString(KEY_GUIDE_IMAGE_STR_BOTTOM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_guide, container, false);
        view.setBackgroundResource(mBgId);
        ImageView guideIV = (ImageView) view.findViewById(R.id.guide_image);
        guideIV.setImageResource(imageResId);
        TextView textBottom = (TextView) view.findViewById(R.id.guide_text_bottom);
        textBottom.setText(mBottomStr);
        TextView textTop = (TextView) view.findViewById(R.id.guide_text_top);
        textTop.setText(mTopStr);
        return view;
    }
}
