package com.example.crabfood_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AddressRetrievalException extends RuntimeException {
    
    public AddressRetrievalException(String message) {
        super(message);
    }
}

