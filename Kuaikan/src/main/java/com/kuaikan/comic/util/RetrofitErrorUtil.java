package com.kuaikan.comic.util;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.kuaikan.comic.R;
import com.kuaikan.comic.rest.model.API.ErrorResponse;
import com.kuaikan.comic.ui.LoginActivity;

import java.io.IOException;
import java.io.InputStream;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by liuchao1 on 15/6/10.
 */
public class RetrofitErrorUtil {

    private static Gson mGson = new Gson();

    public static boolean handleResponse(Context context, Response response){
        try {
            ErrorResponse errorResponse = mGson.fromJson(inputStream2String(response.getBody().in()),ErrorResponse.class);
            return handleErrorCode(context, errorResponse.getCode());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){

        }
        return true;
    }

    public static void handleError(Context context, RetrofitError error ){
        if(context == null){
            return;
        }

        Response response = error.getResponse();
        if (response != null) {

            handleErrorCode(context, response.getStatus());
        }else{
            UIUtil.showThost(context, R.string.error_network);
        }



    }

    public static boolean handleErrorCode(Context context, int status){
        boolean hasError = false;
        switch (status) {
            case IERROR_TYPE.ERROR_NONE:
                hasError = false;
                break;
            case IERROR_TYPE.ERROR_NORMAL:
                UIUtil.showThost(context, R.string.error_code_400);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_EXPIRE:
                UserUtil.logout(context);
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                UIUtil.showThost(context, R.string.error_code_401);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_REGISTER_ALREADY:
                UIUtil.showThost(context, R.string.error_code_409);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_NETWORK_ERROR:
                UIUtil.showThost(context, R.string.error_code_600000);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_TEL_UNREGISTER:
                UIUtil.showThost(context, R.string.error_code_600001);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_TEL_INVALID:
                UIUtil.showThost(context, R.string.error_code_600002);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_TEL_EXIST:
                UIUtil.showThost(context, R.string.error_code_600003);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_ACCOUNT_PWD_ERROR:
                UIUtil.showThost(context, R.string.error_code_600004);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_VERIFY_CODE_INVALID:
                UIUtil.showThost(context, R.string.error_code_600005);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_PORTRAIT_UPLOAD_ERROR:
                UIUtil.showThost(context, R.string.error_code_600006);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_ACCOUNT_EXIST:
                UIUtil.showThost(context, R.string.error_code_600007);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_ACCOUNT_INVALID:
                UIUtil.showThost(context, R.string.error_code_600008);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_ATTENTION_FAILURE:
                UIUtil.showThost(context, R.string.error_code_600009);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_FAV_FAILURE:
                UIUtil.showThost(context, R.string.error_code_600010);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_COMMENT_FAILURE:
                UIUtil.showThost(context, R.string.error_code_600011);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_UNFOLLOW_FAILURE:
                UIUtil.showThost(context, R.string.error_code_600012);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_UNFAV_FAILURE:
                UIUtil.showThost(context, R.string.error_code_600013);
                hasError = true;
                break;
            case IERROR_TYPE.ERROR_VERIFY_CODE_TOO_MUCH:
                UIUtil.showThost(context, R.string.error_code_600014);
                hasError = true;
                break;
            default:
                UIUtil.showThost(context, R.string.error_code_600000);
                hasError = true;
                break;

        }
        return hasError;

    }

    public static void handleError(Context context, RetrofitError error, IErrorCallback errorCallback ){

        Response response = error.getResponse();
        handleError(context, error);
        if(response != null){
            errorCallback.onError(response.getStatus());
        }

    }

    public static String inputStream2String (InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for(int n; (n = in.read(b)) != -1;){
            out.append(new String(b,0,n));
        }
        return out.toString();
    }


    public interface IErrorCallback{
        public void onError(int error_type);
    }


    public interface IERROR_TYPE{

        public static final int ERROR_NONE = 200;
        public static final int ERROR_NORMAL = 400;
        /* 超时*/
        public static final int ERROR_EXPIRE = 401;
        public static final int ERROR_REGISTER_ALREADY = 409;
        public static final int ERROR_NETWORK_ERROR = 600000;
        public static final int ERROR_TEL_UNREGISTER = 600001;
        public static final int ERROR_TEL_INVALID = 600002;
        public static final int ERROR_TEL_EXIST = 600003;
        public static final int ERROR_ACCOUNT_PWD_ERROR = 600004;
        public static final int ERROR_VERIFY_CODE_INVALID = 600005;
        public static final int ERROR_PORTRAIT_UPLOAD_ERROR = 600006;
        public static final int ERROR_ACCOUNT_EXIST = 600007;
        public static final int ERROR_ACCOUNT_INVALID = 600008;
        public static final int ERROR_ATTENTION_FAILURE = 600009;
        public static final int ERROR_FAV_FAILURE = 600010;
        public static final int ERROR_COMMENT_FAILURE = 600011;
        public static final int ERROR_UNFOLLOW_FAILURE = 600012;
        public static final int ERROR_UNFAV_FAILURE = 600013;
        public static final int ERROR_VERIFY_CODE_TOO_MUCH = 600014;

    }

}
