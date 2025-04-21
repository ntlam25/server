package com.example.crabfood_api.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService implements IEmailService{

    private final JavaMailSender mailSender;
    private final String domainUrl;

    public EmailService(JavaMailSender mailSender, 
        @Value("${application.domain-url}") String domainUrl) {
        this.mailSender = mailSender;
        this.domainUrl = domainUrl;
    }

    @Override
    public void sendVerificationEmail(String toEmail, String verificationToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String verificationUrl = domainUrl + "api/auth/verify-email?token=" + verificationToken;

        String htmlContent = "<html><body>"
                + "<h1 style='color: #0066cc;'>Xác thực email CrabFood</h1>"
                + "<p>Vui lòng click vào nút bên dưới để xác thực email:</p>"
                + "<a href='" + verificationUrl + "' style='"
                + "background-color: #0066cc; color: white; padding: 10px 20px;"
                + "text-decoration: none; border-radius: 5px;'>Xác thực Email</a>"
                + "<p>Hoặc copy link này vào trình duyệt:<br>"
                + verificationUrl + "</p>"
                + "</body></html>";

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