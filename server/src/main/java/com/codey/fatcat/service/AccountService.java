package com.codey.fatcat.service;

import com.codey.fatcat.dto.AccountDTO;
import com.codey.fatcat.entity.Account;
import com.codey.fatcat.entity.User;
import com.codey.fatcat.repository.AccountRepository;
import com.codey.fatcat.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
  }

  public List<Account> getAllAccounts() {
    return accountRepository.findAll();
  }

  public Account getAccountById(UUID id) {
    return accountRepository.findById(id).orElse(null);
  }

  public Account createAccount(AccountDTO account) {
    User user = userRepository.findById(account.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));
    Account newAccount = new Account();
    newAccount.setAccountType(account.getAccountType());
    newAccount.setBalance(account.getBalance());
    newAccount.setUser(user);
    return accountRepository.save(newAccount);
  }

  public Account updateAccount(UUID id, AccountDTO account) {
    Account oldAccount = getAccountById(id);
    oldAccount.setAccountType(account.getAccountType());
    oldAccount.setBalance(account.getBalance());
    oldAccount.setUser(oldAccount.getUser());
    return accountRepository.save(oldAccount);
  }

  @Transactional
  public boolean deleteAccount(UUID id) {
    if (accountRepository.existsById(id)) {
      Account account = accountRepository.findById(id).orElse(null);
      if (account != null) {
        account.getUser().getAccounts().remove(account);
        accountRepository.deleteById(id);
        return true;
      }
    }
    return false;
  }

}



