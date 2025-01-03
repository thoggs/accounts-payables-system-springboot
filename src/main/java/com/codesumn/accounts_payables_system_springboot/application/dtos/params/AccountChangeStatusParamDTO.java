package com.codesumn.accounts_payables_system_springboot.application.dtos.params;

import com.codesumn.accounts_payables_system_springboot.application.validators.account.ValidAccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidAccountStatus
public class AccountChangeStatusParamDTO {
    @Schema(defaultValue = "PAID")
    private String status;
}
