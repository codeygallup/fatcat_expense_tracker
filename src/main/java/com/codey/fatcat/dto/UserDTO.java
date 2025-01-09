package com.codey.fatcat.dto;

import com.codey.fatcat.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {

  private UUID id;
  private String name;
  private String email;
  private Set<Account> accounts;

}
