package com.example.datingWebsite.exception;

public class UserAlreadyExists extends RuntimeException {
    public UserAlreadyExists(String message) {
        super(message);
    }
}
