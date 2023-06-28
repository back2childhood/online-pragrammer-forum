package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.entity.Comment;
import com.nowcoder.mycommunity.entity.DiscussPost;
import com.nowcoder.mycommunity.entity.Page;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.service.CommentService;
import com.nowcoder.mycommunity.service.DiscussPostService;
import com.nowcoder.mycommunity.service.LikeService;
import com.nowcoder.mycommunity.service.UserService;
import com.nowcoder.mycommunity.util.CommunityConstant;
import com.nowcoder.mycommunity.util.CommunityUtil;
import com.nowcoder.mycommunity.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

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
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
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

        // like
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        // like status

        int likeStatus = hostHolder.getUser() == null? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus", likeStatus);

        // comment paging information
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // comments: comments of the post
        // replies: comments of the comment

        // comments list
        List<Comment> commentslist = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(),
                page.getOffset(), page.getLimit());

        // comment view object list
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(commentslist != null){
            for(Comment comment : commentslist){
                // comments vo
                Map<String, Object> commentVo = new HashMap<>();
                // comment
                commentVo.put("comment", comment);
                // author
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // like count
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                // like status
                likeStatus = hostHolder.getUser() == null? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);

                // replies list
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(),
                        0, Integer.MAX_VALUE);
                // relies view object list
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply : replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        // like count
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        // like status
                        likeStatus = hostHolder.getUser() == null? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replies", replyVoList);

                // number of replies
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
