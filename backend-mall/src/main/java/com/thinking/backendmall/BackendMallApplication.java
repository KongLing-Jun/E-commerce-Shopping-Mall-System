package com.thinking.backendmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.thinking.backendmall.repository")
public class BackendMallApplication {
    // 功能：处理main
    public static void main(String[] args) {
        SpringApplication.run(BackendMallApplication.class, args);
    }

}
