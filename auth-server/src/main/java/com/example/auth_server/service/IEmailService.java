package com.example.auth_server.service;

import jakarta.mail.MessagingException;

public interface IEmailService {
    public void sendEmail(String to, String subject, String text)
            throws MessagingException;
}
