package br.edu.ufape.bank.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    private LocalDateTime timestamp;
    private String type; //TODO:ENUM for transaction

    public Transaction(Double amount, LocalDateTime timestamp, String type) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.type = type;
    }
}