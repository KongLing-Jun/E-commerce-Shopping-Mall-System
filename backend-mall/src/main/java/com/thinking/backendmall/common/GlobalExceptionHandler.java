package com.thinking.backendmall.common;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.thinking.backendmall.controller")
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(BusinessException.class)
    // 功能：处理businessexception
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("Business exception: code={}, message={}", e.getCode(), e.getMessage());
        // Return business error code/message directly to frontend.
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    // 功能：处理validationexception
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Validation exception: {}", e.getMessage());
        // Return first field validation message.
        String message = e.getBindingResult().getAllErrors().isEmpty()
                ? "Validation failed"
                : e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Result.error(ErrorCode.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    // 功能：处理constraintviolation
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        log.warn("Constraint violation: {}", e.getMessage());
        // Handle query/path parameter validation failures.
        String message = e.getConstraintViolations().isEmpty()
                ? "Validation failed"
                : e.getConstraintViolations().iterator().next().getMessage();
        return Result.error(ErrorCode.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    // 功能：处理exception
    public Result<Void> handleException(Exception e) {
        log.error("Unhandled server exception", e);
        // Fallback for unhandled server-side exceptions.
        return Result.error(ErrorCode.SERVER_ERROR);
    }
}
