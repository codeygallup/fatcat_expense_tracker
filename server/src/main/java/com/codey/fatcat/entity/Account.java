package com.codey.fatcat.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
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

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @JsonManagedReference
  @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Transaction> transactions;

  @Column(name = "account_type", nullable = false)
  private String accountType;

  @Column(nullable = false)
  @PositiveOrZero
  private BigDecimal balance;
}
