package com.codesumn.accounts_payables_system_springboot.domain.outbound;

import com.codesumn.accounts_payables_system_springboot.domain.models.AccountModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface AccountPersistencePort {

    Page<AccountModel> findAll(String searchTerm, Pageable pageable);

    Optional<AccountModel> findById(UUID id);

    void saveAccount(AccountModel accountModel);

    void deleteAccount(AccountModel accountModel);
}
