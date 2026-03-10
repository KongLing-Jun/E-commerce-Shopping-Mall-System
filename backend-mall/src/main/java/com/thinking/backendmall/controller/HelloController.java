package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public Result<String> hello() {
        return Result.success("hello");
    }
}
