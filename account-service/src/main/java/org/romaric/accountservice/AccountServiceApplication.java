package org.romaric.accountservice;

import org.romaric.accountservice.domain.Account;
import org.romaric.accountservice.domain.AccountType;
import org.romaric.accountservice.repository.AccountRepository;

import java.util.List;
import org.romaric.accountservice.service.EbankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(EbankService eBankService, AccountRepository accountRepository){
        return args -> {
            eBankService.saveNewAccount(65000, AccountType.CURRENT_ACCOUNT,"FCFA",Long.valueOf(1));
            eBankService.saveNewAccount(12000, AccountType.SAVING_ACCOUNT,"FCFA",Long.valueOf(1));
            eBankService.saveNewAccount(5000, AccountType.CURRENT_ACCOUNT,"FCFA",Long.valueOf(2));

            List<Account> accounts = eBankService.getAllAccounts();
            for (Account acc : accounts) {
                for (int i = 0; i < 5; i++) {
                    eBankService.credit(acc.getId(), 500+Math.random()*230000);
                    eBankService.debit(acc.getId(), 500+Math.random()*10000);
                }
            }
        };
    }

}
