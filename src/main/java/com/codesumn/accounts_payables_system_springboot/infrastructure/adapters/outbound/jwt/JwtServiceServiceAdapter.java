package com.codesumn.accounts_payables_system_springboot.infrastructure.adapters.outbound.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.codesumn.accounts_payables_system_springboot.application.config.EnvironConfig;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.JwtServicePort;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtServiceServiceAdapter implements JwtServicePort {

    private final String jwtSecret = EnvironConfig.JWT_SECRET;
    private final long jwtExpirationTime = EnvironConfig.JWT_EXPIRATION_TIME;

    @Override
    public String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .sign(Algorithm.HMAC256(jwtSecret));
    }
}