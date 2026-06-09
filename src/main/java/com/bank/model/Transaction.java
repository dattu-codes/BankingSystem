package com.bank.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private String type;
    private double amount;
    private LocalDateTime timestamp;

    public Transaction(String type, double amount, Account account) {
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.timestamp = LocalDateTime.now();
    }
}
