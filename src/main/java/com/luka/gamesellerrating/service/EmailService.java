package com.luka.gamesellerrating.service;

public interface EmailService {

    void sendUserVerificationEmail(String email);
    void sendPasswordResetEmail(String email);
}
