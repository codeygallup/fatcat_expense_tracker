package com.codey.fatcat.service;

import com.codey.fatcat.dto.TransactionDTO;
import com.codey.fatcat.entity.Account;
import com.codey.fatcat.entity.Transaction;
import com.codey.fatcat.enums.TransactionType;
import com.codey.fatcat.exception.ResourceNotFoundException;
import com.codey.fatcat.repository.AccountRepository;
import com.codey.fatcat.repository.TransactionRepository;
import com.codey.fatcat.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    String currentUserEmail = SecurityUtils.getCurrentUserEmail();
    if (SecurityUtils.hasRole("ADMIN")) {
      return transactionRepository.findAll();
    }
    return transactionRepository.findAllByAccount_User_Email(currentUserEmail);
  }

  public Transaction getTransactionById(UUID id) {
    Transaction transaction = transactionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Transaction with id: " + id + " not found"));
    SecurityUtils.validateAccountAccess(transaction.getAccount().getId(), accountRepository);
    return transaction;
  }

  @Transactional
  public Transaction createTransaction(TransactionDTO transaction) {
    SecurityUtils.validateAccountAccess(transaction.getAccountId(), accountRepository);
    Account account = accountRepository.findById(transaction.getAccountId())
        .orElseThrow(() -> new ResourceNotFoundException("Account with id " + transaction.getAccountId() + " not found"));

    Transaction newTransaction = new Transaction();
    newTransaction.setDate(transaction.getDate());
    newTransaction.setAmount(transaction.getAmount());
    newTransaction.setMerchant(transaction.getMerchant());
    newTransaction.setCategory(transaction.getCategory());
    newTransaction.setReimbursable(transaction.isReimbursable());
    newTransaction.setTransactionType(transaction.getTransactionType());
    newTransaction.setAccount(account);

    BigDecimal currentBalance = account.getBalance();
    BigDecimal newBalance = applyTransaction(currentBalance, transaction.getAmount(), transaction.getTransactionType(), account);

    account.setBalance(newBalance);
    accountRepository.save(account);

    return transactionRepository.save(newTransaction);
}


  @Transactional
public Transaction updateTransaction(UUID id, TransactionDTO transactionDTO) {
    Transaction existingTransaction = getTransactionById(id);
    Account account = getAccount(transactionDTO, existingTransaction);
    accountRepository.save(account);

    // Update transaction fields
    existingTransaction.setDate(transactionDTO.getDate());
    existingTransaction.setAmount(transactionDTO.getAmount());
    existingTransaction.setMerchant(transactionDTO.getMerchant());
    existingTransaction.setCategory(transactionDTO.getCategory());
    existingTransaction.setReimbursable(transactionDTO.isReimbursable());
    existingTransaction.setTransactionType(transactionDTO.getTransactionType());

    return transactionRepository.save(existingTransaction);
}

  private static Account getAccount(TransactionDTO transactionDTO, Transaction existingTransaction) {
    Account account = existingTransaction.getAccount();

    // Reverse the old transaction's effect
    BigDecimal currentBalance = account.getBalance();
    BigDecimal balanceAfterReversal = reverseTransaction(currentBalance,
            existingTransaction.getAmount(), existingTransaction.getTransactionType(), account);
    // Then apply the new transaction
    BigDecimal newBalance = applyTransaction(balanceAfterReversal,
            transactionDTO.getAmount(), transactionDTO.getTransactionType(), account);

    account.setBalance(newBalance);
    return account;
  }

  @Transactional
public boolean deleteTransaction(UUID id) {
    Transaction transaction = getTransactionById(id);
    Account account = transaction.getAccount();

    // Reverse the transaction's effect on balance
    BigDecimal currentBalance = account.getBalance();
    BigDecimal newBalance = reverseTransaction(currentBalance, transaction.getAmount(), transaction.getTransactionType(), account);

    account.setBalance(newBalance);
    accountRepository.save(account);

    transactionRepository.deleteById(id);
    return true;
}

  private static BigDecimal applyTransaction(BigDecimal currentBalance, BigDecimal amount,
                                             TransactionType transactionType, Account account) {
    if (account.getAccountType().isLiabilityAccount()) {
      return transactionType.addToBalance()
              ? currentBalance.subtract(amount)
              : currentBalance.add(amount);
    } else {
      return transactionType.addToBalance()
              ? currentBalance.add(amount)
              : currentBalance.subtract(amount);
    }
  }

  private static BigDecimal reverseTransaction(BigDecimal currentBalance, BigDecimal amount,
                                               TransactionType transactionType, Account account) {
    if (account.getAccountType().isLiabilityAccount()) {
      return transactionType.addToBalance()
              ? currentBalance.add(amount)
              : currentBalance.subtract(amount);
    } else {
      return transactionType.addToBalance()
              ? currentBalance.subtract(amount)
              : currentBalance.add(amount);
    }
  }
}
