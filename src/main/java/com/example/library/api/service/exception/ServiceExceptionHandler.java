package com.example.library.api.service.exception;

import com.example.library.api.resource.exception.Error;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ServiceExceptionHandler {

    @ExceptionHandler(IsbnDuplicatedException.class)
    public ResponseEntity<Error> isbnDuplicated(IsbnDuplicatedException exception) {
        System.out.println(exception.getMessage());
        return ResponseEntity.badRequest().body(new Error(exception.getMessage()));
    }
}
