package dev.hotdeals.treecreate.repository;

import dev.hotdeals.treecreate.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Integer>
{
    List<Transaction> findAllByStatus(String status);
}