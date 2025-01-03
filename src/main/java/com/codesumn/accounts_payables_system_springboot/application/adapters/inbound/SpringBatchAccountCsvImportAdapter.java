package com.codesumn.accounts_payables_system_springboot.application.adapters.inbound;

import com.codesumn.accounts_payables_system_springboot.domain.models.AccountModel;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.AccountCsvImportPort;
import com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors.CsvImportException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class SpringBatchAccountCsvImportAdapter implements AccountCsvImportPort {

    private final JobLauncher jobLauncher;
    private final Job importAccountsJob;
    private final FlatFileItemReader<AccountModel> reader;

    @Autowired
    public SpringBatchAccountCsvImportAdapter(
            JobLauncher jobLauncher,
            Job importAccountsJob,
            FlatFileItemReader<AccountModel> reader
    ) {
        this.jobLauncher = jobLauncher;
        this.importAccountsJob = importAccountsJob;
        this.reader = reader;
    }

    @Override
    public int importCsv(InputStream csvContent) {
        try {
            InputStreamResource resource = new InputStreamResource(csvContent);
            reader.setResource(resource);

            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis());

            JobExecution jobExecution = jobLauncher.run(importAccountsJob, jobParametersBuilder.toJobParameters());

            if (jobExecution.getStatus().isUnsuccessful()) {
                List<Throwable> failures = getThrowableList(jobExecution);

                var msgs = failures.stream().map(Throwable::getMessage).toList();
                throw new CsvImportException("CSV import failed with errors", msgs);
            }

            return jobExecution.getExecutionContext().getInt("importedCount", 0);

        } catch (IncorrectTokenCountException e) {
            throw new CsvImportException("CSV parse error", List.of(e.getMessage()));
        } catch (IllegalArgumentException e) {
            throw new CsvImportException("Invalid data error", List.of(e.getMessage()));
        } catch (CsvImportException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar CSV via Spring Batch", e);
        }
    }

    private List<Throwable> getThrowableList(JobExecution jobExecution) {
        List<Throwable> failures = jobExecution.getAllFailureExceptions();

        for (Throwable t : failures) {
            if (t instanceof IncorrectTokenCountException ictEx) {
                throw ictEx;
            }
            if (t instanceof IllegalArgumentException argEx) {
                throw argEx;
            }
        }
        return failures;
    }
}
