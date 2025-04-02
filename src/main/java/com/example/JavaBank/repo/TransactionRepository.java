package com.example.JavaBank.repo;

import com.example.JavaBank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction , Integer> {
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.accountNumber = :accountNumber AND t.transactionDate >= CAST(CURRENT_DATE AS DATE) AND t.transactionType = 'DEBIT'")
    BigDecimal getTotalWithdrawnToday(@Param("accountNumber") String accountNumber);

//    List<Transaction> findByAccountNumberAndTransactionDateBetween(String accountNumber, LocalDate transactionDate);

}
