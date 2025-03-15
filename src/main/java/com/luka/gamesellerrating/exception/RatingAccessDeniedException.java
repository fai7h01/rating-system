package com.luka.gamesellerrating.exception;

public class RatingAccessDeniedException extends RuntimeException{

    public RatingAccessDeniedException(String message) {
        super(message);
    }
}
