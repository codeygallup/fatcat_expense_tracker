package com.codey.fatcat.dto;

import com.codey.fatcat.entity.TransactionCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TransactionDTO {

  private UUID id;
  private LocalDate date;
  private BigDecimal amount;
  private String merchant;
  private TransactionCategory category;
  private boolean isReimbursable;
  private String transactionType;
  private UUID accountId;

}
