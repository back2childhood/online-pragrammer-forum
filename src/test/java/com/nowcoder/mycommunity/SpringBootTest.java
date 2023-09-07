package com.nowcoder.mycommunity;

import com.nowcoder.mycommunity.entity.DiscussPost;
import com.nowcoder.mycommunity.service.DiscussPostService;
import org.junit.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@org.springframework.boot.test.context.SpringBootTest
@ContextConfiguration(classes = MyCommunityApplication.class)
public class SpringBootTest {

    @Autowired
    private DiscussPostService discussPostService;

    private DiscussPost data;

    @BeforeClass
    public static void beforeAll() {
        System.out.println("beforeClass");
    }

    @AfterClass
    public static void afterAll() {
        System.out.println("afterClass");
    }

    @Before
    public void before() {
        System.out.println("before");

        // initialize
        data = new DiscussPost();
        data.setUserId(111);
        data.setId(287);
        data.setTitle("Test title");
        data.setContent("Test content");
        data.setCreateTime(new Date());
//        discussPostService.addDiscussPost(data);
        System.out.println(data.getId());
    }

    @After
    public void after() {
        System.out.println("after");

        // delete
        discussPostService.updateStatus(data.getId(), 2);
    }

//    @Test
//    public void test1(){
//        System.out.println("test1");
//    }
//
//    @Test
//    public void test2(){
//        System.out.println("test2");
//    }

    @Test
    public void testFindById(){
        DiscussPost discussPost = discussPostService.findDiscussPostById(data.getId());
        Assert.assertNotNull(discussPost);
        Assert.assertEquals(discussPost.getTitle(), data.getTitle());
        Assert.assertEquals(discussPost.getContent(), data.getContent());
    }

    @Test
    public void testUpdateScore() {
        int rows = discussPostService.updateScore(data.getId(), 2000.00);
        Assert.assertEquals(1, rows);

        DiscussPost post = discussPostService.findDiscussPostById(data.getId());
        Assert.assertEquals(2000.00, post.getScore(), 2);
    }
}
