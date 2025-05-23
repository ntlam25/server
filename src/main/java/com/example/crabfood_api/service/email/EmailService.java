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

@Service
public class EmailService implements IEmailService{

    private final JavaMailSender mailSender;
    private final String domainUrl;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender,
                        @Value("${application.domain-url}") String domainUrl, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.domainUrl = domainUrl;
        this.templateEngine = templateEngine;
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
}