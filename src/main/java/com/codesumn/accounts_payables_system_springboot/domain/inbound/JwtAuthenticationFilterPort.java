package com.codesumn.accounts_payables_system_springboot.domain.inbound;

import org.springframework.web.filter.OncePerRequestFilter;

public interface JwtAuthenticationFilterPort {
    OncePerRequestFilter getFilter();
}
