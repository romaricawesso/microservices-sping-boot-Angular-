package org.romaric.accountservice.graphql;

import graphql.GraphQLError;
import org.romaric.accountservice.domain.Account;
import org.romaric.accountservice.domain.AccountTransaction;
import org.romaric.accountservice.domain.AccountType;
import org.romaric.accountservice.exceptions.AccountNotFoundException;
import org.romaric.accountservice.exceptions.BalanceNotsifficientException;
import org.romaric.accountservice.exceptions.NegativeAmountException;
import org.romaric.accountservice.service.EbankService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AccountGraphQLController {

    private final EbankService ebankService;

    public AccountGraphQLController(EbankService ebankService) {
        this.ebankService = ebankService;
    }

    @QueryMapping
    public List<Account> getAllAccounts() {
        return ebankService.getAllAccounts();
    }

    @QueryMapping
    public Account getAccountById(@Argument("id") String id) {
        return ebankService.getAccountByid(id);
    }

    @MutationMapping
    public Account saveAccount(@Argument("account") AccountDto account) {
        return ebankService.saveNewAccount(account.initialBalance(), account.type(),
                account.currency(), account.customerId());
    }

    @MutationMapping
    public AccountTransaction credit(@Argument("accountId") String accountId,
                                     @Argument("amount") double amount) {
        return ebankService.credit(accountId, amount);
    }

    @MutationMapping
    public AccountTransaction debit(@Argument("accountId") String accountId,
                                    @Argument("amount") double amount) {
        return ebankService.debit(accountId, amount);
    }

    @GraphQlExceptionHandler
    public GraphQLError handleNotFound(AccountNotFoundException e) {
        return GraphQLError.newError().errorType(ErrorType.NOT_FOUND).message(e.getMessage()).build();
    }

    @GraphQlExceptionHandler({NegativeAmountException.class, BalanceNotsifficientException.class})
    public GraphQLError handleBadRequest(RuntimeException e) {
        return GraphQLError.newError().errorType(ErrorType.BAD_REQUEST).message(e.getMessage()).build();
    }

    public record AccountDto(double initialBalance, AccountType type, String currency, Long customerId) {
    }
}
