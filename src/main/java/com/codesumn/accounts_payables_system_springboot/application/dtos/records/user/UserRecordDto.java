package com.codesumn.accounts_payables_system_springboot.application.dtos.records.user;

import com.codesumn.accounts_payables_system_springboot.shared.enums.RolesEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserRecordDto(
        @NotNull UUID id,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String email,
        @NotNull RolesEnum role
) {
}
