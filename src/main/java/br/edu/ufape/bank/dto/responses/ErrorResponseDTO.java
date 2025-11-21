package br.edu.ufape.bank.dto.responses;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
    String message,
    int status,
    LocalDateTime timestamp
) {}