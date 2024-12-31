package com.codesumn.accounts_payables_system_springboot.domain.inbound;


import com.codesumn.accounts_payables_system_springboot.application.dtos.records.auth.AuthCredentialsRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.auth.AuthResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.github.GitHubTokenRequestRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.response.ResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserInputRecordDto;

public interface UserRegistrationPort {
    ResponseDto<AuthResponseDto> authenticateUser(AuthCredentialsRecordDto credentials);

    ResponseDto<AuthResponseDto> authenticateGitHubUser(GitHubTokenRequestRecordDto tokenRequest);

    ResponseDto<AuthResponseDto> registerUser(UserInputRecordDto user);
}
