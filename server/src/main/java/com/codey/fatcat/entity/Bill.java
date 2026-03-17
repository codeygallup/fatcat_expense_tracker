package com.codey.fatcat.entity;

import com.codey.fatcat.enums.BillStatus;
import com.codey.fatcat.enums.RecurringFrequency;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bills")
public class Bill extends BaseEntity {

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate lastPaidDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status;

    @Column(nullable = false)
    private boolean isRecurring;

    @Enumerated(EnumType.STRING)
    private RecurringFrequency frequency;
}
