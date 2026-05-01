package com.codey.fatcat.entity;

import com.codey.fatcat.enums.BillStatus;
import com.codey.fatcat.enums.RecurringFrequency;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @ToString.Exclude
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ToString.Exclude
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "account_id")
    private Account account;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String description;

    @NotNull
    @Column(nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate lastPaidDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status;

    @Column(nullable = false)
    private boolean isRecurring;

    @Enumerated(EnumType.STRING)
    private RecurringFrequency frequency;
}
