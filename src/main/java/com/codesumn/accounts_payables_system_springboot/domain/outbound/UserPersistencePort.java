package com.codesumn.accounts_payables_system_springboot.domain.outbound;

import com.codesumn.accounts_payables_system_springboot.domain.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserPersistencePort {
    Page<UserModel> findAll(String searchTerm, Pageable pageable);

    Optional<UserModel> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UserModel> findById(UUID id);

    void saveUser(UserModel userModel);

    void deleteUser(UserModel userModel);
}
