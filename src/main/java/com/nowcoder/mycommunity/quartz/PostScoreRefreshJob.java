package com.nowcoder.mycommunity.quartz;

import com.nowcoder.mycommunity.entity.DiscussPost;
import com.nowcoder.mycommunity.service.DiscussPostService;
import com.nowcoder.mycommunity.service.LikeService;
import com.nowcoder.mycommunity.util.CommunityConstant;
import com.nowcoder.mycommunity.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

//    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private void setRedisTemplate(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    private static final Date epoch;

    static{
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2023-8-29 00:00:00");
        } catch (Exception e) {
            throw new RuntimeException("initiate CoderCommunity epoch fail! ", e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if(operations.size() == 0) {
            logger.info("[No operations] No post needs to be refreshed.");
            return;
        }

        logger.info("[operations start] Refreshing the score of posts..." + operations.size());

        while(operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }

        logger.info("[operations end] Refreshing the score of posts...");
    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);

        if(post == null) {
            logger.error("This post is not exist, id = " + postId);
            return;
        }

        // whether this post is wonderful
//        boolean wonderful = post.getStatus() == 1;
        boolean wonderful = false;
        // the number of comments
        int commentCount = post.getCommentCount();
        // the number of likes
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // calculate the weight of the post
        double w = (wonderful? 75 : 0) + commentCount * 10 + likeCount * 2;
        // PostScore = PostWeight + Time
        // in case the result after log is less than 0, we need to take max between w and 1
        double score = Math.log10(Math.max(w, 1)) + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);

        // refresh the score of the post
        discussPostService.updateScore(postId, score);
        // update the data in the Elasticsearch
        // Elasticsearch.updateScore(postId, score)
    }
}
