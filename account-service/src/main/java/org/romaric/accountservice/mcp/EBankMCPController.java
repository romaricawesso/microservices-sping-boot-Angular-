package org.romaric.accountservice.mcp;

import org.romaric.accountservice.domain.Account;
import org.romaric.accountservice.domain.AccountTransaction;
import org.romaric.accountservice.domain.AccountType;
import org.romaric.accountservice.service.EbankService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EBankMCPController {

    private final EbankService ebankService;

    public EBankMCPController(EbankService ebankService) {
        this.ebankService = ebankService;
    }

    @McpTool(description = "Récupérer un compte par son ID")
    public Account getAccountByid(@McpToolParam(description = "L'ID du compte") String accountId) {
        return ebankService.getAccountByid(accountId);
    }

    @McpTool(description = "Récupérer tous les comptes")
    public List<Account> getAllAccounts() {
        return ebankService.getAllAccounts();
    }

    @McpTool(description = "Créer un nouveau compte bancaire")
    public Account saveNewAccount(
            @McpToolParam(description = "Le solde initial du compte") double initialbalance,
            @McpToolParam(description = "Le type de compte (CURRENT_ACCOUNT ou SAVING_ACCOUNT)") AccountType type,
            @McpToolParam(description = "La devise du compte (ex: FCFA)") String currency,
            @McpToolParam(description = "L'ID du client") Long customerId) {
        return ebankService.saveNewAccount(initialbalance, type, currency, customerId);
    }

    @McpTool(description = "Effectuer un crédit sur un compte")
    public AccountTransaction credit(
            @McpToolParam(description = "L'ID du compte") String accountId,
            @McpToolParam(description = "Le montant à créditer") double amount) {
        return ebankService.credit(accountId, amount);
    }

    @McpTool(description = "Effectuer un débit sur un compte")
    public AccountTransaction debit(
            @McpToolParam(description = "L'ID du compte") String accountId,
            @McpToolParam(description = "Le montant à débiter") double amount) {
        return ebankService.debit(accountId, amount);
    }
}
