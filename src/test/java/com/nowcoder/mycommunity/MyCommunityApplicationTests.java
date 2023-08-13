package com.nowcoder.mycommunity;

import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.jdbc.CallableStatement;
import com.nowcoder.mycommunity.dao.AlphaDao;
import com.nowcoder.mycommunity.service.AlphaService;
import net.minidev.json.parser.JSONParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = MyCommunityApplication.class)
class MyCommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testApplicationContext() {
        System.out.println(applicationContext);

        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        System.out.println(alphaDao.select());
    }

    @Test
    public void testPostConstruct() {
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        alphaService.init();
    }

    @Test
    public void testBeanConfig() {
        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
    }

    @Test
    public void contextLoads() {
        float f = 3;
//        Statement statement = new CallableStatement();
    }

    @Test
    public void testMap(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("1", "value1");
        map.put("2", "value2");
        map.put("3", "value3");

        Map<String, String> map1 = new HashMap<>();
        map1.putAll(map);
        System.out.println(JSONObject.toJSONString(map1));
        System.out.println(JSONObject.parse("111"));
    }
}
