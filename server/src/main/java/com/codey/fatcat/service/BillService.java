package com.codey.fatcat.service;

import com.codey.fatcat.dto.BillDTO;
import com.codey.fatcat.entity.Bill;
import com.codey.fatcat.entity.User;
import com.codey.fatcat.enums.BillStatus;
import com.codey.fatcat.exception.ResourceNotFoundException;
import com.codey.fatcat.exception.UnauthorizedException;
import com.codey.fatcat.repository.BillRepository;
import com.codey.fatcat.repository.UserRepository;
import com.codey.fatcat.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final UserRepository userRepository;

    public BillService(BillRepository billRepository, UserRepository userRepository) {
        this.billRepository = billRepository;
        this.userRepository = userRepository;
    }

    public List<Bill> getAllBills() {
        if (SecurityUtils.hasRole("ADMIN")) {
            return billRepository.findAll();
        }
        User currentUser = SecurityUtils.getCurrentUser(userRepository);
        return billRepository.findAllByUserId(currentUser.getId());
    }

    public Bill getBillById(UUID id) {
        return billRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bill with id: " + id + " not found"));
    }

    public List<Bill> getUpcomingBills() {
        User currentUser = SecurityUtils.getCurrentUser(userRepository);
        LocalDate today = LocalDate.now();
        return billRepository.findAllByUserIdAndDueDateBetween(currentUser.getId(), today, today.plusDays(14));
    }

    public Bill createBill(BillDTO bill) {
        User user = SecurityUtils.getCurrentUser(userRepository);
        Bill newBill = new Bill();
        newBill.setUser(user);
        newBill.setName(bill.getName());
        newBill.setDescription(bill.getDescription());
        newBill.setAmount(bill.getAmount());
        newBill.setDueDate(bill.getDueDate());
        newBill.setRecurring(bill.isRecurring());
        newBill.setFrequency(bill.getFrequency());
        newBill.setStatus(bill.getDueDate().isBefore(LocalDate.now()) ? BillStatus.OVERDUE : BillStatus.UNPAID);
        return billRepository.save(newBill);
    }

    public Bill updateBill(UUID id, BillDTO bill) {
        Bill oldBill = getBillById(id);
        oldBill.setName(bill.getName());
        oldBill.setDescription(bill.getDescription());
        oldBill.setAmount(bill.getAmount());
        oldBill.setDueDate(bill.getDueDate());
        oldBill.setRecurring(bill.isRecurring());
        oldBill.setFrequency(bill.getFrequency());
        oldBill.setStatus(bill.getStatus());
        return billRepository.save(oldBill);
    }

    public Bill markAsPaid(UUID id) {
        Bill bill = getBillById(id);
        bill.setStatus(BillStatus.PAID);
        bill.setLastPaidDate(LocalDate.now());
        return billRepository.save(bill);
    }

    @Transactional
    public boolean deleteBill(UUID id) {
        if (billRepository.existsById(id)) {
            billRepository.deleteById(id);
            return true;
        }
        return false;
    }


}
