package com.bank.service;

import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BankingServiceIntegrationTest {

    @Autowired
    private BankingService bankingService;

    @Autowired
    private AccountRepository accountRepository;

    private Account userA;
    private Account userB;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        userA = bankingService.createAccount("userA", "passA");
        userB = bankingService.createAccount("userB", "passB");
        
        bankingService.deposit(userA.getId(), 1000.0);
        userA = accountRepository.findById(userA.getId()).orElseThrow();
    }

    @Test
    void testTransfer_Success() {
        bankingService.transfer(userA.getId(), "userB", 300.0);

        Account updatedA = accountRepository.findById(userA.getId()).orElseThrow();
        Account updatedB = accountRepository.findById(userB.getId()).orElseThrow();

        assertEquals(700.0, updatedA.getBalance());
        assertEquals(300.0, updatedB.getBalance());
    }

    @Test
    void testTransfer_InsufficientFunds_Rollback() {
        assertThrows(IllegalArgumentException.class, () -> {
            bankingService.transfer(userA.getId(), "userB", 2000.0);
        });

        Account updatedA = accountRepository.findById(userA.getId()).orElseThrow();
        Account updatedB = accountRepository.findById(userB.getId()).orElseThrow();

        assertEquals(1000.0, updatedA.getBalance());
        assertEquals(0.0, updatedB.getBalance());
    }

    @Test
    void testTransfer_NonexistentTarget_Rollback() {
        assertThrows(IllegalArgumentException.class, () -> {
            bankingService.transfer(userA.getId(), "nonexistent", 300.0);
        });

        Account updatedA = accountRepository.findById(userA.getId()).orElseThrow();
        assertEquals(1000.0, updatedA.getBalance());
    }

    @Test
    void testConcurrentDeposits_ThreadSafety() throws InterruptedException {
        int threadCount = 10;
        double depositAmount = 100.0;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    bankingService.deposit(userA.getId(), depositAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Account finalAccount = accountRepository.findById(userA.getId()).orElseThrow();
        assertEquals(2000.0, finalAccount.getBalance());
    }
}
