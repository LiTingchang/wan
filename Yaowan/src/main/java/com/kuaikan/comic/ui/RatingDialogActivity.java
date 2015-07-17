package com.kuaikan.comic.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

import com.kuaikan.comic.R;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.kuaikan.comic.util.UIUtil;

public class RatingDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rating_dialog);

        showDialog();
        PreferencesStorageUtil.setRatingDialogShow(this);
    }


    private void showDialog() {
        Dialog ratingDialog = new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("求个鼓励！").setMessage("喜欢我，请给我个好评吧！一个简单的好评，会让我们更有动力做得更好！谢谢！")
                .setPositiveButton("喜欢！给好评！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UIUtil.startMarket(RatingDialogActivity.this);
                        RatingDialogActivity.this.finish();
                    }
                }).setNegativeButton("残忍拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RatingDialogActivity.this.finish();

                    }
                }).setNeutralButton("好吧，给点建议～", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UIUtil.startMarket(RatingDialogActivity.this);
                        RatingDialogActivity.this.finish();
                    }
                }).create();
        ratingDialog.show();
        ratingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                RatingDialogActivity.this.finish();
            }
        });
    }


}
