package com.example.crabfood_api.service.email;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendVerificationEmail(String toEmail, String verificationToken) throws MessagingException;

    void sendPasswordResetEmail(String toEmail, String resetToken);

    void sendVendorRegistrationConfirmation(String email, String fullName, String vendorName);

    void sendVendorApprovalNotification(String email, String fullName, String vendorName, boolean approved, String note);

    void sendEmailVerification(String email, String fullName, String verificationToken);

    void sendPasswordReset(String email, String fullName, String resetToken);

    void sendVendorResubmissionNotification(String email, String fullName, String vendorName);

    void sendVendorReregistrationNotification(String email, String fullName, String vendorName);
}
