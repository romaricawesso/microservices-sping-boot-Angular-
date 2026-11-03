package org.romaric.accountservice.repository;

import org.romaric.accountservice.domain.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<AccountTransaction,Long> {
    List<AccountTransaction> findByAccountId(String accountId);


}
