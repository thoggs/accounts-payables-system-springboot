package com.codesumn.accounts_payables_system_springboot.application.adapters.inbound;

import com.codesumn.accounts_payables_system_springboot.application.dtos.github.GitHubUserDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.auth.AuthCredentialsRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.auth.AuthResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.github.GitHubTokenRequestRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.response.ResponseDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.AuthUserResponseRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.dtos.records.user.UserInputRecordDto;
import com.codesumn.accounts_payables_system_springboot.application.mappers.UserMapper;
import com.codesumn.accounts_payables_system_springboot.domain.inbound.UserRegistrationPort;
import com.codesumn.accounts_payables_system_springboot.domain.models.UserModel;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.GitHubServicePort;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.JwtServicePort;
import com.codesumn.accounts_payables_system_springboot.domain.outbound.UserPersistencePort;
import com.codesumn.accounts_payables_system_springboot.shared.enums.RolesEnum;
import com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors.CustomUserNotFoundException;
import com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors.EmailAlreadyExistsException;
import com.codesumn.accounts_payables_system_springboot.shared.exceptions.errors.ResourceNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceAdapter implements UserRegistrationPort {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoder passwordEncoder;
    private final JwtServicePort jwtServicePort;
    private final AuthenticationManager authenticationManager;
    private final GitHubServicePort gitHubServicePort;

    public AuthServiceAdapter(
            UserPersistencePort userPersistencePort,
            PasswordEncoder passwordEncoder,
            JwtServicePort jwtServicePort,
            AuthenticationManager authenticationManager,
            GitHubServicePort gitHubServicePort
    ) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoder = passwordEncoder;
        this.jwtServicePort = jwtServicePort;
        this.authenticationManager = authenticationManager;
        this.gitHubServicePort = gitHubServicePort;
    }

    @Override
    public ResponseDto<AuthResponseDto> authenticateUser(AuthCredentialsRecordDto credentials) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.email(), credentials.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserModel user = userPersistencePort.findByEmail(userDetails.getUsername())
                .orElseThrow(CustomUserNotFoundException::new);
        String token = jwtServicePort.generateToken(userDetails.getUsername());

        AuthUserResponseRecordDto userData = new AuthUserResponseRecordDto(user);
        AuthResponseDto authResponse = new AuthResponseDto(userData, token);

        return ResponseDto.create(authResponse);
    }

    @Override
    public ResponseDto<AuthResponseDto> authenticateGitHubUser(GitHubTokenRequestRecordDto tokenRequest) {
        GitHubUserDto gitHubUser = gitHubServicePort.getGitHubUser(tokenRequest.githubToken());

        UserModel user = new UserModel();

        if (userPersistencePort.existsByEmail(gitHubUser.getEmail())) {
            user = userPersistencePort.findByEmail(gitHubUser.getEmail()).orElseThrow(ResourceNotFoundException::new);
        } else {
            user.setFirstName(gitHubUser.getName());
            user.setLastName("");
            user.setEmail(gitHubUser.getEmail());
            user.setPassword(passwordEncoder.encode(tokenRequest.githubToken()));
            user.setRole(RolesEnum.USER);

            userPersistencePort.saveUser(user);
        }

        String token = jwtServicePort.generateToken(user.getEmail());

        AuthUserResponseRecordDto userData = new AuthUserResponseRecordDto(user);
        AuthResponseDto authResponse = new AuthResponseDto(userData, token);

        return ResponseDto.create(authResponse);
    }

    @Override
    public ResponseDto<AuthResponseDto> registerUser(UserInputRecordDto userInput) {
        if (userPersistencePort.findByEmail(userInput.email()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        UserModel newUser = UserMapper.fromDto(userInput);

        newUser.setPassword(passwordEncoder.encode(userInput.password()));

        userPersistencePort.saveUser(newUser);

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userInput.email(), userInput.password()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtServicePort.generateToken(userDetails.getUsername());

        AuthUserResponseRecordDto userData = new AuthUserResponseRecordDto(newUser);
        AuthResponseDto authResponse = new AuthResponseDto(userData, token);

        return ResponseDto.create(authResponse);
    }
}
