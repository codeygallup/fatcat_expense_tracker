package com.codey.fatcat.controller;

import com.codey.fatcat.dto.AccountDTO;
import com.codey.fatcat.entity.Account;
import com.codey.fatcat.service.AccountService;
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
@RequestMapping("/accounts")
public class AccountController {

  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @GetMapping
  public ResponseEntity<List<Account>> getAllAccounts() {
    return ResponseEntity.ok(accountService.getAllAccounts());
//    List<Account> accounts = accountService.getAllAccounts();
//    List<AccountDTO> accountDTOS = accounts.stream()
//        .map(account -> new AccountDTO(account.getId(), account.getAccountType(),
//                                       account.getBalance(),
//                                       account.getUser().getId()))
//        .toList();
//    return new ResponseEntity<>(accountDTOS, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Account> getAccountById(@PathVariable UUID id) {
//    Account account = accountService.getAccountById(id);
//    return new ResponseEntity<>(new AccountDTO(account.getId(), account.getAccountType(),
//                                               account.getBalance(),
//                                               account.getUser().getId()), HttpStatus.OK);
    return new ResponseEntity<>(accountService.getAccountById(id), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<Account> createAccount(@RequestBody AccountDTO account) {
    return new ResponseEntity<>(accountService.createAccount(account), HttpStatus.CREATED);
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteAccount(@PathVariable UUID id) {
    return accountService.deleteAccount(id) ? ResponseEntity.ok("Account deleted successfully") :
        ResponseEntity.notFound().build();
  }
}
