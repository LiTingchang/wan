package com.kuaikan.comic.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by skyfishjy on 2/14/15.
 */
public class ShowDeleteFavDialogFragment extends DialogFragment {
    private AlertDialogClickListener alertDialogClickListener;

    public static ShowDeleteFavDialogFragment newInstance() {
        ShowDeleteFavDialogFragment showDeleteFavDialogFragment = new ShowDeleteFavDialogFragment();
        Bundle bundle = new Bundle();
        showDeleteFavDialogFragment.setArguments(bundle);
        return showDeleteFavDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setAlertDialogClickListener(AlertDialogClickListener listener) {
        this.alertDialogClickListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确定删除该收藏吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (alertDialogClickListener != null) {
                            alertDialogClickListener.onPositiveButtonClick();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (alertDialogClickListener != null) {
                            alertDialogClickListener.onNegativeButtonClick();
                        }
                    }
                });
        return builder.create();
    }

    public interface AlertDialogClickListener {
        public void onPositiveButtonClick();

        public void onNegativeButtonClick();
    }
}
