package com.codesumn.accounts_payables_system_springboot.infrastructure.adapters.persistence.repository.account;

import com.codesumn.accounts_payables_system_springboot.domain.models.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AccountJpaRepository extends
        JpaRepository<AccountModel, UUID>, JpaSpecificationExecutor<AccountModel> {
}