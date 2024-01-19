package com.wemap.api.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**")
                .allowedOrigins("http://localhost:3000")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true) //응답에 credentials(쿠키, 인증 헤더 등)을 포함할 수 있는지 여부
                .maxAge(3000); //설정한 시간만큼 pre-flight request를 캐싱
    }
}
