package com.bank.model;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String username;
    private String passwordHash;  // store hashed password
    private double balance;
    private List<Transaction> transactions = new ArrayList<>();

    public Account(String username, String passwordHash, double balance) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.balance = balance;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public List<Transaction> getTransactions() { return transactions; }

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }
}
