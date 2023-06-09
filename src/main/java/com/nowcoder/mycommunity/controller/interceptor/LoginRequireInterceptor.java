package com.nowcoder.mycommunity.controller.interceptor;

import com.nowcoder.mycommunity.annotation.LoginRequired;
import com.nowcoder.mycommunity.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.logging.Handler;

@Component
public class LoginRequireInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    /**
     * check whether the user is logged before doing anything else, so we should use preHandle method
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // get the intercepted method object
            Method method = handlerMethod.getMethod();
            // get the annotation
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            // The current method requires a login, but the user is not logged in
            if(loginRequired != null && hostHolder.getUser() == null){
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
