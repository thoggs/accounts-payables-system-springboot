package com.codesumn.accounts_payables_system_springboot.application.config;

import com.codesumn.accounts_payables_system_springboot.application.adapters.inbound.AccountItemWriter;
import com.codesumn.accounts_payables_system_springboot.domain.models.AccountModel;
import com.codesumn.accounts_payables_system_springboot.shared.enums.AccountStatusEnum;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class ImportAccountsJobConfig {

    @Bean
    public JobRepository jobRepository(
            DataSource dataSource,
            PlatformTransactionManager transactionManager
    ) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public Job importAccountsJob(
            JobRepository jobRepository,
            @Qualifier("importAccountsStep") Step importAccountsStep
    ) {
        return new JobBuilder("importAccountsJob", jobRepository)
                .start(importAccountsStep)
                .build();
    }

    @Bean
    public Step importAccountsStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<AccountModel> accountItemReader,
            AccountItemWriter accountItemWriter
    ) {
        return new StepBuilder("importAccountsStep", jobRepository)
                .<AccountModel, AccountModel>chunk(10, transactionManager)
                .reader(accountItemReader)
                .writer(accountItemWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<AccountModel> accountItemReader() {
        FlatFileItemReader<AccountModel> reader = new FlatFileItemReader<>();

        DefaultLineMapper<AccountModel> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        lineTokenizer.setNames("dueDate", "paymentDate", "amount", "description", "status");

        lineMapper.setLineTokenizer(lineTokenizer);

        lineMapper.setFieldSetMapper(fieldSet -> {
            AccountModel account = new AccountModel();
            account.setDueDate(fieldSet.readDate("dueDate"));
            account.setPaymentDate(fieldSet.readDate("paymentDate"));
            account.setAmount(fieldSet.readBigDecimal("amount"));
            account.setDescription(fieldSet.readString("description"));
            account.setStatus(AccountStatusEnum.fromValue(fieldSet.readString("status")));
            return account;
        });

        reader.setLineMapper(lineMapper);

        return reader;
    }
}
