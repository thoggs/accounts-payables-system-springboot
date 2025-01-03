package com.codesumn.accounts_payables_system_springboot.application.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AccountRequestTotalPaidValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTotalPaidRequest {
    String message() default "Invalid date range or missing dates";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}