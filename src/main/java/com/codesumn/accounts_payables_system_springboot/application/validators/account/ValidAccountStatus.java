package com.codesumn.accounts_payables_system_springboot.application.validators.account;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AccountStatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAccountStatus {
    String message() default "Invalid account status. Allowed values are: PENDING, PAID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}