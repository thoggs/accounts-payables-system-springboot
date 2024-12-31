package com.codesumn.accounts_payables_system_springboot.domain.outbound;

public interface JwtServicePort {
    String generateToken(String username);
}