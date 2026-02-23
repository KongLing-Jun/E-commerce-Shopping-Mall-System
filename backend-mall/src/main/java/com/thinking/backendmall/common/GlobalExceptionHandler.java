package com.thinking.backendmall.common;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.thinking.backendmall.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        // Return business error code/message directly to frontend.
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        // Return first field validation message.
        String message = e.getBindingResult().getAllErrors().isEmpty()
                ? "Validation failed"
                : e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Result.error(ErrorCode.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        // Handle query/path parameter validation failures.
        String message = e.getConstraintViolations().isEmpty()
                ? "Validation failed"
                : e.getConstraintViolations().iterator().next().getMessage();
        return Result.error(ErrorCode.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        // Fallback for unhandled server-side exceptions.
        return Result.error(ErrorCode.SERVER_ERROR);
    }
}
