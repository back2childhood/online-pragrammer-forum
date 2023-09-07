package com.nowcoder.mycommunity.dao;

import com.nowcoder.mycommunity.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    // param use for giving the parameter an alias
    // if only have one parameter in this function, and it is used in 'if', you have to use 'param'
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}
