package com.nowcoder.mycommunity.service;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.nowcoder.mycommunity.dao.LoginTicketMapper;
import com.nowcoder.mycommunity.dao.UserMapper;
import com.nowcoder.mycommunity.entity.LoginTicket;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.util.CommunityConstant;
import com.nowcoder.mycommunity.util.CommunityUtil;
import com.nowcoder.mycommunity.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.channels.Pipe;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${mycommunity.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public User findUserById(int userId){
        return userMapper.selectById(userId);
    }

    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();

        if(user == null){
            throw new IllegalArgumentException("The parameter cannot be null");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "user name cannot be null!");
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "password cannot be null!");
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "email cannot be null!");
        }

        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg", "this account is exists");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg", "this account is exists");
            return map;
        }

        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        Context context = new Context();
        context.setVariable("email", user.getEmail());

        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        try {
            mailClient.sendMail(user.getEmail(), "activate account", content);
        }catch (Exception e){
            map.put("emailMsg", "email address is invalid!");
        }
        return map;
    }

    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTAVATION_REPEATE;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTAVATION_FAILED;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        // handle null value
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "the account cannot be empty");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "the password cannot be empty");
            return map;
        }

        // verify the account
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "this account don't exist");
            return map;
        }

        // verify the status
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "this account isn't activated");
            return map;
        }

        // verify the password
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "wrong password");
            return map;
        }

        // Generate login credentials
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpire(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * don't need return a value, it will success if there is no error
     * @param ticket
     */
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId, String headUrl){
        return userMapper.updateHeaders(userId, headUrl);
    }
}
