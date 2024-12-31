package com.codesumn.accounts_payables_system_springboot.domain.outbound;

import com.codesumn.accounts_payables_system_springboot.application.dtos.github.GitHubUserDto;

public interface GitHubServicePort {
    GitHubUserDto getGitHubUser(String token);
}
