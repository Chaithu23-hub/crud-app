package com.softsuave.crud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Memory Comment: This is a custom exception.
 *
 * @ResponseStatus(HttpStatus.NOT_FOUND) is the most important part.
 * It tells Spring Boot that whenever this exception is thrown from a
 * controller (or a service called by a controller), it should automatically
 * stop the request and send a 404 NOT FOUND status back to the client.
 * This keeps your service logic clean (you just 'throw' it) and your
 * controller logic even cleaner (you don't need to write any try-catch
 * blocks).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class StudentNotFoundException extends RuntimeException {

    /**
     * A constructor that takes a message.
     * @param message The error message to be sent in the 404 response.
     */
    public StudentNotFoundException(String message) {
        super(message);
    }
}
