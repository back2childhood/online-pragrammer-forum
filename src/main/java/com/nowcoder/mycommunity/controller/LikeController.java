package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.annotation.LoginRequired;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.service.LikeService;
import com.nowcoder.mycommunity.util.CommunityUtil;
import com.nowcoder.mycommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping(path = "/like")
    @ResponseBody
    @LoginRequired
    public String like(int entityType, int entityId, int entityUserId){
        User user = hostHolder.getUser();

        // like
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // number
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // status
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        // return the result
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return CommunityUtil.getJSONString(0, null, map);
    }
}
