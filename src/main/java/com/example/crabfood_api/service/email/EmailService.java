package com.example.crabfood_api.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService implements IEmailService{

    private final JavaMailSender mailSender;
    private final String domainUrl;
    private final TemplateEngine templateEngine;
    private final String fromEmail;
    private final String fromName;

    public EmailService(JavaMailSender mailSender,
                        @Value("${application.domain-url}") String domainUrl, TemplateEngine templateEngine,
                        @Value("${app.email.from:noreply@crabfood.com}") String fromEmail,
                        @Value("${app.email.from-name:CrabFood Team}") String fromName) {
        this.mailSender = mailSender;
        this.domainUrl = domainUrl;
        this.templateEngine = templateEngine;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    @Async
    @Override
    public void sendVerificationEmail(String toEmail, String verificationToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String verificationUrl = domainUrl + "api/auth/verify-email?token=" + verificationToken;

        Context context = new Context();
        context.setVariable("verificationUrl",verificationUrl);
        context.setVariable("email",toEmail);
        String htmlContent = templateEngine.process("EmailTemplate",context);

        helper.setTo(toEmail);
        helper.setSubject("Xác thực email đăng ký CrabFood");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String subject = "Đặt lại mật khẩu CrabFood";
        String resetUrl = domainUrl + "api/v1/auth/reset-password?token=" + resetToken;
        String message = "Vui lòng click vào link sau để đặt lại mật khẩu: " + resetUrl
                + "\n\nLink có hiệu lực trong 24 giờ.";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }

    @Override
    public void sendVendorRegistrationConfirmation(String email, String fullName, String vendorName) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("vendorName", vendorName);
            context.setVariable("registrationDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("baseUrl", domainUrl);

            String htmlContent = templateEngine.process("EmailRegisterVendor", context);

            sendHtmlEmail(
                    email,
                    "Xác nhận đăng ký thành công - " + vendorName,
                    htmlContent
            );

        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email xác nhận", e);
        }
    }

    @Override
    public void sendVendorApprovalNotification(String email, String fullName, String vendorName, boolean approved, String note) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("vendorName", vendorName);
            context.setVariable("approved", approved);
            context.setVariable("note", note);
            context.setVariable("approvalDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("baseUrl", "http://localhost:4200/");

            String templateName = approved ? "email/vendor-approval-success" : "email/vendor-approval-rejected";
            String htmlContent = templateEngine.process(templateName, context);

            String subject = approved
                    ? "Chúc mừng! Cửa hàng " + vendorName + " đã được phê duyệt"
                    : "Thông báo về đơn đăng ký cửa hàng " + vendorName;

            sendHtmlEmail(email, subject, htmlContent);

        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email thông báo", e);
        }
    }

    @Override
    public void sendEmailVerification(String email, String fullName, String verificationToken) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("verificationUrl", domainUrl + "/api/auth/verify-email?token=" + verificationToken);
            context.setVariable("baseUrl", domainUrl);

            String htmlContent = templateEngine.process("email/email-verification", context);

            sendHtmlEmail(
                    email,
                    "Xác thực email của bạn - CrabFood",
                    htmlContent
            );

        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email xác thực", e);
        }
    }

    @Override
    public void sendPasswordReset(String email, String fullName, String resetToken) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("resetUrl", domainUrl + "/reset-password?token=" + resetToken);
            context.setVariable("baseUrl", domainUrl);
            context.setVariable("expiryTime", "24 giờ");

            String htmlContent = templateEngine.process("email/password-reset", context);

            sendHtmlEmail(
                    email,
                    "Đặt lại mật khẩu - CrabFood",
                    htmlContent
            );

        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email đặt lại mật khẩu", e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        try {
            helper.setFrom(fromEmail, fromName);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    @Override
    public void sendVendorResubmissionNotification(String email, String fullName, String vendorName) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("vendorName", vendorName);
            context.setVariable("updateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("baseUrl", domainUrl);

            String htmlContent = templateEngine.process("email/vendor-resubmission", context);

            sendHtmlEmail(
                    email,
                    "Cập nhật thông tin đăng ký thành công - " + vendorName,
                    htmlContent
            );

        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email thông báo cập nhật thông tin", e);
        }
    }

    @Override
    public void sendVendorReregistrationNotification(String email, String fullName, String vendorName) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("vendorName", vendorName);
            context.setVariable("reregistrationDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("baseUrl", domainUrl);

            String htmlContent = templateEngine.process("email/vendor-reregistration", context);

            sendHtmlEmail(
                    email,
                    "Đăng ký lại thành công - " + vendorName,
                    htmlContent
            );

        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email thông báo đăng ký lại", e);
        }
    }
}