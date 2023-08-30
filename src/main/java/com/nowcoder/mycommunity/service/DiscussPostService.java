package com.nowcoder.mycommunity.service;

import com.nowcoder.mycommunity.dao.DiscussPostMapper;
import com.nowcoder.mycommunity.entity.DiscussPost;
import com.nowcoder.mycommunity.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> discussPosts(int userId, int offset, int limit, int orderMode) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("paramater can't be null");
        }

        // filter sensitive words
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        /*
        工作中，可能碰到一下特殊字符转义的问题，例如< > ?等，有的时候保存到数据库时，数据库会自动将特殊字符进行转义，
        存到数据库的就不是你输入的那些特殊字符，而是转义以后的，例如“<”,保存到数据库时会变成“&lt;”,
        但是你想保存到数据库的就是“<”,因此你可以用HtmlUtils.htmlEscape()进行转义一下，再保存到数据库就ok了。
        或者数据库存的是特殊字符转义后的结果，你想要转义前的结果，那么也可以用HtmlUtils.htmlUnescape()进行转义，就能得到你想要的特殊字符了
         */
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }
}
