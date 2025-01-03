package com.codesumn.accounts_payables_system_springboot.application.adapters.inbound;

import com.codesumn.accounts_payables_system_springboot.domain.models.AccountModel;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.AccountPersistencePort;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AccountItemWriter implements ItemWriter<AccountModel>, StepExecutionListener {

    private final AccountPersistencePort accountPersistencePort;
    private int savedCount = 0;

    @Autowired
    public AccountItemWriter(AccountPersistencePort accountPersistencePort) {
        this.accountPersistencePort = accountPersistencePort;
    }

    @Override
    public void beforeStep(@NonNull StepExecution stepExecution) {
        savedCount = 0;
    }

    @Override
    public void write(Chunk<? extends AccountModel> chunk) {
        for (AccountModel account : chunk) {
            accountPersistencePort.saveAccount(account);
            savedCount++;
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution.getJobExecution()
                .getExecutionContext()
                .put("importedCount", savedCount);
        return ExitStatus.COMPLETED;
    }
}