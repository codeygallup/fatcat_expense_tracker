package com.codey.fatcat.dto;

import com.codey.fatcat.enums.BillStatus;
import com.codey.fatcat.enums.RecurringFrequency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillDTO {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate lastPaidDate;
    private BillStatus status;
    private boolean isRecurring;
    private RecurringFrequency frequency;
    private UUID userId;
    private UUID accountId;
}
