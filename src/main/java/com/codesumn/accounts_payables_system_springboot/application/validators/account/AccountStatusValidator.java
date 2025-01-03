package com.codesumn.accounts_payables_system_springboot.application.validators.account;

import com.codesumn.accounts_payables_system_springboot.application.dtos.params.AccountChangeStatusParamDTO;
import com.codesumn.accounts_payables_system_springboot.shared.enums.AccountStatusEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountStatusValidator implements ConstraintValidator<ValidAccountStatus, AccountChangeStatusParamDTO> {

    @Override
    public boolean isValid(AccountChangeStatusParamDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getStatus() == null || dto.getStatus().isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Account status cannot be null or empty")
                    .addPropertyNode("status")
                    .addConstraintViolation();
            return false;
        }

        try {
            AccountStatusEnum.fromValue(dto.getStatus());
            return true;
        } catch (IllegalArgumentException ex) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid account status. Allowed values are: PENDING, PAID")
                    .addPropertyNode("status")
                    .addConstraintViolation();
            return false;
        }
    }
}