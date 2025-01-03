package com.codesumn.accounts_payables_system_springboot.application.validators;

import com.codesumn.accounts_payables_system_springboot.application.dtos.params.AccountRequestTotalPaidParamDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AccountRequestTotalPaidValidator
        implements ConstraintValidator<ValidTotalPaidRequest, AccountRequestTotalPaidParamDTO> {

    @Override
    public boolean isValid(AccountRequestTotalPaidParamDTO value, ConstraintValidatorContext context) {
        if (value == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Request body cannot be null")
                    .addConstraintViolation();
            return false;
        }

        boolean isValid = true;
        String startDate = value.getStartDate();
        String endDate = value.getEndDate();

        context.disableDefaultConstraintViolation();

        if (startDate == null || startDate.isBlank()) {
            context.buildConstraintViolationWithTemplate("Start date cannot be null or empty")
                    .addPropertyNode("startDate")
                    .addConstraintViolation();
            isValid = false;
        } else {
            try {
                LocalDate.parse(startDate);
            } catch (DateTimeParseException ex) {
                context.buildConstraintViolationWithTemplate("Invalid start date format. Use 'YYYY-MM-DD'")
                        .addPropertyNode("startDate")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        if (endDate == null || endDate.isBlank()) {
            context.buildConstraintViolationWithTemplate("End date cannot be null or empty")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            isValid = false;
        } else {
            try {
                LocalDate.parse(endDate);
            } catch (DateTimeParseException ex) {
                context.buildConstraintViolationWithTemplate("Invalid end date format. Use 'YYYY-MM-DD'")
                        .addPropertyNode("endDate")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}