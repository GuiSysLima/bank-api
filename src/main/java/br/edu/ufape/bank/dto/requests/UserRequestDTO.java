package br.edu.ufape.bank.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
    @NotBlank String name,
    @Email @NotBlank String email,
    @NotBlank @Size(min = 11, max = 11) String cpf
    
) {}
