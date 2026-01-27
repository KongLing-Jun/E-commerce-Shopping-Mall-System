package com.thinking.backendmall.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Mall API",
                version = "v1",
                description = "3C mall backend API documentation"
        )
)
public class OpenApiConfig {
}
