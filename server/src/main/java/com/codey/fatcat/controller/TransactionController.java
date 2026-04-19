package com.codey.fatcat.controller;

import com.codey.fatcat.dto.TransactionDTO;
import com.codey.fatcat.entity.Transaction;
import com.codey.fatcat.service.TransactionService;
import com.codey.fatcat.utils.DTOConverter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<TransactionDTO>> getAllTransactions(@RequestParam(required = false) UUID accountId) {
        List<Transaction> transactions = accountId != null
                ? transactionService.getTransactionsByAccount(accountId)
                : transactionService.getAllTransactions();
        return ResponseEntity.ok(DTOConverter.toList(transactions, DTOConverter::convertToDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable UUID id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(DTOConverter.convertToDTO(transaction));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<TransactionDTO>> getRecentTransactions() {
        Page<Transaction> transactions = transactionService.getRecentTransactions();
        return ResponseEntity.ok(DTOConverter.toList(transactions.getContent(), DTOConverter::convertToDTO));
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO transaction) {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(DTOConverter.convertToDTO(createdTransaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable UUID id, @Valid @RequestBody TransactionDTO transaction) {
        Transaction updatedTransaction = transactionService.updateTransaction(id, transaction);
        return ResponseEntity.accepted().body(DTOConverter.convertToDTO(updatedTransaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        return transactionService.deleteTransaction(id) ? ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }
}
