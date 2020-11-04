package dev.hotdeals.treecreate.repository;

import dev.hotdeals.treecreate.model.Transaction;
import dev.hotdeals.treecreate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<Transaction, Integer>
{
}