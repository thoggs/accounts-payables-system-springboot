package com.codesumn.accounts_payables_system_springboot.application.adapters.inbound;

import com.codesumn.accounts_payables_system_springboot.application.dtos.records.account.AccountInputRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.account.AccountRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.metadata.MetadataPaginationRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.pagination.PaginationDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.pagination.PaginationResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.response.ResponseDto;
import com.codesumn.accounts_payables_system_springboot.domain.models.AccountModel;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.AccountPersistencePort;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.AccountServicePort;
import com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors.ResourceNotFoundException;
import com.codesumn.accounts_payables_system_springboot.shared.parsers.SortParser;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountServiceAdapter implements AccountServicePort {

    private final AccountPersistencePort accountPersistencePort;
    private final SortParser sortParser;

    @Autowired
    public AccountServiceAdapter(AccountPersistencePort accountPersistencePort, SortParser sortParser) {
        this.accountPersistencePort = accountPersistencePort;
        this.sortParser = sortParser;
    }

    @Override
    public PaginationResponseDto<List<AccountRecordDto>> getAll(
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
                : Sort.by(Sort.Order.asc("dueDate"));

        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<AccountModel> accountPage = accountPersistencePort.findAll(searchTerm, pageable);

        List<AccountRecordDto> accountRecords = accountPage.getContent().stream()
                .map(account -> new AccountRecordDto(
                        account.getId(),
                        account.getDueDate(),
                        account.getPaymentDate(),
                        account.getAmount(),
                        account.getDescription(),
                        account.getStatus()
                ))
                .collect(Collectors.toList());

        PaginationDto pagination = new PaginationDto(
                accountPage.getNumber() + 1,
                accountPage.getSize(),
                accountPage.getTotalElements(),
                accountPage.getTotalPages()
        );

        MetadataPaginationRecordDto metadata = new MetadataPaginationRecordDto(
                pagination,
                Collections.emptyList()
        );

        return PaginationResponseDto.create(accountRecords, metadata);
    }

    @Override
    public ResponseDto<AccountRecordDto> getAccountById(UUID id) {
        AccountModel account = accountPersistencePort.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        AccountRecordDto accountRecord = new AccountRecordDto(
                account.getId(),
                account.getDueDate(),
                account.getPaymentDate(),
                account.getAmount(),
                account.getDescription(),
                account.getStatus()
        );

        return ResponseDto.create(accountRecord);
    }

    @Override
    public ResponseDto<AccountRecordDto> createAccount(AccountInputRecordDto accountInput) {
        AccountModel newAccount = new AccountModel();
        return getAccountRecordDtoResponseDto(accountInput, newAccount);
    }

    @Override
    public ResponseDto<AccountRecordDto> updateAccount(UUID id, AccountInputRecordDto accountInput) {
        AccountModel existingAccount = accountPersistencePort.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        return getAccountRecordDtoResponseDto(accountInput, existingAccount);
    }

    @Override
    public ResponseDto<AccountRecordDto> deleteAccount(UUID id) {
        AccountModel account = accountPersistencePort.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        accountPersistencePort.deleteAccount(account);

        AccountRecordDto accountRecord = new AccountRecordDto(
                account.getId(),
                account.getDueDate(),
                account.getPaymentDate(),
                account.getAmount(),
                account.getDescription(),
                account.getStatus()
        );

        return ResponseDto.create(accountRecord);
    }

    @NotNull
    private ResponseDto<AccountRecordDto> getAccountRecordDtoResponseDto(AccountInputRecordDto accountInput, AccountModel existingAccount) {
        existingAccount.setDueDate(accountInput.dueDate());
        existingAccount.setPaymentDate(accountInput.paymentDate());
        existingAccount.setAmount(accountInput.amount());
        existingAccount.setDescription(accountInput.description());
        existingAccount.setStatus(accountInput.status());

        accountPersistencePort.saveAccount(existingAccount);

        AccountRecordDto updatedAccountRecord = new AccountRecordDto(
                existingAccount.getId(),
                existingAccount.getDueDate(),
                existingAccount.getPaymentDate(),
                existingAccount.getAmount(),
                existingAccount.getDescription(),
                existingAccount.getStatus()
        );

        return ResponseDto.create(updatedAccountRecord);
    }
}
