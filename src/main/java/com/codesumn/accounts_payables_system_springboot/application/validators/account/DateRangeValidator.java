package com.codesumn.accounts_payables_system_springboot.application.validators.account;

import com.codesumn.accounts_payables_system_springboot.application.dtos.params.FilterCriteriaParamDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, FilterCriteriaParamDTO> {

    @Override
    public boolean isValid(FilterCriteriaParamDTO value, ConstraintValidatorContext context) {
        boolean startDatePresent = value.getStartDate() != null;
        boolean endDatePresent = value.getEndDate() != null;

        context.disableDefaultConstraintViolation();

        if (startDatePresent && !endDatePresent) {
            context.buildConstraintViolationWithTemplate("endDate must be provided if startDate is present")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            return false;
        }

        if (!startDatePresent && endDatePresent) {
            context.buildConstraintViolationWithTemplate("startDate must be provided if endDate is present")
                    .addPropertyNode("startDate")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
