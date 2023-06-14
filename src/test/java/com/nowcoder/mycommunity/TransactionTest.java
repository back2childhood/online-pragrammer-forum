package com.nowcoder.mycommunity;

import com.nowcoder.mycommunity.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MyCommunityApplication.class)
public class TransactionTest {

    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSavel1(){
        Object obj = alphaService.save1();
        System.out.println(obj);
    }
}
