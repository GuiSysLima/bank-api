package br.edu.ufape.bank.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message, Long id) {
        super(message + " not found with id: " + id);
    }
    
    public ResourceNotFoundException(String message, String identifier) {
        super(message + " not found with identifier: " + identifier);
    }
}
