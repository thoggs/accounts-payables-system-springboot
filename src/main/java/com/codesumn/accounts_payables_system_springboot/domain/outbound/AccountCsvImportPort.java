package com.codesumn.accounts_payables_system_springboot.domain.outbound;

import java.io.InputStream;

public interface AccountCsvImportPort {
    int importCsv(InputStream csvContent);
}