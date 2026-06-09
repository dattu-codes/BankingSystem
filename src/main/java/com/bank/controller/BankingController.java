package com.bank.controller;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.service.BankingService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BankingController {

    @Autowired
    private BankingService bankingService;

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        try {
            Account account = bankingService.createAccount(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(new AccountResponse(account));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Account account = bankingService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(new AccountResponse(account));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/account/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long id, @RequestBody AmountRequest request) {
        try {
            Account account = bankingService.deposit(id, request.getAmount());
            return ResponseEntity.ok(new AccountResponse(account));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/account/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long id, @RequestBody AmountRequest request) {
        try {
            Account account = bankingService.withdraw(id, request.getAmount());
            return ResponseEntity.ok(new AccountResponse(account));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/account/{id}/transfer")
    public ResponseEntity<?> transfer(@PathVariable Long id, @RequestBody TransferRequest request) {
        try {
            bankingService.transfer(id, request.getToUsername(), request.getAmount());
            return ResponseEntity.ok(new SuccessResponse("Transfer successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/account/{id}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable Long id) {
        try {
            List<Transaction> transactions = bankingService.getTransactionHistory(id);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // DTOs
    @Data
    static class AuthRequest {
        private String username;
        private String password;
    }

    @Data
    static class AmountRequest {
        private double amount;
    }

    @Data
    static class TransferRequest {
        private String toUsername;
        private double amount;
    }

    @Data
    static class AccountResponse {
        private Long id;
        private String username;
        private double balance;

        public AccountResponse(Account account) {
            this.id = account.getId();
            this.username = account.getUsername();
            this.balance = account.getBalance();
        }
    }

    @Data
    static class ErrorResponse {
        private String error;
        public ErrorResponse(String error) { this.error = error; }
    }

    @Data
    static class SuccessResponse {
        private String message;
        public SuccessResponse(String message) { this.message = message; }
    }
}
