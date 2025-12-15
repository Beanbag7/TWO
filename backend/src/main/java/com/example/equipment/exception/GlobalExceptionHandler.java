package com.example.equipment.exception;

import com.example.equipment.common.Result;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        e.printStackTrace();
        System.out.println("Exception: " + e.getMessage());
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public Result<String> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException e) {

        // 1. 获取所有字段错误的列表
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        // 2. 使用 Java 8 Stream 将它们拼接起来
        // 例如拼接成： "Username cannot be empty; Password cannot be empty"
        for (FieldError fieldError : fieldErrors)
            System.out.println(fieldError.getDefaultMessage());
        String msg = fieldErrors.stream()
                .map(FieldError::getDefaultMessage) // 提取每个错误的提示语
                .collect(Collectors.joining("; ")); // 用分号隔开
//        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.error(400, msg);
    }
    
    // Add more specific exceptions if needed
}
