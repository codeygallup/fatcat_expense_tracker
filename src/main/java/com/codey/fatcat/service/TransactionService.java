package com.codey.fatcat.service;

import com.codey.fatcat.dto.TransactionDTO;
import com.codey.fatcat.entity.Account;
import com.codey.fatcat.entity.Transaction;
import com.codey.fatcat.repository.AccountRepository;
import com.codey.fatcat.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;

  public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
    this.transactionRepository = transactionRepository;
    this.accountRepository = accountRepository;
  }

  public List<Transaction> getAllTransactions() {
    return transactionRepository.findAll();
  }

  public Transaction getTransactionById(UUID id) {
    return transactionRepository.findById(id).orElse(null);
  }

  public Transaction createTransaction(TransactionDTO transaction) {
    Account account = accountRepository.findById(transaction.getAccountId())
        .orElseThrow(() -> new RuntimeException("Account not found"));
    Transaction newTransaction = new Transaction();
    newTransaction.setDate(transaction.getDate());
    newTransaction.setAmount(transaction.getAmount());
    newTransaction.setMerchant(transaction.getMerchant());
    newTransaction.setCategory(transaction.getCategory());
    newTransaction.setReimbursable(transaction.isReimbursable());
    newTransaction.setTransactionType(transaction.getTransactionType());
    newTransaction.setAccount(account);
    return transactionRepository.save(newTransaction);
  }

  public boolean deleteTransaction(UUID id) {
    if (transactionRepository.existsById(id)) {
      transactionRepository.deleteById(id);
      return true;
    }
    return false;
  }
//  public void addTransaction(Transaction transaction) {
//    Account account = transaction.getAccount();
//
//    if ("credit".equalsIgnoreCase(transaction.getTransactionType())) {
//      account.setBalance(account.getBalance() + transaction.getAmount());
//    } else if ("debit".equalsIgnoreCase(transaction.getTransactionType())) {
//      account.setBalance(account.getBalance() - transaction.getAmount());
//    }
//
//    accountRepository.save(account);
//    transactionRepository.save(transaction);
//  }
}
