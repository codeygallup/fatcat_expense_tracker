package com.codey.fatcat.controller;

import com.codey.fatcat.dto.AccountDTO;
import com.codey.fatcat.entity.Account;
import com.codey.fatcat.service.AccountService;
import com.codey.fatcat.utils.DTOConverter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
  public ResponseEntity<List<AccountDTO>> getAllAccounts() {
    List<Account> accounts = accountService.getAllAccounts();
    List<AccountDTO> accountDTOs = accounts.stream()
        .map(DTOConverter::convertToDTO)
        .toList();
    return ResponseEntity.ok(accountDTOs);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AccountDTO> getAccountById(@PathVariable UUID id) {
    Account account = accountService.getAccountById(id);
    return ResponseEntity.ok(DTOConverter.convertToDTO(account));
  }

  @PostMapping
  public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO account) {
    Account createdAccount = accountService.createAccount((account));
    return ResponseEntity.status(HttpStatus.CREATED).body(DTOConverter.convertToDTO(createdAccount));
  }

  @PutMapping("/{id}")
  public ResponseEntity<AccountDTO> updateAccount(@PathVariable UUID id, @Valid @RequestBody AccountDTO account) {
    Account updatedAccount = accountService.updateAccount(id, account);
    return ResponseEntity.accepted().body(DTOConverter.convertToDTO(updatedAccount));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
    return accountService.deleteAccount(id) ? ResponseEntity.noContent().build() :
        ResponseEntity.notFound().build();
  }
}
