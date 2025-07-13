package com.codey.fatcat.utils;

import com.codey.fatcat.dto.AccountDTO;
import com.codey.fatcat.dto.TransactionDTO;
import com.codey.fatcat.dto.UserDTO;
import com.codey.fatcat.entity.Account;
import com.codey.fatcat.entity.Transaction;
import com.codey.fatcat.entity.User;

public class DTOConverter {

    public static UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getAccounts());
    }

    public static AccountDTO convertToDTO(Account account) {
        return new AccountDTO(account.getId(), account.getAccountType(), account.getBalance(), account.getUser().getId());
    }

    public static TransactionDTO convertToDTO(Transaction transaction) {
        return new TransactionDTO(transaction.getId(), transaction.getDate(), transaction.getAmount(), transaction.getMerchant(), transaction.getCategory(), transaction.isReimbursable(), transaction.getTransactionType(), transaction.getAccount().getId());

    }
}
