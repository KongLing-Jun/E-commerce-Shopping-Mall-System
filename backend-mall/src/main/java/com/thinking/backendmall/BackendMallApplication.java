package com.thinking.backendmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.thinking.backendmall.repository")
public class BackendMallApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendMallApplication.class, args);
    }

}
