package com.codey.fatcat.enums;

public enum TransactionType {
 DEPOSIT, WITHDRAWAL;

  public boolean addToBalance() {
   return this == DEPOSIT;
  }
}
