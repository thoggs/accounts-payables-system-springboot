package com.codesumn.accounts_payables_system_springboot.domain.outbound;

import com.codesumn.accounts_payables_system_springboot.application.dtos.records.account.AccountInputRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.account.AccountRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.pagination.PaginationResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.response.ResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AccountServicePort {
    PaginationResponseDto<List<AccountRecordDto>> getAll(
            int page,
            int pageSize,
            String searchTerm,
            String sorting,
            LocalDate startDate,
            LocalDate endDate
    ) throws IOException;

    ResponseDto<AccountRecordDto> getAccountById(UUID id);

    ResponseDto<AccountRecordDto> createAccount(AccountInputRecordDto accountInput);

    ResponseDto<AccountRecordDto> updateAccount(UUID id, AccountInputRecordDto accountInput);

    ResponseDto<AccountRecordDto> deleteAccount(UUID id);

    ResponseDto<AccountRecordDto> updateAccountStatus(UUID id, String status);

    ResponseDto<BigDecimal> getTotalPaid(String startDate, String endDate);

    ResponseDto<Integer> importAccounts(MultipartFile file) throws IOException;
}
