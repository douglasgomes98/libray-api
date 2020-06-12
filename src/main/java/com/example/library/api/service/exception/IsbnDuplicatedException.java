package com.example.library.api.service.exception;

public class IsbnDuplicatedException extends RuntimeException {

    public IsbnDuplicatedException(String string) {
        super(string);
    }

}
