package com.nowcoder.mycommunity.util;

import com.nowcoder.mycommunity.entity.User;
import org.springframework.stereotype.Component;

/**
 * hold user object by a thread isolated method
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
