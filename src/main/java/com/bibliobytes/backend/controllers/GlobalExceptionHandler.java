package com.bibliobytes.backend.controllers;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception
    ) {
        var errors = new HashMap<String, String>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintValidations(
            ConstraintViolationException exception
    ) {
        var errors = new HashMap<String, String>();

        exception.getConstraintViolations().forEach(violation -> {
            errors.put(violation.getMessage(), violation.getInvalidValue().toString());
        });

        return ResponseEntity.badRequest().body(errors);
    }

//    @ExceptionHandler(HandlerMethodValidationException.class)
//    public ResponseEntity<String> handleHandlerMethodValidationException(
//            HandlerMethodValidationException exception
//    ) {
//        System.out.println("Infact this is what should run.");
//
//        System.out.println(exception.getTarget());
//        System.out.println(exception.getMethod());
//
//        return ResponseEntity.badRequest().body(exception.getMessage());
//    }
}
