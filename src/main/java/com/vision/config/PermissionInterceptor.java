package com.vision.config;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 权限拦截器
 */
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");

        if (role == null) {
            return true;
        }

        // API 权限控制
        if (requestURI.startsWith("/vision/api/")) {
            return checkApiPermission(requestURI, role, response);
        }

        return true;
    }

    /**
     * 检查 API 权限
     */
    private boolean checkApiPermission(String uri, String role, HttpServletResponse response) throws Exception {
        String method = uri;

        // 管理员拥有所有权限
        if ("ADMIN".equals(role)) {
            return true;
        }

        // 验光师权限
        if ("OPTOMETRIST".equals(role)) {
            if (method.contains("/api/patient") ||
                    method.contains("/api/optometry") ||
                    method.contains("/api/analysis") ||
                    method.contains("/api/user/info")) {
                return true;
            }
        }

        // 医生权限
        if ("DOCTOR".equals(role)) {
            if (method.contains("/api/patient") ||
                    method.contains("/api/optometry") ||
                    method.contains("/api/prescription") ||
                    method.contains("/api/analysis") ||
                    method.contains("/api/user/info")) {
                return true;
            }
        }

        // 加工师权限
        if ("PROCESSOR".equals(role)) {
            if (method.contains("/api/processing") ||
                    method.contains("/api/prescription/page") ||
                    method.contains("/api/user/info")) {
                return true;
            }
        }

        // 权限不足
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":403,\"msg\":\"权限不足\"}");
        return false;
    }
}