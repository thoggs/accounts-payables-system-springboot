package com.codesumn.accounts_payables_system_springboot.application.adapters.inbound;

import com.codesumn.accounts_payables_system_springboot.application.dtos.github.GitHubUserDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.auth.AuthCredentialsRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.auth.AuthResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.github.GitHubTokenRequestRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.response.ResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserInputRecordDto;
import com.codesumn.accounts_payables_system_springboot.domain.models.UserModel;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.*;
import com.codesumn.accounts_payables_system_springboot.shared.enums.RolesEnum;
import com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors.CustomUserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceAdapterTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtServicePort jwtServicePort;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private GitHubServicePort gitHubServicePort;

    @InjectMocks
    private AuthServiceAdapter authServiceAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticateUser_shouldReturnAuthResponse_whenCredentialsAreValid() {
        // Arrange
        AuthCredentialsRecordDto credentials = new AuthCredentialsRecordDto("user@example.com", "password");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        UserModel userModel = new UserModel();
        userModel.setEmail("user@example.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(userPersistencePort.findByEmail("user@example.com")).thenReturn(Optional.of(userModel));
        when(jwtServicePort.generateToken("user@example.com")).thenReturn("jwt-token");

        ResponseDto<AuthResponseDto> response = authServiceAdapter.authenticateUser(credentials);

        assertThat(response.data().accessToken()).isEqualTo("jwt-token");
        assertThat(response.data().user().email()).isEqualTo("user@example.com");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtServicePort).generateToken("user@example.com");
    }

    @Test
    void authenticateUser_shouldThrowCustomUserNotFoundException_whenUserDoesNotExist() {
        AuthCredentialsRecordDto credentials = new AuthCredentialsRecordDto("nonexistent@example.com", "password");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("nonexistent@example.com");
        when(userPersistencePort.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(CustomUserNotFoundException.class, () -> authServiceAdapter.authenticateUser(credentials));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userPersistencePort).findByEmail("nonexistent@example.com");
    }

    @Test
    void authenticateGitHubUser_shouldAuthenticateAndReturnToken() {
        GitHubTokenRequestRecordDto tokenRequest = new GitHubTokenRequestRecordDto("github-token");

        GitHubUserDto gitHubUser = new GitHubUserDto();
        gitHubUser.setName("John Doe");
        gitHubUser.setEmail("john.doe@example.com");

        UserModel savedUser = new UserModel();
        savedUser.setId(UUID.randomUUID());
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setEmail("john.doe@example.com");
        savedUser.setPassword("encodedGithubToken");
        savedUser.setRole(RolesEnum.USER);

        when(gitHubServicePort.getGitHubUser("github-token")).thenReturn(gitHubUser);
        when(userPersistencePort.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("github-token")).thenReturn("encodedGithubToken");
        doNothing().when(userPersistencePort).saveUser(any(UserModel.class));
        when(jwtServicePort.generateToken("john.doe@example.com")).thenReturn("jwt-token");

        ResponseDto<AuthResponseDto> response = authServiceAdapter.authenticateGitHubUser(tokenRequest);

        assertThat(response.data().accessToken()).isEqualTo("jwt-token");
        assertThat(response.data().user().email()).isEqualTo("john.doe@example.com");
        verify(gitHubServicePort).getGitHubUser("github-token");
        verify(userPersistencePort).existsByEmail("john.doe@example.com");
        verify(passwordEncoder).encode("github-token");
        verify(userPersistencePort).saveUser(any(UserModel.class));
        verify(jwtServicePort).generateToken("john.doe@example.com");
    }

    @Test
    void registerUser_shouldRegisterAndReturnToken() {

        UserInputRecordDto userInput = new UserInputRecordDto(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                "USER"
        );

        UserModel newUser = new UserModel();
        newUser.setId(UUID.randomUUID());
        newUser.setFirstName("John");
        newUser.setLastName("Doe");
        newUser.setEmail("john.doe@example.com");
        newUser.setPassword("encodedPassword");
        newUser.setRole(RolesEnum.USER);

        UserDetails userDetails = mock(UserDetails.class);

        when(userPersistencePort.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        doNothing().when(userPersistencePort).saveUser(any(UserModel.class));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, "encodedPassword"));
        when(userDetails.getUsername()).thenReturn("john.doe@example.com");
        when(jwtServicePort.generateToken("john.doe@example.com")).thenReturn("jwt-token");

        ResponseDto<AuthResponseDto> response = authServiceAdapter.registerUser(userInput);

        assertThat(response.data().accessToken()).isEqualTo("jwt-token");
        assertThat(response.data().user().email()).isEqualTo("john.doe@example.com");

        verify(userPersistencePort).findByEmail("john.doe@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userPersistencePort).saveUser(any(UserModel.class));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtServicePort).generateToken("john.doe@example.com");
    }
}