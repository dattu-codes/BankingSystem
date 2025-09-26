package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;
import java.util.HashMap;
import java.util.Map;

public class BankingService {
    private Map<String, Account> accounts = new HashMap<>();

    public boolean createAccount(String username, String password) {
        if (accounts.containsKey(username)) {
            System.out.println("Username already exists!");
            return false;
        }
        String hash = PasswordUtil.hashPassword(password);
        accounts.put(username, new Account(username, hash, 0.0));
        System.out.println("Account created successfully!");
        return true;
    }

    public Account login(String username, String password) {
        if (accounts.containsKey(username)) {
            Account acc = accounts.get(username);
            String hash = PasswordUtil.hashPassword(password);
            if (acc.getPasswordHash().equals(hash)) {
                System.out.println("Login successful!");
                return acc;
            }
        }
        System.out.println("Invalid username/password!");
        return null;
    }

    public void deposit(Account acc, double amount) {
        acc.setBalance(acc.getBalance() + amount);
        Transaction t = new Transaction("DEPOSIT", amount);
        acc.addTransaction(t);
        System.out.println("Deposited: " + amount + ", New Balance: " + acc.getBalance());
    }

    public void withdraw(Account acc, double amount) {
        if (acc.getBalance() >= amount) {
            acc.setBalance(acc.getBalance() - amount);
            Transaction t = new Transaction("WITHDRAW", amount);
            acc.addTransaction(t);
            System.out.println("Withdrawn: " + amount + ", New Balance: " + acc.getBalance());
        } else {
            System.out.println("Insufficient funds!");
        }
    }

    public void printTransactionHistory(Account acc) {
        System.out.println("\n--- Transaction History ---");
        for (Transaction t : acc.getTransactions()) {
            System.out.println(t);
        }
    }
}
