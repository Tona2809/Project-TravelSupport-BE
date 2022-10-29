package com.hcmute.hotel.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET","POST","DELETE","PUT","OPTIONS","PATCH")
                        .allowedHeaders("*")
                        .allowedOrigins("http://localhost:3000/","http://localhost:8080/","https://tiki-web.vercel.app/"
                        ,"https://gorgeous-pastelito-2fb64c.netlify.app/","https://2a5d-2001-ee0-4f4e-8cc0-65c7-8c8f-7b91-3ddb.ngrok.io/")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
