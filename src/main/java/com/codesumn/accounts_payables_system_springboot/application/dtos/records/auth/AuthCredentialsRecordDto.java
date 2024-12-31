package com.codesumn.accounts_payables_system_springboot.application.dtos.records.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthCredentialsRecordDto(
        @NotBlank String email,
        @NotBlank String password
) {
}
