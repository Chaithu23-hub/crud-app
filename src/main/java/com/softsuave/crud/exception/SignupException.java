package com.softsuave.crud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class SignupException extends RuntimeException {
    public SignupException(String message) {
        super(message);
    }
}
