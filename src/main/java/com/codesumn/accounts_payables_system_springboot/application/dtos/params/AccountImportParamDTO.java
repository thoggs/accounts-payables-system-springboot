package com.codesumn.accounts_payables_system_springboot.application.dtos.params;

import com.codesumn.accounts_payables_system_springboot.application.validators.account.ValidMultipartFile;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ValidMultipartFile(
        message = "Invalid file uploaded",
        allowedTypes = {"text/csv", "application/vnd.ms-excel"},
        maxSize = 5 * 1024 * 1024
)
public class AccountImportParamDTO {
    private MultipartFile file;
}
