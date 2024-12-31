package com.codesumn.accounts_payables_system_springboot.application.adapters.inbound;

import com.codesumn.accounts_payables_system_springboot.application.dtos.records.metadata.MetadataPaginationRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.pagination.PaginationDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.pagination.PaginationResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.response.ResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserInputRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.mappers.UserMapper;
import com.codesumn.accounts_payables_system_springboot.domain.inbound.UserServicePort;
import com.codesumn.accounts_payables_system_springboot.domain.models.UserModel;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.UserPersistencePort;
import com.codesumn.accounts_payables_system_springboot.shared.enums.RolesEnum;
import com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors.EmailAlreadyExistsException;
import com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors.ResourceNotFoundException;
import com.codesumn.accounts_payables_system_springboot.shared.parsers.SortParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceAdapter implements UserServicePort {
    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoder passwordEncoder;
    private final SortParser sortParser;

    @Autowired
    public UserServiceAdapter(
            UserPersistencePort userPersistencePort,
            PasswordEncoder passwordEncoder,
            SortParser sortParser
    ) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoder = passwordEncoder;
        this.sortParser = sortParser;
    }

    @Override
    public PaginationResponseDto<List<UserRecordDto>> getAll(
            int page,
            int pageSize,
            String searchTerm,
            String sorting
    ) throws IOException {
        String decodedSorting = (sorting != null && !sorting.trim().isEmpty() && !"[]".equals(sorting))
                ? URLDecoder.decode(sorting, StandardCharsets.UTF_8)
                : null;

        Sort sort = (decodedSorting != null && !decodedSorting.trim().isEmpty() && !"[]".equals(decodedSorting))
                ? sortParser.parseSorting(decodedSorting)
                : Sort.by(Sort.Order.asc("firstName"));

        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<UserModel> userPage = userPersistencePort.findAll(searchTerm, pageable);

        List<UserRecordDto> userRecords = userPage.getContent().stream()
                .map(user -> new UserRecordDto(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole()
                ))
                .collect(Collectors.toList());

        PaginationDto pagination = new PaginationDto(
                userPage.getNumber() + 1,
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages()
        );

        MetadataPaginationRecordDto metadata = new MetadataPaginationRecordDto(
                pagination,
                Collections.emptyList()
        );

        return PaginationResponseDto.create(userRecords, metadata);
    }

    @Override
    public ResponseDto<UserRecordDto> getUserById(UUID id) {
        UserModel user = userPersistencePort.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        UserRecordDto userRecord = new UserRecordDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseDto.create(userRecord);
    }

    @Override
    public ResponseDto<UserRecordDto> createUser(UserInputRecordDto userInput) {
        userPersistencePort
                .findByEmail(userInput.email())
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException();
                });

        UserModel newUser = UserMapper.fromDto(userInput);

        newUser.setPassword(passwordEncoder.encode(userInput.password()));

        userPersistencePort.saveUser(newUser);

        UserRecordDto newUserRecord = new UserRecordDto(
                newUser.getId(),
                newUser.getFirstName(),
                newUser.getLastName(),
                newUser.getEmail(),
                newUser.getRole()
        );

        return ResponseDto.create(newUserRecord);
    }

    @Override
    public ResponseDto<UserRecordDto> updateUser(UUID id, UserInputRecordDto userInput) {
        UserModel existingUser = userPersistencePort.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        userPersistencePort
                .findByEmail(userInput.email())
                .ifPresent(user -> {
                    if (!user.getEmail().equals(existingUser.getEmail())) {
                        throw new EmailAlreadyExistsException();
                    }
                });

        existingUser.setFirstName(userInput.firstName());
        existingUser.setLastName(userInput.lastName());
        existingUser.setEmail(userInput.email());
        existingUser.setRole(RolesEnum.fromValue(userInput.role()));

        if (!userInput.password().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userInput.password()));
        }

        userPersistencePort.saveUser(existingUser);

        UserRecordDto updatedUserRecord = new UserRecordDto(
                existingUser.getId(),
                existingUser.getFirstName(),
                existingUser.getLastName(),
                existingUser.getEmail(),
                existingUser.getRole()
        );

        return ResponseDto.create(updatedUserRecord);
    }

    @Override
    public ResponseDto<UserRecordDto> deleteUser(UUID id) {
        UserModel user = userPersistencePort.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        userPersistencePort.deleteUser(user);

        UserRecordDto userRecord = new UserRecordDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseDto.create(userRecord);
    }

}
