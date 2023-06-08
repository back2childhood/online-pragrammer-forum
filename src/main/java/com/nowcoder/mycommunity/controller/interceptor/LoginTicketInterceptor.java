package com.nowcoder.mycommunity.controller.interceptor;

import com.nowcoder.mycommunity.entity.LoginTicket;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.service.UserService;
import com.nowcoder.mycommunity.util.CookieUtil;
import com.nowcoder.mycommunity.util.HostHolder;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // get certificate from cookie
        String ticket = CookieUtil.getValue(request, "ticket");
        if(ticket != null){
            // query the certificate
            LoginTicket loginTicket = userService.findLoginTicket(ticket);

            // check if the certificate valid
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                // query users base on the certificate
                User user = userService.findUserById(loginTicket.getUserId());

                // hold this user during this request
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
