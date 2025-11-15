package br.edu.ufape.bank.dto.requests;

import br.edu.ufape.bank.model.enums.AccountType;
import jakarta.validation.constraints.NotNull;

public record AccountRequestDTO(
    
    @NotNull(message = "O tipo da conta não pode ser nulo")
    AccountType accountType
    
) {}