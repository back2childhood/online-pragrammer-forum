package com.nowcoder.mycommunity;

import com.nowcoder.mycommunity.dao.DiscussPostMapper;
import com.nowcoder.mycommunity.dao.UserMapper;
import com.nowcoder.mycommunity.entity.DiscussPost;
import com.nowcoder.mycommunity.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MyCommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);
    }

    @Test
    public void testUpdatatUser(){
        userMapper.updateStatus(101, 2);
        User user = userMapper.selectById(101);
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("111222333444");
        user.setCreateTime(new Date());
        user.setHeaderUrl("http://images.nowcoder.com/head/1t.png");
        user.setType(0);
        user.setStatus(0);
        user.setPassword("1122");
        user.setSalt("0");
        user.setActivationCode("00000");
        user.setId(11111);
        user.setEmail("111111");
        userMapper.insertUser(user);
        user = userMapper.selectById(11111);
        System.out.println(user);
    }


    @Test
    public void testSelectDiscuss(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for(DiscussPost discussPost : list) {
            System.out.println(discussPost);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }
}
