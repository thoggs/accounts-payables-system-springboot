package com.codesumn.accounts_payables_system_springboot.domain.inbound;

import com.codesumn.accounts_payables_system_springboot.application.dtos.records.pagination.PaginationResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.response.ResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserInputRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserRecordDto;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserServicePort {
    PaginationResponseDto<List<UserRecordDto>> getAll(
            int page,
            int pageSize,
            String searchTerm,
            String sorting
    ) throws IOException;

    ResponseDto<UserRecordDto> getUserById(UUID id);

    ResponseDto<UserRecordDto> createUser(UserInputRecordDto userInput);

    ResponseDto<UserRecordDto> updateUser(UUID id, UserInputRecordDto userInput);

    ResponseDto<UserRecordDto> deleteUser(UUID id);
}
