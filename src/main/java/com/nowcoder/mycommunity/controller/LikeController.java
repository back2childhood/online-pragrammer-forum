package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.annotation.LoginRequired;
import com.nowcoder.mycommunity.entity.Event;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.event.EventProducer;
import com.nowcoder.mycommunity.service.LikeService;
import com.nowcoder.mycommunity.util.CommunityConstant;
import com.nowcoder.mycommunity.util.CommunityUtil;
import com.nowcoder.mycommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer producer;

    @PostMapping(path = "/like")
    @ResponseBody
    @LoginRequired
    public String like(int entityType, int entityId, int entityUserId, int postId) {
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

//        // trigger the like event
//        if(likeStatus == 1){
//            Event event = new Event()
//                    .setTopic(TOPIC_LIKE)
//                    .setUserId(hostHolder.getUser().getId())
//                    .setEntityType(entityType)
//                    .setEntityId(entityId)
//                    .setEntityUserId(entityUserId)
//                    .setData("postId", postId);
//            producer.fireEvent(event);
//        }

        return CommunityUtil.getJSONString(0, null, map);
    }
}
