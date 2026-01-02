package br.edu.ufape.bank.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class SecurityException extends RuntimeException {
    public SecurityException(String message) {
        super(message);
    }
}