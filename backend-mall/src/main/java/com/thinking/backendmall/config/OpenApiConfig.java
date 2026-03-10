package com.thinking.backendmall.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mallOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("3C Mall API")
                        .version("v1")
                        .description("3C mall platform API documentation"));
    }
}
