package com.kuaikan.comic.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaikan.comic.KKMHApp;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.API.EditProfileResponse;
import com.kuaikan.comic.rest.model.SignUserInfo;
import com.kuaikan.comic.util.FileUtil;
import com.kuaikan.comic.util.ImageUtil;
import com.kuaikan.comic.util.PreferencesStorageUtil;
import com.kuaikan.comic.util.RetrofitErrorUtil;
import com.kuaikan.comic.util.UIUtil;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditProfileActivity extends BaseActivity implements View.OnClickListener{

    // 设置头像
    private static final int IMAGE_REQUEST_CODE = 0; // 请求码 本地图片
    private static final int CAMERA_REQUEST_CODE = 1; // 拍照
    private static final int RESULT_REQUEST_CODE = 2; // 裁剪
    private static final String SAVE_AVATORNAME = "head.jpeg";// 保存的图片名

    @InjectView(R.id.edit_profile_avatar)
    ImageView mAvatarImg;
    @InjectView(R.id.edit_profile_name)
    EditText mProfileName;

    private ProgressDialog progressDialog;
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBarToolbar().setNavigationIcon(R.drawable.ic_nav_delete);
        TextView title = (TextView)getActionBarToolbar().findViewById(R.id.toolbar_center_title);
        title.setText("编辑资料");

        ButterKnife.inject(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在上传");
        progressDialog.setCancelable(false);

        initUI();

    }

    private void initUI(){
        SignUserInfo signUser = PreferencesStorageUtil.readSignUserInfo(this);
        if(!TextUtils.isEmpty(signUser.getAvatar_url())){
            Picasso.with(this).load(signUser.getAvatar_url())
                    .resize(this.getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width), this.getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width))
                    .transform(new RoundedTransformationBuilder()
                            .borderWidthDp(0)
                            .cornerRadius(getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width) / 2)
                            .oval(false)
                            .build())
                    .into(mAvatarImg);
        }
        mProfileName.setText(signUser.getNickname());
        mAvatarImg.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_profile_avatar:
                if(FileUtil.isSDCardMounted()){
                    showOptionsDialog();
                }else{
                    UIUtil.showThost(EditProfileActivity.this, "没有安装SD卡!");
                }
                break;
        }
    }

    // 选择图片来源
    private void showOptionsDialog()
    {
        String[] items = new String[] { "拍照", "选择本地图片" };

        DialogInterface.OnClickListener click = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case 0://拍照
                        Intent intentFromCapture = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                                .fromFile(new File(ImageUtil.getImageCachePath(),
                                        SAVE_AVATORNAME)));
                        startActivityForResult(intentFromCapture,
                                CAMERA_REQUEST_CODE);
                        break;
                    case 1://选择本地图片
                        Intent intentFromGallery = new Intent();
                        intentFromGallery.setType("image/*"); // 设置文件类型
                        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intentFromGallery,
                                IMAGE_REQUEST_CODE);
                        break;
                }
            }
        };

        new AlertDialog.Builder(this).setItems(items,
                click).show();
    }

    /**
     * 回调结果处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != RESULT_CANCELED)
        {
            switch (requestCode)
            {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    startPhotoZoom(Uri.fromFile(new File(ImageUtil.getImageCachePath(), SAVE_AVATORNAME)));
                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null)
                    {
                        getImageToView(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     */
    public void startPhotoZoom(Uri uri)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(Intent data)
    {
        Bundle extras = data.getExtras();
        if (extras != null)
        {
            Bitmap photo = extras.getParcelable("data");
            saveMyBitmap(photo); // 保存裁剪后的图片到SD

//            Picasso.with(this).load(photo)
//                    .resize(this.getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width), this.getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width))
//                    .transform(new RoundedTransformationBuilder()
//                            .borderWidthDp(0)
//                            .cornerRadius(getResources().getDimensionPixelSize(R.dimen.main_tab_profile_avatar_width) / 2)
//                            .oval(false)
//                            .build())
//                    .into(mAvatarImg);
            mAvatarImg.setImageBitmap(photo);
        }
    }

    /**
     * 将头像保存在SDcard
     */
    public void saveMyBitmap(Bitmap bitmap)
    {
        System.out.println("path--->" + ImageUtil.getImageCachePath());
        if(FileUtil.createOrExistsFolder(ImageUtil.getImageCachePath())){
            File f = new File(ImageUtil.getImageCachePath(),
                    SAVE_AVATORNAME);
            try
            {
//                f.createNewFile();
                FileOutputStream fOut = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

                fOut.flush();
                fOut.close();
                mFile = f;
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    private void upLoadProfile(File file){
        progressDialog.show();
        String name = mProfileName.getText().toString().trim();
        if(name.length() < 3){
            UIUtil.showThost(EditProfileActivity.this, "昵称至少为三个字");
            progressDialog.dismiss();
            return ;
        }
        KKMHApp.getRestClient().updateAccount(name, file, new Callback<EditProfileResponse>() {
            @Override
            public void success(EditProfileResponse userInfo, Response response) {
                progressDialog.dismiss();
                if(RetrofitErrorUtil.handleResponse(EditProfileActivity.this, response)){
                    return;
                }
                showSuccessToast();
                PreferencesStorageUtil.writeUserUserInfo(EditProfileActivity.this, userInfo.getAvatar_url(), userInfo.getNickname());
                EditProfileActivity.this.finish();
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                RetrofitErrorUtil.handleError(EditProfileActivity.this, error);
                showFailureToast();
                EditProfileActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.edit_profile_finish:
                upLoadProfile(mFile);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFailureToast() {
        Toast.makeText(this, "上传失败", Toast.LENGTH_SHORT).show();
    }

    private void showSuccessToast() {
        Toast.makeText(this, "上传成功", Toast.LENGTH_SHORT).show();
    }

}
