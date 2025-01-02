package com.codesumn.accounts_payables_system_springboot.infrastructure.adapters.persistence.repository.account;

import com.codesumn.accounts_payables_system_springboot.domain.models.AccountModel;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.AccountPersistencePort;
import com.codesumn.accounts_payables_system_springboot.infrastructure.adapters.persistence.specifications.AccountSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountPersistenceAdapter implements AccountPersistencePort {

    private final AccountJpaRepository accountJpaRepository;

    public AccountPersistenceAdapter(AccountJpaRepository accountJpaRepository) {
        this.accountJpaRepository = accountJpaRepository;
    }

    @Override
    public Page<AccountModel> findAll(String searchTerm, Pageable pageable) {
        Specification<AccountModel> spec = (searchTerm != null && !searchTerm.isEmpty())
                ? AccountSpecifications.searchWithTerm(searchTerm)
                : null;

        return accountJpaRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<AccountModel> findById(UUID id) {
        return accountJpaRepository.findById(id);
    }

    @Override
    public void saveAccount(AccountModel accountModel) {
        accountJpaRepository.save(accountModel);
    }

    @Override
    public void deleteAccount(AccountModel accountModel) {
        accountJpaRepository.delete(accountModel);
    }
}