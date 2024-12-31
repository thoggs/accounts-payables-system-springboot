package com.codesumn.accounts_payables_system_springboot.application.dtos.records.user;

import com.codesumn.accounts_payables_system_springboot.domain.models.UserModel;
import com.codesumn.accounts_payables_system_springboot.shared.enums.RolesEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AuthUserResponseRecordDto(
        @NotBlank UUID id,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String email,
        @NotNull RolesEnum role
) {
    public AuthUserResponseRecordDto(UserModel user) {
        this(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole());
    }
}
