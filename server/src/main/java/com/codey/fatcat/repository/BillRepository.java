package com.codey.fatcat.repository;

import com.codey.fatcat.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {
    List<Bill> findAllByUserId(UUID userId);

    List<Bill> findAllByUserIdAndDueDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);
}
