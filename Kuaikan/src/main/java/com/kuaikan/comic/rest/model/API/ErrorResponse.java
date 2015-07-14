package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.BaseModel;

/**
 * Created by a on 2015/4/1.
 */
public class ErrorResponse extends BaseModel {
    private int code;

    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
