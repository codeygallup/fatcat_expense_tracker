package com.codey.fatcat.entity;

import com.codey.fatcat.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

  @ToString.Exclude
  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ToString.Exclude
  @JsonManagedReference
  @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Transaction> transactions;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "account_type", nullable = false)
  private AccountType accountType;

  @NotNull
  @Column(nullable = false)
  private BigDecimal balance;
}
