package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.entity.DiscussPost;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.service.DiscussPostService;
import com.nowcoder.mycommunity.service.UserService;
import com.nowcoder.mycommunity.util.CommunityUtil;
import com.nowcoder.mycommunity.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        if(StringUtils.isBlank(title)){
            return CommunityUtil.getJSONString(403, "title can't be empty");
        }
        if(StringUtils.isBlank(content)){
            return CommunityUtil.getJSONString(403, "content can't be empty");
        }

        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403, "you should log in first");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());

        discussPostService.addDiscussPost(post);

        return CommunityUtil.getJSONString(0, "Article published successfully");
    }

    @GetMapping(path = "/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model){
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);

        // when we browse other's article, we hope to show this author's information.
        // we have two ways to achieve this aim.
        // 1. we can use correlated select, mybatis support this operation
        // this solution is faster, but higher coupling
        // 2. we just use userservice in method where we need it
        // this solution is slower, but we can use redis to speed up.
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        return "/site/discuss-detail";
    }
}
