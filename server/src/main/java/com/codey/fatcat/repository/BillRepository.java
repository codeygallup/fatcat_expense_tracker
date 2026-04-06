package com.codey.fatcat.repository;

import com.codey.fatcat.entity.Bill;
import com.codey.fatcat.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {
    List<Bill> findAllByUserId(UUID userId);

    @Query("SELECT b FROM Bill b WHERE b.user.id = :userId AND (b.dueDate BETWEEN :start AND :end OR b.status IN :statuses)")
    List<Bill> findUpcomingOrUnpaid(@Param("userId") UUID userId, @Param("start") LocalDate start, @Param("end") LocalDate end, @Param("statuses") List<BillStatus> statuses);
}
