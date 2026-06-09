package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BankingService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Account createAccount(String username, String password) {
        if (accountRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists!");
        }
        Account newAccount = new Account(username, password);
        return accountRepository.save(newAccount);
    }

    public Account login(String username, String password) {
        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        if (accountOpt.isPresent() && accountOpt.get().getPassword().equals(password)) {
            return accountOpt.get();
        }
        throw new IllegalArgumentException("Invalid username or password!");
    }

    @Transactional
    public Account deposit(Long accountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        
        account.setBalance(account.getBalance() + amount);
        Transaction transaction = new Transaction("Deposit", amount, account);
        account.addTransaction(transaction);
        
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(Long accountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        
        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds!");
        }
        
        account.setBalance(account.getBalance() - amount);
        Transaction transaction = new Transaction("Withdrawal", amount, account);
        account.addTransaction(transaction);
        
        return accountRepository.save(account);
    }

    @Transactional
    public void transfer(Long fromAccountId, String toUsername, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
        
        Account toAccount = accountRepository.findByUsername(toUsername)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        if (fromAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds!");
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        Transaction outTransaction = new Transaction("Transfer Out to " + toUsername, amount, fromAccount);
        fromAccount.addTransaction(outTransaction);

        Transaction inTransaction = new Transaction("Transfer In from " + fromAccount.getUsername(), amount, toAccount);
        toAccount.addTransaction(inTransaction);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    public List<Transaction> getTransactionHistory(Long accountId) {
        return transactionRepository.findByAccountIdOrderByTimestampDesc(accountId);
    }
}
