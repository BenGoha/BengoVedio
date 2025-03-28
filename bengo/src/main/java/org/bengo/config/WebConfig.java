package org.bengo.config;

import org.bengo.config.AdminInterceptor;
import org.bengo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private UserService userService;

    @Autowired
    private AdminInterceptor adminInterceptor; // 使用 Spring 管理的单例

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**", "/authorize/**", "/bengo/**", "/api/**", "/video/**","/customer/**")
                .excludePathPatterns("/bengo/login/**", "/bengo/index/**", "/bengo/cdn/**", "/bengo/file/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/register","/login", "/register", "/api/urlConfig.js")
                .excludePathPatterns("/api/urlConfig.js","/customer/login", "/customer/register");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:5378", "http://localhost:5378")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}