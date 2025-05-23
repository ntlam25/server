package com.example.crabfood_api.controller;

import com.example.crabfood_api.service.email.IEmailService;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    private final IEmailService service;

    public EmailController(IEmailService service) {
        this.service = service;
    }

    @PostMapping("/send-verify-email")
    public ResponseEntity<Void> sendEmailVerify(@RequestParam String email) throws MessagingException {
        String token = UUID.randomUUID().toString();
        service.sendVerificationEmail(email,token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
