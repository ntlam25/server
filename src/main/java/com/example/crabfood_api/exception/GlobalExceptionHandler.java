package com.example.crabfood_api.exception;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", ex.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", ex.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "Invalid username/email or password");
        return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", ex.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<Map<String, String>> handleEmailException(EmailException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "Send email failed" + ex.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddressRetrievalException.class)
    public ResponseEntity<Map<String, String>> handleParseJsonException(AddressRetrievalException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", ex.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("error", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(ExpiredJwtException .class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwtException(ExpiredJwtException  ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("error", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", ex.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
