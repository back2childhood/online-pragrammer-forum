package com.nowcoder.mycommunity.controller.interceptor;

import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.service.DataService;
import com.nowcoder.mycommunity.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;

@Component
public class DataInterceptor implements HandlerInterceptor{

    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // calculate the UV
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);

        // calculate the DAU
        User user = hostHolder.getUser();
        if(user != null){
            dataService.recordDAU(user.getId());
        }

        return true;
    }
}
