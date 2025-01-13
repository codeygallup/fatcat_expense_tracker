package com.codey.fatcat.repository;

import com.codey.fatcat.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
  List<Transaction> findAllByAccount_User_Email(String email);
}
