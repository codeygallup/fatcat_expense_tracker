package com.codey.fatcat.controller;

import com.codey.fatcat.dto.TransactionDTO;
import com.codey.fatcat.entity.Transaction;
import com.codey.fatcat.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @GetMapping
  public ResponseEntity<List<Transaction>> getAllTransactions() {
    return new ResponseEntity<>(transactionService.getAllTransactions(), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Transaction> getTransactionById(@PathVariable UUID id) {
    return new ResponseEntity<>(transactionService.getTransactionById(id), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDTO transaction) {
    return new ResponseEntity<>(transactionService.createTransaction(transaction), HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteTransaction(@PathVariable UUID id) {
    return transactionService.deleteTransaction(id) ? ResponseEntity.ok("Transaction deleted successfully") :
        ResponseEntity.notFound().build();
  }
}
