package com.codesumn.accounts_payables_system_springboot.application.controllers;

import com.codesumn.accounts_payables_system_springboot.application.dtos.records.auth.AuthCredentialsRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.auth.AuthResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.github.GitHubTokenRequestRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.response.ResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserInputRecordDto;
import com.codesumn.accounts_payables_system_springboot.domain.inbound.UserRegistrationPort;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRegistrationPort userRegistrationPort;

    @Autowired
    public AuthController(UserRegistrationPort userRegistrationPort) {
        this.userRegistrationPort = userRegistrationPort;
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseDto<AuthResponseDto>> authenticateUser(
            @RequestBody @Valid AuthCredentialsRecordDto credentials
    ) {
        return new ResponseEntity<>(userRegistrationPort.authenticateUser(credentials), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<AuthResponseDto>> registerUser(
            @RequestBody @Valid UserInputRecordDto user
    ) {
        return new ResponseEntity<>(userRegistrationPort.registerUser(user), HttpStatus.CREATED);
    }

    @PostMapping("/github-signin")
    public ResponseEntity<ResponseDto<AuthResponseDto>> authenticateGitHubUser(
            @RequestBody @Valid GitHubTokenRequestRecordDto tokenRequest
    ) {
        return new ResponseEntity<>(userRegistrationPort.authenticateGitHubUser(tokenRequest), HttpStatus.OK);
    }
}
