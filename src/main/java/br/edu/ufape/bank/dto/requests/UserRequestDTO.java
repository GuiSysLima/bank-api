package br.edu.ufape.bank.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
    @NotBlank String name
) {}