package com.codesumn.accounts_payables_system_springboot.application.adapters.inbound;

import com.codesumn.accounts_payables_system_springboot.application.dtos.records.pagination.PaginationResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.response.ResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserInputRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserRecordDto;
import com.codesumn.accounts_payables_system_springboot.domain.models.UserModel;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.UserPersistencePort;
import com.codesumn.accounts_payables_system_springboot.shared.enums.RolesEnum;
import com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors.EmailAlreadyExistsException;
import com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors.ResourceNotFoundException;
import com.codesumn.accounts_payables_system_springboot.shared.parsers.SortParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceAdapterTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SortParser sortParser;

    private UserServiceAdapter userServiceAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userServiceAdapter = new UserServiceAdapter(
                userPersistencePort,
                passwordEncoder,
                sortParser
        );
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        UUID userId = UUID.randomUUID();
        UserModel userModel = new UserModel();
        userModel.setId(userId);
        userModel.setFirstName("Jane");
        userModel.setLastName("Doe");

        when(userPersistencePort.findById(userId)).thenReturn(Optional.of(userModel));

        ResponseDto<UserRecordDto> response = userServiceAdapter.getUserById(userId);

        assertThat(response.data().id()).isEqualTo(userId);
        assertThat(response.data().firstName()).isEqualTo("Jane");
        verify(userPersistencePort).findById(userId);
    }

    @Test
    void getUserById_shouldThrowResourceNotFound_whenNotFound() {
        UUID userId = UUID.randomUUID();
        when(userPersistencePort.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userServiceAdapter.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userPersistencePort).findById(userId);
    }

    @Test
    void createUser_shouldSaveAndReturnNewUser() {
        UserInputRecordDto inputDto = new UserInputRecordDto(
                "John",
                "Doe",
                "john.doe@example.com",
                "secretPass",
                "ADMIN"
        );

        when(userPersistencePort.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("secretPass"))
                .thenReturn("encodedSecretPass");

        ResponseDto<UserRecordDto> response = userServiceAdapter.createUser(inputDto);

        assertThat(response.data()).isNotNull();
        assertThat(response.data().firstName()).isEqualTo("John");
        assertThat(response.data().lastName()).isEqualTo("Doe");
        assertThat(response.data().email()).isEqualTo("john.doe@example.com");
        assertThat(response.data().role()).isEqualTo(RolesEnum.ADMIN);

        verify(userPersistencePort).findByEmail("john.doe@example.com");
        verify(passwordEncoder).encode("secretPass");
        verify(userPersistencePort).saveUser(argThat(user ->
                user.getFirstName().equals("John") &&
                        user.getLastName().equals("Doe") &&
                        user.getEmail().equals("john.doe@example.com") &&
                        user.getPassword().equals("encodedSecretPass") &&
                        user.getRole() == RolesEnum.ADMIN
        ));
    }

    @Test
    void createUser_shouldThrowEmailAlreadyExists_whenEmailDuplicate() {
        UserInputRecordDto inputDto = new UserInputRecordDto(
                "Mary",
                "Doe",
                "mary@example.com",
                "secret",
                "USER"
        );

        UserModel existingUser = new UserModel();
        existingUser.setId(UUID.randomUUID());
        existingUser.setEmail("mary@example.com");

        when(userPersistencePort.findByEmail("mary@example.com"))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userServiceAdapter.createUser(inputDto))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userPersistencePort).findByEmail("mary@example.com");
        verifyNoMoreInteractions(userPersistencePort);
    }

    @Test
    void updateUser_shouldUpdateAndReturnUser() {
        UUID userId = UUID.randomUUID();

        UserModel existingUser = new UserModel();
        existingUser.setId(userId);
        existingUser.setFirstName("OldFirst");
        existingUser.setLastName("OldLast");
        existingUser.setEmail("oldmail@example.com");
        existingUser.setPassword("oldPass");
        existingUser.setRole(RolesEnum.USER);

        when(userPersistencePort.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(userPersistencePort.findByEmail("newmail@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("newPass"))
                .thenReturn("encodedNewPass");

        UserInputRecordDto dto = new UserInputRecordDto(
                "NewFirst",
                "NewLast",
                "newmail@example.com",
                "newPass",
                "ADMIN"
        );

        var response = userServiceAdapter.updateUser(userId, dto);

        assertThat(response.data().id()).isEqualTo(userId);
        assertThat(response.data().firstName()).isEqualTo("NewFirst");
        assertThat(response.data().lastName()).isEqualTo("NewLast");
        assertThat(response.data().email()).isEqualTo("newmail@example.com");
        assertThat(response.data().role()).isEqualTo(RolesEnum.ADMIN);


        verify(userPersistencePort).findById(userId);
        verify(userPersistencePort).findByEmail("newmail@example.com");
        verify(passwordEncoder).encode("newPass");

        verify(userPersistencePort).saveUser(argThat(user ->
                user.getId().equals(userId) &&
                        user.getFirstName().equals("NewFirst") &&
                        user.getLastName().equals("NewLast") &&
                        user.getEmail().equals("newmail@example.com") &&
                        user.getPassword().equals("encodedNewPass") &&
                        user.getRole() == RolesEnum.ADMIN
        ));
    }

    @Test
    void deleteUser_shouldRemoveUserAndReturnDeletedInfo() {
        UUID userId = UUID.randomUUID();
        UserModel existingUser = new UserModel();
        existingUser.setId(userId);
        existingUser.setFirstName("ToDelete");

        when(userPersistencePort.findById(userId))
                .thenReturn(Optional.of(existingUser));

        var response = userServiceAdapter.deleteUser(userId);

        assertThat(response.data().id()).isEqualTo(userId);
        assertThat(response.data().firstName()).isEqualTo("ToDelete");
        verify(userPersistencePort).deleteUser(existingUser);
    }

    @Test
    void getAll_shouldReturnPagination() throws IOException {
        int page = 1;
        int pageSize = 5;
        String searchTerm = "someTerm";
        String sorting = "sortingJSON";

        when(sortParser.parseSorting(sorting)).thenReturn(Sort.by("firstName"));

        UserModel u1 = new UserModel();
        u1.setId(UUID.randomUUID());
        u1.setFirstName("Alice");

        UserModel u2 = new UserModel();
        u2.setId(UUID.randomUUID());
        u2.setFirstName("Bob");

        Page<UserModel> userPage = new PageImpl<>(
                List.of(u1, u2),
                PageRequest.of(0, pageSize, Sort.by("firstName")),
                2
        );

        when(userPersistencePort.findAll(eq(searchTerm), any(Pageable.class)))
                .thenReturn(userPage);

        PaginationResponseDto<List<UserRecordDto>> result =
                userServiceAdapter.getAll(page, pageSize, searchTerm, sorting);

        assertThat(result.data()).hasSize(2);
        assertThat(result.data().getFirst().firstName()).isEqualTo("Alice");
        verify(sortParser).parseSorting(sorting);
        verify(userPersistencePort).findAll(eq(searchTerm), any(Pageable.class));
    }
}
