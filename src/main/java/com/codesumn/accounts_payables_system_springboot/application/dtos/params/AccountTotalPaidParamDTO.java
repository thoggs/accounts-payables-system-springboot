package com.codesumn.accounts_payables_system_springboot.application.dtos.params;

import com.codesumn.accounts_payables_system_springboot.application.validators.account.ValidTotalPaidRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidTotalPaidRequest
public class AccountTotalPaidParamDTO {
    @Schema(defaultValue = "2025-01-10")
    private String startDate;
    @Schema(defaultValue = "2025-01-10")
    private String endDate;
}
