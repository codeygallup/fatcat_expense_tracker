package com.codey.fatcat.service;

import com.codey.fatcat.dto.AccountDTO;
import com.codey.fatcat.entity.Account;
import com.codey.fatcat.entity.User;
import com.codey.fatcat.exception.ResourceNotFoundException;
import com.codey.fatcat.exception.UnauthorizedException;
import com.codey.fatcat.repository.AccountRepository;
import com.codey.fatcat.repository.UserRepository;
import com.codey.fatcat.utils.SecurityUtils;
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
    String currentUserEmail = SecurityUtils.getCurrentUserEmail();
    if (SecurityUtils.hasRole("ADMIN")) {
      return accountRepository.findAll();
    }
    User currentUser =
        userRepository.findByEmail(currentUserEmail).orElseThrow(() -> new UnauthorizedException("User not found"));
    return accountRepository.findAllByUserId(currentUser.getId());
  }

  public Account getAccountById(UUID id) {
    SecurityUtils.validateAccountAccess(id, accountRepository);
    return accountRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Account with id: " + id + " not found"));
  }

  public Account createAccount(AccountDTO account) {
    SecurityUtils.validateUserAccess(account.getUserId(), userRepository);
    User user = userRepository.findById(account.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User with id: " + account.getUserId() + " not found"));
    Account newAccount = new Account();
    newAccount.setAccountType(account.getAccountType());
    newAccount.setBalance(account.getBalance());
    newAccount.setUser(user);
    return accountRepository.save(newAccount);
  }

  public Account updateAccount(UUID id, AccountDTO account) {
    SecurityUtils.validateAccountAccess(id, accountRepository);
    Account oldAccount = getAccountById(id);
    oldAccount.setAccountType(account.getAccountType());
    oldAccount.setBalance(account.getBalance());
    oldAccount.setUser(oldAccount.getUser());
    return accountRepository.save(oldAccount);
  }

  @Transactional
  public boolean deleteAccount(UUID id) {
    SecurityUtils.validateAccountAccess(id, accountRepository);
    if (accountRepository.existsById(id)) {
      Account account = accountRepository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Account with id: " + id + " not found"));
      if (account != null) {
        account.getUser().getAccounts().remove(account);
        accountRepository.deleteById(id);
        return true;
      }
    }
    return false;
  }

}



