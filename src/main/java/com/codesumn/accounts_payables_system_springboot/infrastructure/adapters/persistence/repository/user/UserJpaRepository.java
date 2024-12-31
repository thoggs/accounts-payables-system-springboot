package com.codesumn.accounts_payables_system_springboot.infrastructure.adapters.persistence.repository.user;

import com.codesumn.accounts_payables_system_springboot.domain.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserModel, UUID>, JpaSpecificationExecutor<UserModel> {
    Optional<UserModel> findByEmail(String email);

    boolean existsByEmail(String email);
}
