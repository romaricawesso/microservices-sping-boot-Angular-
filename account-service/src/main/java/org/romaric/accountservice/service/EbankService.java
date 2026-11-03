package org.romaric.accountservice.service;

import jakarta.transaction.Transactional;
import org.romaric.accountservice.domain.*;
import org.romaric.accountservice.exceptions.AccountNotFoundException;
import org.romaric.accountservice.exceptions.BalanceNotsifficientException;
import org.romaric.accountservice.exceptions.NegativeAmountException;
import org.romaric.accountservice.repository.AccountRepository;
import org.romaric.accountservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EbankService {
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    public EbankService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account getAccountByid(String accountId){
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("Account not found")

        );
        return account;
    }

    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    public Account saveNewAccount(double initialbalance, AccountType type,String currency, Long customerId){
        if (initialbalance < 0 ) throw new NegativeAmountException("the amount shoud be positive");

        Account account = Account.builder()
                .id(UUID.randomUUID().toString()).
                currency(currency)
                .status(AccountStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .type(type).customerId(customerId).
                balance(initialbalance).build();
        Account saveAccount = accountRepository.save(account);
        AccountTransaction transaction = AccountTransaction.builder()
                .amount(initialbalance)
                .type(TransactionType.CREDIT)
                .dateTime(LocalDateTime.now())
                .account(saveAccount)
                .build();
        transactionRepository.save(transaction);
        return saveAccount;
    }

    public AccountTransaction credits(String accountId, double amount ){
        if (amount <= 0) throw new NegativeAmountException("amount should be positive");
        Account account = getAccountByid(accountId);
        account.setBalance(account.getBalance()+amount);
        Account savedAccount = accountRepository.save(account);
        AccountTransaction accountTransaction = AccountTransaction.builder()
                .amount(amount)
                .dateTime(LocalDateTime.now())
                .type(TransactionType.CREDIT)
                .account(savedAccount)
                .build();
        AccountTransaction transaction = transactionRepository.save(accountTransaction);
        return  accountTransaction;
    }

    public AccountTransaction debit(String accountId, double amount ){
        if (amount <= 0) throw new NegativeAmountException("amount should be positive");
        Account account = getAccountByid(accountId);
        if (account.getBalance() < amount) throw new BalanceNotsifficientException("Balance Not sifficientException");
        AccountTransaction accountTransaction =
                AccountTransaction.builder()
                .amount(amount)
                .dateTime(LocalDateTime.now())
                .type(TransactionType.DEBIT)
                .account(account)
                .build();
        account.setBalance(account.getBalance()-amount);
        Account savedAccount = accountRepository.save(account);
        AccountTransaction transaction = transactionRepository.save(accountTransaction);
        return  transaction;
    }


}
