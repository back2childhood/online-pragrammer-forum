package com.nowcoder.mycommunity.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.mycommunity.dao.DiscussPostMapper;
import com.nowcoder.mycommunity.entity.DiscussPost;
import com.nowcoder.mycommunity.util.SensitiveFilter;
import jakarta.annotation.PostConstruct;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private final static Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine's core API: Cache, LoadingCache, AsyncLoadingCache

    // posts list cache
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // posts total number cache
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init(){
        // initialize the post list cache
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(String key) throws Exception {
                        if(key == null || key.length() == 0){
                            throw new IllegalArgumentException("parameter error: key must not be null");
                        }
                        String[] params = key.split(":");
                        if(params == null || params.length != 2) {
                            throw new IllegalArgumentException("parameter error");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        // in there, we can add second level cache, such as Redis
                        // if we can't find data in the Redis, then query it in MySQL

                        logger.debug("load post list from DB...");
                        return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                    }
                });
        // initialize the post total number cache
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(Integer key) throws Exception {
                        logger.debug("load post list from DB...");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode) {
        if(userId == 0 && orderMode == 1){
            return postListCache.get(offset + ":" + limit);
        }
        logger.debug("load post list from DB...");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    public int findDiscussPostRows(int userId) {
        if (userId == 0) {
            return postRowsCache.get(userId);
        }
        logger.debug("load post rows from DB...");
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
