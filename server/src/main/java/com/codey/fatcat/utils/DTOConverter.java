package com.codey.fatcat.utils;

import com.codey.fatcat.dto.AccountDTO;
import com.codey.fatcat.dto.BillDTO;
import com.codey.fatcat.dto.TransactionDTO;
import com.codey.fatcat.dto.UserDTO;
import com.codey.fatcat.entity.Account;
import com.codey.fatcat.entity.Bill;
import com.codey.fatcat.entity.Transaction;
import com.codey.fatcat.entity.User;

import java.util.List;
import java.util.function.Function;

public class DTOConverter {

    public static UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getAccounts());
    }

    public static AccountDTO convertToDTO(Account account) {
        return new AccountDTO(account.getId(), account.getAccountType(), account.getName(), account.getBalance(), account.getUser().getId());
    }

    public static TransactionDTO convertToDTO(Transaction transaction) {
        return new TransactionDTO(transaction.getId(), transaction.getDate(), transaction.getAmount(), transaction.getMerchant(), transaction.getCategory(), transaction.isReimbursable(), transaction.getTransactionType(), transaction.getAccount().getId());
    }

    public static BillDTO convertToDTO(Bill bill) {
        return new BillDTO(
                bill.getId(),
                bill.getName(),
                bill.getDescription(),
                bill.getAmount(),
                bill.getDueDate(),
                bill.getLastPaidDate(),
                bill.getStatus(),
                bill.isRecurring(),
                bill.getFrequency(),
                bill.getUser().getId(),
                bill.getAccount() != null ? bill.getAccount().getId() : null
        );
    }

    public static <T, R> List<R> toList(List<T> items, Function<T, R> function) {
        return items.stream().map(function).toList();
    }
}
