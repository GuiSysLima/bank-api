package br.edu.ufape.bank.configs;

import org.springframework.dao.DataIntegrityViolationException;
import br.edu.ufape.bank.dto.responses.ErrorResponseDTO;
import br.edu.ufape.bank.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponseDTO> handleSecurityException(SecurityException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
            ex.getMessage(),
            HttpStatus.FORBIDDEN.value(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        ErrorResponseDTO error = new ErrorResponseDTO(
            "Erro de validação: " + errorMessage,
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex) {
        ex.printStackTrace();
        ErrorResponseDTO error = new ErrorResponseDTO(
            "Ocorreu um erro interno no servidor. Contate o suporte.",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        
        String message = "Erro de conflito de dados. Verifique se o registro já existe.";
        
        String cause = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "";

        if (cause.toLowerCase().contains("cpf")) {
            message = "Este CPF já está cadastrado em outra conta.";
        } else if (cause.toLowerCase().contains("email")) {
            message = "Este email já está em uso.";
        } else if (cause.toLowerCase().contains("keycloak_id")) {
            message = "Usuário já vinculado.";
        }

        ErrorResponseDTO error = new ErrorResponseDTO(
            message,
            HttpStatus.CONFLICT.value(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}