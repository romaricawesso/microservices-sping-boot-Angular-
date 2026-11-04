package org.romaric.accountservice.rest;


import org.romaric.accountservice.domain.Account;
import org.romaric.accountservice.domain.AccountTransaction;
import org.romaric.accountservice.domain.AccountType;
import org.romaric.accountservice.service.EbankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class EbankRestController {

    private final EbankService ebankService;

    public EbankRestController(EbankService ebankService) {
        this.ebankService = ebankService;
    }

    @GetMapping("/{accountId}")
    public Account getAccountByid(@PathVariable String accountId) {
        return ebankService.getAccountByid(accountId);
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return ebankService.getAllAccounts();
    }

    @PostMapping
    public Account saveNewAccount(@RequestParam double initialbalance,
                                  @RequestParam AccountType type,
                                  @RequestParam String currency,
                                  @RequestParam Long customerId) {
        return ebankService.saveNewAccount(initialbalance, type, currency, customerId);
    }

    @PostMapping("/{accountId}/credit")
    public AccountTransaction credit(@PathVariable String accountId,
                                     @RequestParam double amount) {
        return ebankService.credit(accountId, amount);
    }

    @PostMapping("/{accountId}/debit")
    public AccountTransaction debit(@PathVariable String accountId,
                                    @RequestParam double amount) {
        return ebankService.debit(accountId, amount);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception e){
        return ResponseEntity.internalServerError().body(Map.of("errorMessage",e.getMessage()));
    }


}
