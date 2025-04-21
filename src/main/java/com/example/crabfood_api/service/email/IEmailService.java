package com.example.crabfood_api.service.email;

import jakarta.mail.MessagingException;

public interface IEmailService {
    public void sendVerificationEmail(String toEmail, String verificationToken) throws MessagingException;
    public void sendPasswordResetEmail(String toEmail, String resetToken);
}
