package com.codesumn.accounts_payables_system_springboot.application.dtos.params;

import com.codesumn.accounts_payables_system_springboot.application.validators.ValidTotalPaidRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidTotalPaidRequest
public class AccountRequestTotalPaidParamDTO {
    @Schema(defaultValue = "2025-01-10")
    private String startDate;
    @Schema(defaultValue = "2025-01-10")
    private String endDate;
}
