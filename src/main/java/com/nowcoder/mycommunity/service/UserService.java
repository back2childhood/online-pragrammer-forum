package com.nowcoder.mycommunity.service;

import com.nowcoder.mycommunity.dao.UserMapper;
import com.nowcoder.mycommunity.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById(int userId){
        return userMapper.selectById(userId);
    }
}
