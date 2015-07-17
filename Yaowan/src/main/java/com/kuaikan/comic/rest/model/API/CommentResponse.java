package com.kuaikan.comic.rest.model.API;

import com.kuaikan.comic.rest.model.BaseModel;
import com.kuaikan.comic.rest.model.Comment;

import java.util.List;

/**
 * Created by skyfishjy on 12/23/14.
 */
public class CommentResponse extends BaseModel {
    private List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }
}
