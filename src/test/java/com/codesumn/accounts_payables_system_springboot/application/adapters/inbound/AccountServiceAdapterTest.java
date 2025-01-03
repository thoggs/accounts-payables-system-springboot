package com.codesumn.accounts_payables_system_springboot.application.adapters.inbound;

import com.codesumn.accounts_payables_system_springboot.application.dtos.records.account.AccountInputRecordDto;
import com.codesumn.accounts_payables_system_springboot.domain.models.AccountModel;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.AccountCsvImportPort;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.AccountPersistencePort;
import com.codesumn.accounts_payables_system_springboot.shared.enums.AccountStatusEnum;
import com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors.ResourceNotFoundException;
import com.codesumn.accounts_payables_system_springboot.shared.parsers.SortParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceAdapterTest {

    @Mock
    private AccountPersistencePort accountPersistencePort;

    @Mock
    private SortParser sortParser;

    @Mock
    private AccountCsvImportPort accountCsvImportPort;

    private AccountServiceAdapter accountServiceAdapter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        accountServiceAdapter = new AccountServiceAdapter(
                accountPersistencePort,
                sortParser,
                accountCsvImportPort
        );
    }

    @Test
    void getAccountById_shouldReturnAccount_whenFound() {
        UUID accountId = UUID.randomUUID();
        AccountModel model = new AccountModel();
        model.setId(accountId);
        model.setStatus(AccountStatusEnum.PENDING);

        when(accountPersistencePort.findById(accountId))
                .thenReturn(Optional.of(model));

        var response = accountServiceAdapter.getAccountById(accountId);

        assertThat(response.data().status()).isEqualTo(AccountStatusEnum.PENDING);
        assertThat(response.data().id()).isEqualTo(accountId);

        verify(accountPersistencePort).findById(accountId);
        verifyNoMoreInteractions(accountPersistencePort);
    }

    @Test
    void getAccountById_shouldThrow_whenNotFound() {
        UUID accountId = UUID.randomUUID();
        when(accountPersistencePort.findById(accountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountServiceAdapter.getAccountById(accountId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(accountPersistencePort).findById(accountId);
    }

    @Test
    void createAccount_shouldSaveAndReturnCreatedAccount() {
        LocalDate ld = LocalDate.of(2023, 1, 10);
        Date date = localDateToDate(ld);

        AccountInputRecordDto dto = new AccountInputRecordDto(
                date,
                date,
                new BigDecimal("1000.00"),
                "Test Create",
                AccountStatusEnum.PENDING
        );

        AccountModel savedModel = new AccountModel();
        savedModel.setId(UUID.randomUUID());
        savedModel.setDueDate(dto.dueDate());
        savedModel.setPaymentDate(dto.paymentDate());
        savedModel.setAmount(dto.amount());
        savedModel.setDescription(dto.description());
        savedModel.setStatus(dto.status());

        when(accountPersistencePort.saveAccount(any(AccountModel.class)))
                .thenReturn(savedModel);

        var result = accountServiceAdapter.createAccount(dto);

        assertThat(result.data().id()).isNull();
        assertThat(result.data().description()).isEqualTo("Test Create");
        verify(accountPersistencePort).saveAccount(any(AccountModel.class));
    }

    @Test
    void updateAccount_shouldUpdateAndReturn() {
        LocalDate ld = LocalDate.of(2023, 1, 10);
        Date date = localDateToDate(ld);

        UUID existingId = UUID.randomUUID();

        AccountModel existingModel = new AccountModel();
        existingModel.setId(existingId);
        existingModel.setDescription("Old Description");

        when(accountPersistencePort.findById(existingId))
                .thenReturn(Optional.of(existingModel));

        AccountInputRecordDto dto = new AccountInputRecordDto(
                date,
                date,
                new BigDecimal("555.55"),
                "New Desc",
                AccountStatusEnum.PAID
        );

        AccountModel updatedModel = new AccountModel();
        updatedModel.setId(existingId);
        updatedModel.setDescription("New Desc");
        updatedModel.setStatus(AccountStatusEnum.PAID);

        when(accountPersistencePort.saveAccount(any(AccountModel.class)))
                .thenReturn(updatedModel);

        var response = accountServiceAdapter.updateAccount(existingId, dto);

        assertThat(response.data().id()).isEqualTo(existingId);
        assertThat(response.data().description()).isEqualTo("New Desc");
        assertThat(response.data().status()).isEqualTo(AccountStatusEnum.PAID);

        verify(accountPersistencePort).findById(existingId);
        verify(accountPersistencePort).saveAccount(any(AccountModel.class));
    }

    @Test
    void getAll_shouldReturnPaginationResponse() throws IOException {
        int page = 1;
        int pageSize = 2;
        String searchTerm = "test";
        String sorting = "someSortingJSON";
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        when(sortParser.parseSorting(sorting)).thenReturn(Sort.by("dueDate"));

        AccountModel m1 = new AccountModel();
        m1.setId(UUID.randomUUID());
        m1.setDescription("Desc1");
        m1.setDueDate(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        AccountModel m2 = new AccountModel();
        m2.setId(UUID.randomUUID());
        m2.setDescription("Desc2");
        m2.setDueDate(Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Page<AccountModel> mockedPage = new PageImpl<>(
                List.of(m1, m2),
                PageRequest.of(0, pageSize, Sort.by("dueDate")),
                2
        );

        when(accountPersistencePort.findAll(eq(searchTerm), eq(startDate), eq(endDate), any(Pageable.class)))
                .thenReturn(mockedPage);

        var result = accountServiceAdapter.getAll(page, pageSize, searchTerm, sorting, startDate, endDate);

        assertThat(result.data()).hasSize(2);
        assertThat(result.data().get(0).description()).isEqualTo("Desc1");
        assertThat(result.data().get(1).description()).isEqualTo("Desc2");

        assertThat(result.metadata().pagination().totalPages()).isEqualTo(1L);

        verify(sortParser).parseSorting(sorting);
        verify(accountPersistencePort).findAll(eq(searchTerm), eq(startDate), eq(endDate), any(Pageable.class));
    }

    @Test
    void deleteAccount_shouldDelete() {
        UUID accId = UUID.randomUUID();

        AccountModel model = new AccountModel();
        model.setId(accId);
        model.setDescription("To Delete");

        when(accountPersistencePort.findById(accId)).thenReturn(Optional.of(model));

        var response = accountServiceAdapter.deleteAccount(accId);

        assertThat(response.data().id()).isEqualTo(accId);
        assertThat(response.data().description()).isEqualTo("To Delete");
        verify(accountPersistencePort).deleteAccount(model);
    }

    @Test
    void updateAccountStatus_shouldSetNewStatus() {
        UUID accId = UUID.randomUUID();
        AccountModel model = new AccountModel();
        model.setId(accId);
        model.setStatus(AccountStatusEnum.PENDING);

        when(accountPersistencePort.findById(accId)).thenReturn(Optional.of(model));

        var result = accountServiceAdapter.updateAccountStatus(accId, "PAID");

        assertThat(result.data().status()).isEqualTo(AccountStatusEnum.PAID);
        verify(accountPersistencePort).saveAccount(model);
    }

    @Test
    void getTotalPaid_shouldReturnValue() {
        when(accountPersistencePort.calculateTotalPaid(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("1234.56"));

        var result = accountServiceAdapter.getTotalPaid("2023-01-01", "2023-02-01");

        assertThat(result.data()).isEqualTo("1234.56");
        verify(accountPersistencePort).calculateTotalPaid(eq(LocalDate.of(2023, 1, 1)), eq(LocalDate.of(2023, 2, 1)));
    }

    @Test
    void importAccounts_shouldCallCsvImportPort() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(mock(InputStream.class));
        when(accountCsvImportPort.importCsv(any(InputStream.class))).thenReturn(3);

        var result = accountServiceAdapter.importAccounts(file);

        assertThat(result.data()).isEqualTo(3);
        verify(accountCsvImportPort).importCsv(any(InputStream.class));
    }

    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
    }
}