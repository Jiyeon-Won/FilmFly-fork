package com.sparta.filmfly.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // board 작성할 때 씀
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/temp/**")
            .addResourceLocations("file:src/main/resources/static/temp/");
    }

    // CORS 해결
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:8080",
                "http://localhost:63342",
                "https://localhost:8080",
                "https://localhost:63342",
                "https://www.filmfly.shop",
                "https://api.filmfly.shop"
            )
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
            .allowedHeaders("Content-Type", "Authorization", "Accept")
            .allowedHeaders("Set-Cookie")
            .allowCredentials(true)
            .maxAge(3600);
    }
}