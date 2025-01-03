package com.codesumn.accounts_payables_system_springboot.application.dtos.params;

import com.codesumn.accounts_payables_system_springboot.application.validators.ValidAccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@ValidAccountStatus
public class AccountChangeStatusParamDTO {
    @Schema(defaultValue = "PAID")
    private String status;
}
