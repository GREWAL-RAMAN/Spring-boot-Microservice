package com.grewal.projectservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExcept {


    @ExceptionHandler(ListNotFound.class)
    public ResponseEntity<String> handleListNotDound(Exception e)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No product for follwoing application");
    }
}
