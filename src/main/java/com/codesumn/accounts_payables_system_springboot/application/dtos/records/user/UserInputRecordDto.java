package com.codesumn.accounts_payables_system_springboot.application.dtos.records.user;

import jakarta.validation.constraints.NotBlank;

public record UserInputRecordDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String email,
        @NotBlank String password,
        String role
) {
}
