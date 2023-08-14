package com.nowcoder.mycommunity.service;

import com.nowcoder.mycommunity.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class FollowService {

    @Autowired
    private RedisTemplate redisTemplate;

//    public  void follow(int userId, int entityType, int entityId){
//        redisTemplate.execute(new SessionCallback() {
//            @Override
//            public Object execute(RedisOperations operations) throws DataAccessException {
//                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
//                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
//                // start transaction
//                operations.multi();
//
//                operations.
//
//                return null;
//            }
//        })
//    }
}
