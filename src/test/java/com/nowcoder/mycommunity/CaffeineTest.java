package com.nowcoder.mycommunity;

import com.nowcoder.mycommunity.entity.DiscussPost;
import com.nowcoder.mycommunity.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MyCommunityApplication.class)
public class CaffeineTest {

    @Autowired
    private DiscussPostService postService;

    @Test
    public void initDataForTest(){
        for(int i = 0; i < 30000; ++i){
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("Test");
            post.setContent("Test content");
            post.setCreateTime(new Date());
            post.setScore(Math.random() * 2000);
            postService.addDiscussPost(post);
        }
    }

    @Test
    public void testCache(){
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(postService.findDiscussPosts(0, 0, 10, 0));
    }
}
