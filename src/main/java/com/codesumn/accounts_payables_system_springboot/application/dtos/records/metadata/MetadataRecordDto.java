package com.codesumn.accounts_payables_system_springboot.application.dtos.records.metadata;

import com.codesumn.accounts_payables_system_springboot.application.dtos.errors.ErrorMessageDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public record MetadataRecordDto(@JsonProperty("messages") List<ErrorMessageDto> messages) implements Serializable {

    @JsonCreator
    public MetadataRecordDto {
    }
}
