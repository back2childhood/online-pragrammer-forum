package com.nowcoder.mycommunity;

import com.nowcoder.mycommunity.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MyCommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testMail(){
        mailClient.sendMail("qian_jili@163.com", "TEST", "Welcome");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username", "sunday");

        String content = templateEngine.process("/mail/demo", context);

        mailClient.sendMail("qian_jili@163.com", "TEST", content);
    }
}
