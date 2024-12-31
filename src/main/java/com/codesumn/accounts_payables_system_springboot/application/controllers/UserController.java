package com.codesumn.accounts_payables_system_springboot.application.controllers;

import com.codesumn.accounts_payables_system_springboot.application.dtos.params.FilterCriteriaParamDTO;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.pagination.PaginationResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.response.ResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserInputRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserRecordDto;
import com.codesumn.accounts_payables_system_springboot.domain.inbound.UserServicePort;
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
@RequestMapping("/api/users")
public class UserController {

    private final UserServicePort userServicePort;

    @Autowired
    public UserController(UserServicePort userServicePort) {
        this.userServicePort = userServicePort;
    }

    @GetMapping
    public ResponseEntity<PaginationResponseDto<List<UserRecordDto>>> index(
            @Valid @ParameterObject @ModelAttribute FilterCriteriaParamDTO parameters
    ) throws IOException {
        return new ResponseEntity<>(userServicePort.getAll(
                parameters.getPage(),
                parameters.getPageSize(),
                parameters.getSearchTerm(),
                parameters.getSortField()
        ), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<UserRecordDto>> show(@PathVariable UUID id) {
        return new ResponseEntity<>(userServicePort.getUserById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseDto<UserRecordDto>> store(
            @RequestBody @Valid UserInputRecordDto userInputRecordDto
    ) {
        return new ResponseEntity<>(userServicePort.createUser(userInputRecordDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<UserRecordDto>> update(
            @PathVariable UUID id,
            @RequestBody @Valid UserInputRecordDto userInputRecordDto
    ) {
        return new ResponseEntity<>(userServicePort.updateUser(id, userInputRecordDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<UserRecordDto>> destroy(@PathVariable UUID id) {
        return new ResponseEntity<>(userServicePort.deleteUser(id), HttpStatus.OK);
    }
}
