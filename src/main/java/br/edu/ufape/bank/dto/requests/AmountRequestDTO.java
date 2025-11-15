// Em br.edu.ufape.bank.dto.requests.AmountRequestDTO.java
package br.edu.ufape.bank.dto.requests;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AmountRequestDTO {

    @NotNull
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;
    
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}