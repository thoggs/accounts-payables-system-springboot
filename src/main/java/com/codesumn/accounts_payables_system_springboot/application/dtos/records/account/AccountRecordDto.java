package com.codesumn.accounts_payables_system_springboot.application.dtos.records.account;

import com.codesumn.accounts_payables_system_springboot.shared.enums.AccountStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record AccountRecordDto(
        @NotNull UUID id,
        @NotNull Date dueDate,
        Date paymentDate,
        @NotNull BigDecimal amount,
        @NotBlank String description,
        @NotNull AccountStatusEnum status
) {
}