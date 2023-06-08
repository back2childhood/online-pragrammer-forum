package com.nowcoder.mycommunity.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AlphaInterceptor implements HandlerInterceptor {

    // logger
    private static final Logger logger = LoggerFactory.getLogger(AlphaInterceptor.class);

    // execute before Controller
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("preHandle: " + handler.toString());
        return true;
    }

    // execute after Controller, but before TemplateEngine
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        logger.debug("postHandle: " + handler.toString());
    }

    // execute after TemplateEngine
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        logger.debug("afterHandle: " + handler.toString());
    }
}
