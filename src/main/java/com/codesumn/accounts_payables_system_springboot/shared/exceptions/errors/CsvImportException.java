package com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors;

import lombok.Getter;

import java.util.List;

@Getter
public class CsvImportException extends RuntimeException {
    private final List<String> errors;

    public CsvImportException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

}
