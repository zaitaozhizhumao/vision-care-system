package com.vision.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private com.vision.mapper.SysLogMapper sysLogMapper;

    @Around("execution(* com.vision.controller.*.*(..)) && !execution(* com.vision.controller.PageController.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                HttpSession session = request.getSession(false);

                if (session != null) {
                    Map<String, Object> log = new HashMap<>();
                    log.put("userId", session.getAttribute("userId"));
                    log.put("username", session.getAttribute("username"));
                    log.put("operation", joinPoint.getSignature().getName());
                    log.put("method", request.getMethod() + " " + request.getRequestURI());
                    log.put("params", request.getQueryString());
                    log.put("ip", getClientIp(request));

                    sysLogMapper.insert(log);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}