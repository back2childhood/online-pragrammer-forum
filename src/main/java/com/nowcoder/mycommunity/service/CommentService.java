package com.nowcoder.mycommunity.service;

import com.nowcoder.mycommunity.dao.CommentWrapper;
import com.nowcoder.mycommunity.dao.CommentWrapper;
import com.nowcoder.mycommunity.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentWrapper commentWrapper;

    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit){
        return commentWrapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId){
        return commentWrapper.selectCountByEntity(entityType, entityId);
    }
}
