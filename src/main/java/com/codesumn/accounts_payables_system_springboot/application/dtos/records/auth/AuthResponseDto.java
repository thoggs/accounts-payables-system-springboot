package com.codesumn.accounts_payables_system_springboot.application.dtos.records.auth;

import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.AuthUserResponseRecordDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponseDto(
        @JsonProperty("user") AuthUserResponseRecordDto user,
        @JsonProperty("accessToken") String accessToken) {

    @JsonCreator
    public AuthResponseDto {}
}
