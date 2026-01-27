package com.thinking.backendmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.upload-url-prefix:/uploads}")
    private String uploadUrlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        String location = uploadPath.toUri().toString();
        String pattern = uploadUrlPrefix.endsWith("/")
                ? uploadUrlPrefix + "**"
                : uploadUrlPrefix + "/**";
        registry.addResourceHandler(pattern).addResourceLocations(location);
    }
}
