package com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors;

public class CustomUnauthorizedException extends RuntimeException {
    public CustomUnauthorizedException(String message) {
        super(message);
    }
}
