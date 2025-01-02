package com.codesumn.accounts_payables_system_springboot.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountStatusEnum {
    PENDING("pending"),
    PAID("paid");

    private final String value;

    public static AccountStatusEnum fromValue(String value) {
        for (AccountStatusEnum status : AccountStatusEnum.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid account status: " + value);
    }
}