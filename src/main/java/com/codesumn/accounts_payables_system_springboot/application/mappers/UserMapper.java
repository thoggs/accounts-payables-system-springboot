package com.codesumn.accounts_payables_system_springboot.application.mappers;

import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserInputRecordDto;
import com.codesumn.accounts_payables_system_springboot.domain.models.UserModel;
import com.codesumn.accounts_payables_system_springboot.shared.enums.RolesEnum;

public class UserMapper {
    public static UserModel fromDto(UserInputRecordDto dto) {
        UserModel user = new UserModel();
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(RolesEnum.fromValue(dto.role()));
        return user;
    }
}