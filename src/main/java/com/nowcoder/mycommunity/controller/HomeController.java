package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.entity.DiscussPost;
import com.nowcoder.mycommunity.entity.Page;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.service.DiscussPostService;
import com.nowcoder.mycommunity.service.LikeService;
import com.nowcoder.mycommunity.service.UserService;
import com.nowcoder.mycommunity.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {
        // before the method is called, SpringMVC automatically instantiates the Model and Page, and
        // injects the Page into Model
        // so, it is possible to access the data in the Page object directly from thymeleaf
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode=" + orderMode);

        List<DiscussPost> list = discussPostService.discussPosts(0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost discussPost : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", discussPost);

                User user = userService.findUserById(discussPost.getUserId());
                map.put("user", user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("orderMode", orderMode);
        return "/index";
    }

    @GetMapping(path = "/error")
    public String getErrorPage() {
        return "/error/500";
    }
}
