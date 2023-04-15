package com.scut.turing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//允许跨域访问的路径
                .allowedOrigins("*")//语序跨域访问的源
                .allowedMethods("POST","GET")// 允许请求方法
                .maxAge(16800)//预检间隔事件
                .allowedHeaders("*")//允许头部设置
                .allowCredentials(true);//是否发送cookie
    }
}
