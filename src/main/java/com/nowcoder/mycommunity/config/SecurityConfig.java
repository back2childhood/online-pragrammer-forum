package com.nowcoder.mycommunity.config;

import com.nowcoder.mycommunity.util.CommunityConstant;
import com.nowcoder.mycommunity.util.CommunityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig implements CommunityConstant{

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/resource/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) -> {
            try {
                authz
                        .requestMatchers(
                                "/user/setting",
                                "user/upload",
                                "/discuss/add",
                                "/comment/add/**",
                                "/letter/**",
                                "/notice/**",
                                "like",
                                "/follow",
                                "/unfollow"
                        )
                        .hasAnyAuthority(
                                AUTHORITY_ADMIN,
                                AUTHORITY_MODERATOR,
                                AUTHORITY_USER
                        )
                        .requestMatchers(
                                "/discuss/top",
                                "/discuss/wonderful"
                        )
                        .hasAnyAuthority(
                                AUTHORITY_MODERATOR
                        )
                        .requestMatchers(
                                "/discuss/delete",
                                "/data/**",
                                "/actuator/**"
                        )
                        .hasAnyAuthority(
                                AUTHORITY_ADMIN
                        )
                        .anyRequest().permitAll()
                        .and().csrf().disable();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Handling when permissions are insufficient
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    // not logged in
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        // Determine whether the request is synchronous or asynchronous
                        String xRequestedWith = request.getHeader("x-requested-with");
                        // asynchronous
                        if("XMLHttpRequest".equals(xRequestedWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "not logged in :)"));
                        }else{
                            // synchronous request
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    // permissions are insufficient
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        // Determine whether the request is synchronous or asynchronous
                        String xRequestedWith = request.getHeader("x-requested-with");
                        // asynchronous
                        if("XMLHttpRequest".equals(xRequestedWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "you don't have sufficient permission :)"));
                        }else{
                            // synchronous request
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        // Security Automatically blocks exit requests by default
        // we should override its automatic process to do ourselves code
        http.logout().logoutUrl("/securitylogout");
        return http.build();
    }

}
