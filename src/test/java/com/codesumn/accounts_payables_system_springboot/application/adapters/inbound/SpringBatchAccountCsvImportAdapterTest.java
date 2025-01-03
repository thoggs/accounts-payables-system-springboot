package com.codesumn.accounts_payables_system_springboot.application.adapters.inbound;

import com.codesumn.accounts_payables_system_springboot.domain.models.AccountModel;
import com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors.CsvImportException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpringBatchAccountCsvImportAdapterTest {


    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job importAccountsJob;

    @Mock
    private FlatFileItemReader<AccountModel> reader;

    private SpringBatchAccountCsvImportAdapter csvImportAdapter;

    @BeforeEach
    void setUp() {
        csvImportAdapter = new SpringBatchAccountCsvImportAdapter(jobLauncher, importAccountsJob, reader);
    }

    @Test
    void importCsv_shouldReturnImportedCount_whenJobIsSuccessful() throws Exception {

        InputStream mockInputStream = new ByteArrayInputStream("mock CSV content".getBytes());
        InputStreamResource resource = new InputStreamResource(mockInputStream);

        JobExecution mockJobExecution = mock(JobExecution.class);
        when(mockJobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        ExecutionContext mockExecutionContext = new ExecutionContext();
        mockExecutionContext.putInt("importedCount", 10);
        when(mockJobExecution.getExecutionContext()).thenReturn(mockExecutionContext);

        doNothing().when(reader).setResource(resource);

        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(mockJobExecution);

        int importedCount = csvImportAdapter.importCsv(mockInputStream);

        assertThat(importedCount).isEqualTo(10);
    }

    @Test
    void importCsv_shouldThrowCsvImportException_whenJobFails() throws Exception {
        InputStream mockInputStream = new ByteArrayInputStream("mock CSV content".getBytes());
        InputStreamResource resource = new InputStreamResource(mockInputStream);

        JobExecution mockJobExecution = mock(JobExecution.class);
        when(mockJobExecution.getStatus()).thenReturn(BatchStatus.FAILED);
        when(mockJobExecution.getAllFailureExceptions())
                .thenReturn(List.of(new IllegalArgumentException("Invalid data")));

        doNothing().when(reader).setResource(resource);

        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(mockJobExecution);

        CsvImportException exception = assertThrows(
                CsvImportException.class,
                () -> csvImportAdapter.importCsv(mockInputStream)
        );

        assertThat(exception.getMessage()).contains("Invalid data");
    }

    @Test
    void importCsv_shouldThrowCsvImportException_whenParseErrorOccurs() {
        InputStream mockInputStream = new ByteArrayInputStream("mock CSV content".getBytes());
        InputStreamResource resource = new InputStreamResource(mockInputStream);

        IncorrectTokenCountException parseException = new IncorrectTokenCountException(5, 4);

        doThrow(parseException).when(reader).setResource(resource);

        CsvImportException exception = assertThrows(
                CsvImportException.class,
                () -> csvImportAdapter.importCsv(mockInputStream)
        );

        assertThat(exception.getMessage()).contains("CSV parse error");
    }

    @Test
    void importCsv_shouldThrowRuntimeException_whenUnexpectedErrorOccurs() {
        InputStream mockInputStream = new ByteArrayInputStream("mock CSV content".getBytes());
        InputStreamResource resource = new InputStreamResource(mockInputStream);

        doThrow(new RuntimeException("Unexpected error")).when(reader).setResource(resource);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> csvImportAdapter.importCsv(mockInputStream)
        );

        assertThat(exception.getMessage()).contains("Error importing CSV via Spring Batch");
    }
}
