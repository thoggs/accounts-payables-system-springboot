package com.codesumn.accounts_payables_system_springboot.application.mappers;

import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserInputRecordDto;
import com.codesumn.accounts_payables_system_springboot.domain.models.UserModel;
import com.codesumn.accounts_payables_system_springboot.shared.enums.RolesEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void fromDto_shouldMapDtoToUserModel() {

        UserInputRecordDto dto = new UserInputRecordDto(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                "USER"
        );

        UserModel user = UserMapper.fromDto(dto);

        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getRole()).isEqualTo(RolesEnum.USER);
    }
}
