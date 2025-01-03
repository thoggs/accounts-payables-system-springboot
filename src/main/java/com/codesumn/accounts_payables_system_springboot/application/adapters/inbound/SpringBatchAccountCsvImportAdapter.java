package com.codesumn.accounts_payables_system_springboot.application.adapters.inbound;

import com.codesumn.accounts_payables_system_springboot.domain.models.AccountModel;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.AccountCsvImportPort;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

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

            JobExecution jobExecution = jobLauncher.run(
                    importAccountsJob,
                    jobParametersBuilder.toJobParameters()
            );

            return jobExecution.getExecutionContext().getInt("importedCount", 0);
        } catch (Exception e) {
            throw new RuntimeException("Error in import Spring Batch", e);
        }
    }
}
