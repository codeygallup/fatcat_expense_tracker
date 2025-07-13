package com.codey.fatcat.dto;

import com.codey.fatcat.enums.TransactionCategory;
import com.codey.fatcat.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

  private UUID id;
  private LocalDate date;
  private BigDecimal amount;
  private String merchant;
  private TransactionCategory category;
  private boolean isReimbursable;
  private TransactionType transactionType;
  private UUID accountId;

}
