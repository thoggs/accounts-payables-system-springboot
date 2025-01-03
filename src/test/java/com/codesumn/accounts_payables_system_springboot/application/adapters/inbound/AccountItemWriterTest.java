package com.codesumn.accounts_payables_system_springboot.application.adapters.inbound;

import com.codesumn.accounts_payables_system_springboot.domain.models.AccountModel;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.AccountPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AccountItemWriterTest {

    @Mock
    private AccountPersistencePort accountPersistencePort;

    @Mock
    private StepExecution stepExecution;

    @InjectMocks
    private AccountItemWriter accountItemWriter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void beforeStep_shouldResetSavedCount() {

        JobExecution mockJobExecution = mock(JobExecution.class);
        ExecutionContext mockExecutionContext = new ExecutionContext();

        when(stepExecution.getJobExecution()).thenReturn(mockJobExecution);
        when(mockJobExecution.getExecutionContext()).thenReturn(mockExecutionContext);

        accountItemWriter.beforeStep(stepExecution);
        ExitStatus exitStatus = accountItemWriter.afterStep(stepExecution);

        assert exitStatus != null;
        assertThat(exitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
        assertThat(mockExecutionContext.getInt("importedCount", -1)).isEqualTo(0);
    }

    @Test
    void write_shouldSaveAccountsAndIncrementSavedCount() {

        AccountModel account1 = new AccountModel();
        account1.setId(UUID.randomUUID());
        account1.setAmount(BigDecimal.valueOf(100));
        account1.setDescription("Account 1");

        AccountModel account2 = new AccountModel();
        account2.setId(UUID.randomUUID());
        account2.setAmount(BigDecimal.valueOf(200));
        account2.setDescription("Account 2");

        List<AccountModel> accounts = Arrays.asList(account1, account2);
        Chunk<AccountModel> chunk = new Chunk<>(accounts);

        accountItemWriter.write(chunk);

        ArgumentCaptor<AccountModel> captor = ArgumentCaptor.forClass(AccountModel.class);
        verify(accountPersistencePort, times(2)).saveAccount(captor.capture());

        List<AccountModel> capturedAccounts = captor.getAllValues();
        assertThat(capturedAccounts).containsExactly(account1, account2);
    }

    @Test
    void afterStep_shouldSetImportedCountInExecutionContext() {

        AccountModel account1 = new AccountModel();
        account1.setId(UUID.randomUUID());
        account1.setAmount(BigDecimal.valueOf(100));
        account1.setDescription("Account 1");

        AccountModel account2 = new AccountModel();
        account2.setId(UUID.randomUUID());
        account2.setAmount(BigDecimal.valueOf(200));
        account2.setDescription("Account 2");

        List<AccountModel> accounts = Arrays.asList(account1, account2);
        Chunk<AccountModel> chunk = new Chunk<>(accounts);

        accountItemWriter.beforeStep(stepExecution);
        accountItemWriter.write(chunk);

        JobExecution mockJobExecution = mock(JobExecution.class);
        ExecutionContext mockExecutionContext = new ExecutionContext();
        when(stepExecution.getJobExecution()).thenReturn(mockJobExecution);
        when(mockJobExecution.getExecutionContext()).thenReturn(mockExecutionContext);

        ExitStatus exitStatus = accountItemWriter.afterStep(stepExecution);

        assertThat(exitStatus).isEqualTo(ExitStatus.COMPLETED);
        assertThat(mockExecutionContext.getInt("importedCount")).isEqualTo(2);
    }
}
