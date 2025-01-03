package com.codesumn.accounts_payables_system_springboot.application.dtos.params;

import com.codesumn.accounts_payables_system_springboot.application.validators.account.ValidDateRange;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ValidDateRange
public class FilterCriteriaParamDTO {

    private String searchTerm;
    private String role;
    private String sortField;
    private Boolean sortDescending;

    @Min(1)
    private Integer page = 1;

    @Min(1)
    private Integer pageSize = 10;

    private LocalDate startDate;
    private LocalDate endDate;
}
