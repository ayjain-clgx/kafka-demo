package com.self.emailnotification.exception;

public class NonRetryableException extends RuntimeException {

    public NonRetryableException(String message) {
        super(message);
    }
}
