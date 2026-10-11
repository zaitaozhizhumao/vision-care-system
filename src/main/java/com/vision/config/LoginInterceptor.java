package com.vision.config;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();

        // 放行静态资源和登录相关请求
        if (requestURI.contains("/static/") ||
                requestURI.contains("/login") ||
                requestURI.contains("/api/user/login") ||
                requestURI.contains("/api/user/logout") ||
                requestURI.equals(contextPath + "/") ||
                requestURI.endsWith(".css") ||
                requestURI.endsWith(".js") ||
                requestURI.endsWith(".png") ||
                requestURI.endsWith(".jpg") ||
                requestURI.endsWith(".ico") ||
                requestURI.endsWith(".woff") ||
                requestURI.endsWith(".woff2") ||
                requestURI.endsWith(".ttf")) {
            return true;
        }

        // 检查登录状态
        HttpSession session = request.getSession();
        Object userId = session.getAttribute("userId");

        if (userId == null) {
            // 未登录，重定向到登录页
            response.sendRedirect(contextPath + "/login");
            return false;
        }

        return true;
    }
}