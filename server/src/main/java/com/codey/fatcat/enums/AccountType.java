package com.codey.fatcat.enums;

public enum AccountType {
    CHECKING,
    SAVINGS,
    CREDIT_CARD;

    public boolean isLiabilityAccount() {
        return this == CREDIT_CARD;
    }
}
