package org.romaric.accountservice;

import org.romaric.accountservice.domain.AccountType;
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
    CommandLineRunner commandLineRunner(EbankService eBankService){
        return args -> {
            eBankService.saveNewAccount(65000, AccountType.CURRENT_ACCOUNT,"FCFA",Long.valueOf(1));
            eBankService.saveNewAccount(12000, AccountType.SAVING_ACCOUNT,"FCFA",Long.valueOf(1));
            eBankService.saveNewAccount(5000, AccountType.CURRENT_ACCOUNT,"FCFA",Long.valueOf(2));
        };
    }

}
