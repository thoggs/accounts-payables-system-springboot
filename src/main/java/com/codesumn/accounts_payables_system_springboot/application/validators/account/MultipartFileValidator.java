package com.codesumn.accounts_payables_system_springboot.application.validators.account;

import com.codesumn.accounts_payables_system_springboot.application.dtos.params.AccountImportParamDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class MultipartFileValidator implements ConstraintValidator<ValidMultipartFile, AccountImportParamDTO> {

    private String[] allowedTypes;
    private long maxSize;

    @Override
    public void initialize(ValidMultipartFile constraintAnnotation) {
        this.allowedTypes = constraintAnnotation.allowedTypes();
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(AccountImportParamDTO value, ConstraintValidatorContext context) {
        MultipartFile file = value.getFile();

        context.disableDefaultConstraintViolation();

        if (file == null || file.isEmpty()) {
            context.buildConstraintViolationWithTemplate("File cannot be null or empty")
                    .addPropertyNode("file")
                    .addConstraintViolation();
            return false;
        }

        if (!isValidContentType(file)) {
            context.buildConstraintViolationWithTemplate(
                            "Invalid file type. Allowed types: " + String.join(", ", allowedTypes))
                    .addPropertyNode("file")
                    .addConstraintViolation();
            return false;
        }

        if (!isValidSize(file)) {
            context.buildConstraintViolationWithTemplate(
                            "File size exceeds the maximum allowed size of " + maxSize + " bytes")
                    .addPropertyNode("file")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean isValidContentType(MultipartFile file) {
        return allowedTypes == null || allowedTypes.length == 0 || Arrays
                .asList(allowedTypes).contains(file.getContentType());
    }

    private boolean isValidSize(MultipartFile file) {
        return file.getSize() <= maxSize;
    }
}
