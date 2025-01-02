package com.codesumn.accounts_payables_system_springboot.application.controllers;

import com.codesumn.accounts_payables_system_springboot.application.dtos.params.FilterCriteriaParamDTO;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.account.AccountInputRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.account.AccountRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.pagination.PaginationResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.response.ResponseDto;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.AccountServicePort;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountServicePort accountServicePort;

    @Autowired
    public AccountController(AccountServicePort accountServicePort) {
        this.accountServicePort = accountServicePort;
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDto<List<AccountRecordDto>>> index(
            @Valid @ParameterObject @ModelAttribute FilterCriteriaParamDTO parameters
    ) throws IOException {
        return new ResponseEntity<>(accountServicePort.getAll(
                parameters.getPage(),
                parameters.getPageSize(),
                parameters.getSearchTerm(),
                parameters.getSortField()
        ), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<AccountRecordDto>> show(@PathVariable UUID id) {
        return new ResponseEntity<>(accountServicePort.getAccountById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseDto<AccountRecordDto>> store(
            @RequestBody @Valid AccountInputRecordDto accountInputRecordDto
    ) {
        return new ResponseEntity<>(accountServicePort.createAccount(accountInputRecordDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<AccountRecordDto>> update(
            @PathVariable UUID id,
            @RequestBody @Valid AccountInputRecordDto accountInputRecordDto
    ) {
        return new ResponseEntity<>(accountServicePort.updateAccount(id, accountInputRecordDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<AccountRecordDto>> destroy(@PathVariable UUID id) {
        return new ResponseEntity<>(accountServicePort.deleteAccount(id), HttpStatus.OK);
    }
}
