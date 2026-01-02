package br.edu.ufape.bank.dto.responses;

import java.math.BigDecimal;

import br.edu.ufape.bank.model.Account;
import br.edu.ufape.bank.model.enums.AccountType;

public record AccountResponseDTO(
    Long id,
    Long userId,
    String accountNumber,
    AccountType accountType,
    BigDecimal balance
) {
    public AccountResponseDTO(Account account) {
        this(
            account.getId(),
            account.getUser().getId(),
            account.getAccountNumber(),
            account.getAccountType(),
            account.getBalance()
        );
    }
}
