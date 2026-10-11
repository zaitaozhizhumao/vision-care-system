package com.vision.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 登录拦截器
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/api/user/login",
                        "/static/**",
                        "/error");

        // 权限拦截器
        registry.addInterceptor(new PermissionInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/logout",
                        "/api/user/info");
    }

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/dashboard").setViewName("dashboard");
        registry.addViewController("/patient").setViewName("patient/list");
        registry.addViewController("/patient/add").setViewName("patient/add");
        registry.addViewController("/optometry").setViewName("optometry/list");
        registry.addViewController("/optometry/add").setViewName("optometry/add");
        registry.addViewController("/prescription").setViewName("prescription/list");
        registry.addViewController("/processing").setViewName("processing/list");
        registry.addViewController("/followup").setViewName("followup/list");
        registry.addViewController("/analysis").setViewName("analysis/index");
    }
}