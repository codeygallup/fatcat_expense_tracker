package com.codey.fatcat.entity;

import com.codey.fatcat.enums.TransactionCategory;
import com.codey.fatcat.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private String merchant;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionCategory category;

  @Column(nullable = false, name = "is_reimbursable")
  private boolean isReimbursable;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "transaction_type")
  private TransactionType transactionType;
}

