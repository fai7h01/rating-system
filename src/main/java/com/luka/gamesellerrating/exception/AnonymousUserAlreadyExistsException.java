package com.luka.gamesellerrating.exception;

public class AnonymousUserAlreadyExistsException extends RuntimeException{

    public AnonymousUserAlreadyExistsException(String message) {
        super(message);
    }
}
