package com.example.auth_server.dto;

public record EmailTemplate(
        String subject,
        String headerColor,
        String title,
        String message,
        String codeLabel,
        String infoWarning,
        String ignoreWarning
) {}