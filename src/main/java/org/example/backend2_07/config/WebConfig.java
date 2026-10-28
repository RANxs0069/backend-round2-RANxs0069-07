package org.example.backend2_07.config;

import org.example.backend2_07.security.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)     //调取这个类进行预处理
                .addPathPatterns("/user/**")        // 拦截所有 /user 开头的接口
                .excludePathPatterns("/user/login", "/user/register");   // 但登录接口放行
    }
}
